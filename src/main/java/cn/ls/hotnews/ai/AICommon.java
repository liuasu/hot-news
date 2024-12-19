package cn.ls.hotnews.ai;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.enums.AIPlatFormEnum;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.entity.*;
import cn.ls.hotnews.model.vo.ArticleVO;
import cn.ls.hotnews.model.vo.HotNewsVO;
import cn.ls.hotnews.service.AiArticleCreationLogService;
import cn.ls.hotnews.service.AiConfigService;
import cn.ls.hotnews.service.PromptService;
import cn.ls.hotnews.strategy.ChromeDriverStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * title: AICommon
 * author: liaoshuo
 * date: 2024/12/7 21:04
 * description:
 */
@Slf4j
@Service
public class AICommon {


    private static final AtomicBoolean running = new AtomicBoolean(false); // 控制线程的开关
    private final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    /**
     * 监控最新资讯map
     */
    private final Map<String, Object> MonitorTheLatestInformationMap = new HashMap<>();
    @Resource
    private AiConfigService aiConfigService;
    @Resource
    private PromptService promptService;
    @Resource
    private ChromeDriverStrategy chromeDriverStrategy;
    @Resource
    private AiArticleCreationLogService aiArticleCreationLogService;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    /**
     * 处理ai返回信息
     *
     * @param chatResponseContent 聊天响应内容
     * @return {@link Article }
     */
    public Article InterceptInfo(String chatResponseContent) {
        String[] strings = chatResponseContent.trim().replace("'", "").split("【【【【【");
        Article article = new Article();
        String title = strings[1];
        article.setTitle(title);
        String conText = strings[2].trim()
                .replace("**", "")
                .replace("###", "")
                .replace("-", "");
        article.setConText(conText);
        return article;
    }

    /**
     * 按键删除
     *
     * @param map 地图
     */
    public void removeByKey(Map<String, Object> map) {
        map.remove("aiPlatForm");
        map.remove("promptName");
        map.remove("hotNewsTitle");
        map.remove("userIdStr");
        map.remove("thirdPartyFormName");
        map.remove("hotURL");
    }

    /**
     * 将文章组装起来
     *
     * @param articleList 文章列表
     * @return {@link String }
     */
    public String assemblyContext(List<String> articleList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < articleList.size(); i++) {
            if (i == 0) {
                stringBuilder.append(articleList.get(i)).append("\n\t");
            }
            stringBuilder.append(articleList.get(i)).append("\t");
        }
        return stringBuilder.toString();
    }

    /**
     * 获取AI配置(秘钥)
     *
     * @param aiPlatForm AI 平台形式
     * @param userId     用户 ID
     * @return {@link AiConfig }
     */
    public AiConfig aiConfig(String aiPlatForm, Long userId) {
        Integer values = Objects.requireNonNull(AIPlatFormEnum.getValuesByName(aiPlatForm)).getValues();
        AiConfig aiConfig = aiConfigService.getAiConfigByUserIdInPlatForm(userId, values);
        ThrowUtils.throwIf(aiConfig == null, ErrorCode.NOT_FOUND_ERROR);
        return aiConfig;
    }

    /**
     * 获取ai提示词模板
     *
     * @param promptName 提示名称
     * @param loginUser  登录用户
     * @return {@link Prompt }
     */
    public Prompt prompt(String promptName, User loginUser) {
        Prompt prompt = promptName == null ?
                promptService.queryByDefault() :
                promptService.queryByPromptName(promptName, loginUser);
        ThrowUtils.throwIf(prompt == null, ErrorCode.NOT_FOUND_ERROR);
        return prompt;
    }

    /**
     * 这个map是拿到hotUrlGainNewMap中相关文章的图片
     *
     * @param hotUrlGainNewMap 热门 URL Gain 新地图
     * @param articleList      文章列表
     * @return {@link Map }<{@link String }, {@link List }<{@link String }>>
     */
    public Map<String, List<String>> imgMap(Map<String, Object> hotUrlGainNewMap, List<String> articleList) {
        Map<String, List<String>> map = new HashMap<>();
        for (String key : hotUrlGainNewMap.keySet()) {
            ArticleVO articleVO = (ArticleVO) hotUrlGainNewMap.get(key);
            articleList.add(String.format("%s \n%s", articleVO.getTitle(), articleVO.getConText().replace("，", ",")));
            List<String> imgList = articleVO.getImgList();
            if (CollectionUtil.isNotEmpty(imgList)) {
                map.put(key + "img", imgList);
            }
        }
        return map;
    }

    /**
     * 操作浏览器发文
     *
     * @param thirdPartyFormName
     * @param userIdStr
     * @param article
     * @param map
     */
    public void chromePublishArticle(String thirdPartyFormName, String userIdStr, Article article, Map<String, List<String>> map) {
        chromeDriverStrategy.getChromeDriverKey(thirdPartyFormName).chromePublishArticle(userIdStr, article, map);
    }

    /**
     * 添加 AI 文章创建日志
     *
     * @param article    品
     * @param hotTitle   热门标题
     * @param hotUrl     热门网址
     * @param aiPlatForm AI 平台形式
     * @param loginUser  登录用户
     */
    public void addAiArticleCreationLog(Article article, String hotTitle, String hotUrl, String aiPlatForm, User loginUser) {
        CompletableFuture.runAsync(() -> {
            AiArticleCreationLog aiArticleCreationLog = new AiArticleCreationLog();
            aiArticleCreationLog.setAiPlatForm(aiPlatForm);
            aiArticleCreationLog.setHotTitle(hotTitle);
            aiArticleCreationLog.setHotUrl(hotUrl);
            aiArticleCreationLog.setAiCreationTitle(article.getTitle());
            aiArticleCreationLog.setAiCreationContext(article.getConText());
            aiArticleCreationLog.setUserId(String.valueOf(loginUser.getId()));
            aiArticleCreationLog.setCreateTime(new Date());
            aiArticleCreationLog.setUpdateTime(new Date());
            Boolean aBoolean = aiArticleCreationLogService.addAiArticleCreationLog(aiArticleCreationLog);
            ThrowUtils.throwIf(!aBoolean, ErrorCode.OPERATION_ERROR);
        }, threadPoolExecutor);
    }

    /**
     * 监控最新信息
     */
    public void MonitorTheLatestInformation() {
        //ThrowUtils.throwIf(executorService!=null,ErrorCode.OPERATION_ERROR,"请关闭当前托管");

        running.set(true);
        executorService.submit(() -> {
            while (running.get()) {
                try {
                    startMonitoring();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * 开始监控
     */
    private void startMonitoring() throws Exception {
        log.info("开始监控****");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + TimeUnit.MINUTES.toMillis(5); // 5分钟的结束时间
        // 在5分钟内持续执行代码
        while (System.currentTimeMillis() < endTime && running.get()) {
            log.info("监控中****");
            //todo 根据指定的类型到各个平台进行获取
            String s = HttpUtil.get("https://ent.163.com/special/000381Q1/newsdata_movieidx.js?callback=data_callback");
            String str = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
            // 获取当前时间
            LocalDateTime currentTime = LocalDateTime.now();
            getLastHotInfo(str, currentTime);
            // 这里可以添加适当的休眠，避免过于频繁的输出

            Thread.sleep(1000); // 每秒执行一次

        }
        if (running.get()) {
            // 休息1分钟
            log.info("休息1分钟...");
            Thread.sleep(TimeUnit.MINUTES.toMillis(1)); // 休息1分钟
            log.info("休息结束...");
        }

    }

    /**
     * 获取最新热门信息
     *
     * @param str         str
     * @param currentTime 当前时间
     */
    private void getLastHotInfo(String str, LocalDateTime currentTime) {
        for (Object o : JSONUtil.parseArray(str)) {
            Map<String, Object> map = (Map<String, Object>) o;
            String timeStr = (String) map.get("time");
            if (StringUtils.isNotBlank(timeStr)) {
                String[] timeArry = timeStr.split(" ");
                String[] dateArry = timeArry[0].split("/");
                String[] newsTimeArry = timeArry[1].split(":");

                LocalDateTime targetTime = LocalDateTime.of(
                        Integer.parseInt(dateArry[2]),
                        Integer.parseInt(dateArry[0]),
                        Integer.parseInt(dateArry[1]),
                        Integer.parseInt(newsTimeArry[0]),
                        Integer.parseInt(newsTimeArry[1]),
                        Integer.parseInt(newsTimeArry[2])
                ); // 示例时间

                // 判断目标时间是否在当前时间的10分钟之内
                boolean isWithinOneHour = targetTime.isAfter(currentTime.minusMinutes(10)) && targetTime.isBefore(currentTime.plusMinutes(10));

                // 输出结果
                if (isWithinOneHour) {
                    String title = (String) map.get("title");
                    String docurl = map.get("docurl").toString();
                    String docId = docurl.substring(docurl.lastIndexOf("/") + 1, docurl.indexOf(".html"));
                    String imgurl = (String) map.get("imgurl");
                    if (!MonitorTheLatestInformationMap.containsKey(docId)) {
                        HotNewsVO hotNewsVO = new HotNewsVO();
                        hotNewsVO.setBiId(docId);
                        hotNewsVO.setTitle(title);
                        hotNewsVO.setHotURL(docurl);
                        hotNewsVO.setImageURL(imgurl);
                        log.info("10分钟内发布的\t{}:{}", title, docurl);
                        MonitorTheLatestInformationMap.put(docId, hotNewsVO);
                    }
                }
            }
        }
    }


    //@PreDestroy
    public void clear() {
        log.info("关闭监控....");
        running.set(false);
        if (!MonitorTheLatestInformationMap.isEmpty()) {
            MonitorTheLatestInformationMap.clear();
        }
    }


}
