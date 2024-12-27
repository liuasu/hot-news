package cn.ls.hotnews.ai;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.enums.AIPlatFormEnum;
import cn.ls.hotnews.exception.BusinessException;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.thirdpartyaccount.AccountTrusteeship;
import cn.ls.hotnews.model.entity.*;
import cn.ls.hotnews.model.vo.ArticleVO;
import cn.ls.hotnews.model.vo.ThirdPartyAccountVO;
import cn.ls.hotnews.service.AiArticleCreationLogService;
import cn.ls.hotnews.service.AiConfigService;
import cn.ls.hotnews.service.PromptService;
import cn.ls.hotnews.strategy.ChromeDriverStrategy;
import cn.ls.hotnews.strategy.HotNewsStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static cn.ls.hotnews.constant.CommonConstant.MonitorTheLatestInformationMap;

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
    private final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
    /**
     * 记录上一次发布的类型
     */
    private final List<String> hotTypeList = new CopyOnWriteArrayList<>();
    /**
     * 记录上次发布的账号<index,account>
     * 或
     * 记录上次账号发布类型<account,hotType>
     */
    private final Map<String, String> accountMap = new ConcurrentHashMap<>();
    private int index = 0;
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
    @Resource
    private HotNewsStrategy hotNewsStrategy;

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
        String conText = strings[2].trim().replace("**", "").replace("###", "").replace("-", "");
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
        Prompt prompt = promptName == null ? promptService.queryByDefault() : promptService.queryByPromptName(promptName, loginUser);
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
        String key = "editing_1";
        ArticleVO articleVO = (ArticleVO) hotUrlGainNewMap.get(key);
        articleList.add(String.format("%s \n%s", articleVO.getTitle(), articleVO.getConText().replace("，", ",")));
        List<String> imgList = articleVO.getImgList();
        if (CollectionUtil.isNotEmpty(imgList)) {
            map.put(key + "img", imgList);
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
    public void MonitorTheLatestInformation(Consumer<Map<String, Object>> consumer) {
        boolean isRunning = running.get();
        if (!isRunning) {
            running.set(true);
        }
        executorService.submit(() -> {
            log.info("开始监控****");
            while (running.get()) {
                try {
                    long startTime = System.currentTimeMillis();
                    long endTime = startTime + TimeUnit.MINUTES.toMillis(5); // 5分钟的结束时间
                    // 获取当前时间
                    LocalDateTime currentTime = LocalDateTime.now();
                    // 在5分钟内持续执行代码
                    while (System.currentTimeMillis() < endTime && running.get()) {
                        log.info("监控中****");
                        //todo 根据指定的类型到各个平台进行获取
                        String body = HttpUtil.get("https://ent.163.com/special/000381Q1/newsdata_movieidx.js?callback=data_callback");
                        Map<String, Object> listMap = hotNewsStrategy.getHotNewsByPlatform("wangyi").extractResponseInfo(body, currentTime);

                        if (CollectionUtil.isNotEmpty(listMap)) {
                            consumer.accept(listMap);
                        }
                        // 这里可以添加适当的休眠，每秒执行一次 避免过于频繁的输出
                        Thread.sleep(TimeUnit.SECONDS.toMillis(1));

                    }
                    if (running.get()) {
                        // 休息1分钟
                        log.info("休息1分钟...");
                        Thread.sleep(TimeUnit.MINUTES.toMillis(1)); // 休息1分钟
                        log.info("休息结束...");
                    }
                } catch (Exception e) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "监控失败");
                }
            }
        });
    }

    /**
     * 监控最新信息2
     *
     * @param urlMap        <hotType,urlList>
     * @param accountVOList 账户投票表
     * @param consumer      消费者
     */
    public void monitorTheLatestInformation2(Map<String, List<HotApi>> urlMap, List<AccountTrusteeship> accountVOList, Consumer<Map<String, Object>> consumer) {
        // 进行监控
        if (!running.get()) {
            running.set(true);
            log.info("开始监控****");
        }
        long startTime = System.currentTimeMillis();
        long endTime = startTime + TimeUnit.MINUTES.toMillis(5); // 5分钟的结束时间

        CompletableFuture.supplyAsync(() -> {
            try {
                while (running.get()) {
                    for (AccountTrusteeship accountTrusteeship : accountVOList) {
                        String hotTypeKey = accountTrusteeship.getHotType();
                        String account = accountTrusteeship.getAccount();
                        if (hotTypeList.contains(accountMap.get(account))) {
                            // 休息1分钟
                            if (running.get()) {
                                log.info("休息1分钟...");
                                sleep(1);
                                log.info("休息结束...");
                                continue;
                            }
                        }
                        List<HotApi> hotApis = urlMap.get(hotTypeKey);
                        for (HotApi item : hotApis) {
                            getUrlInfo(consumer, account, hotTypeKey, item, endTime);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("监控过程中发生异常: ", e);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
            }
            return null; // 返回 null 表示任务完成
        }).exceptionally(ex -> {
            log.error("监控任务异常: ", ex);
            return null; // 返回 null 表示任务异常完成
        });
    }

    /**
     * 获取 URL 信息
     *
     * @param consumer   消费者
     * @param account    帐户
     * @param hotTypeKey 热类型 Key
     * @param item       项目
     * @param endTime    结束时间
     */
    private void getUrlInfo(Consumer<Map<String, Object>> consumer, String account, String hotTypeKey, HotApi item, long endTime) {
        String apiURL = item.getApiURL();
        String platform = item.getPlatform();
        String apiName = item.getApiName();
        String[] strings = platform.split("_");
        platform = strings[0];

        // 在5分钟内持续执行代码
        while (System.currentTimeMillis() < endTime && running.get()) {
            log.info("{} 热点监控中****", apiName);
            try {
                String body = HttpUtil.get(apiURL);
                Map<String, Object> articleMap = hotNewsStrategy.getHotNewsByPlatform(platform).extractResponseInfo(body, LocalDateTime.now());
                if (CollectionUtil.isNotEmpty(articleMap) && !hotTypeList.contains(hotTypeKey)) {
                    hotTypeList.add(hotTypeKey);
                    accountMap.put(account, hotTypeKey);
                    consumer.accept(articleMap);
                } else {
                    // 适当的休眠，避免过于频繁的请求
                    sleep(1);
                    break;
                }
            } catch (Exception e) {
                log.error("获取URL信息时发生异常: ", e);
                sleep(1); // 在发生异常时休眠
            }
        }
        //// 休息1分钟
        //if (System.currentTimeMillis() > endTime && running.get()) {
        //    log.info("休息1分钟...");
        //    sleep(1);
        //    log.info("休息结束...");
        //}
    }

    private void sleep(int minutes) {
        try {
            Thread.sleep(TimeUnit.MINUTES.toMillis(minutes));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            log.error("休眠被中断: ", e);
        }
    }

    public void clear() {
        log.info("关闭监控....");
        running.set(false);
        if (!MonitorTheLatestInformationMap.isEmpty()) {
            MonitorTheLatestInformationMap.clear();
        }
    }


    /**
     * 发布文章
     *
     * @param imgMap        IMG 地图
     * @param accountVOList 账户投票表
     * @param chatMessages  聊天消息
     * @param articleList   文章列表
     */
    public void PublishArticle(Map<String, List<String>> imgMap, List<AccountTrusteeship> accountVOList, String chatMessages, List<String> articleList) {
        CompletableFuture.runAsync(() -> {

            //当发布数 == 账号数时 将 accountMap、hotTypeList 进行清空
            if (accountMap.size() == accountVOList.size()) {
                accountMap.clear();
                hotTypeList.clear();
            }
            //获取集合中的账号
            ThirdPartyAccountVO thirdPartyAccountVO = accountVOList.get(index);

            String account = thirdPartyAccountVO.getAccount();
            String platForm = thirdPartyAccountVO.getPlatForm();
            //添加到记录发布的集合中
            String key = String.valueOf(index);
            if (!accountMap.containsKey(key)) {
                accountMap.put(key, account);
                index++;
            }
            //chatMessages 不为空，清空 articleList
            //if (StringUtils.isNotBlank(chatMessages)) {
            //    articleList.clear();
            //}
            ////解析ai返回的信息
            //Article article = this.InterceptInfo(chatMessages);
            //记录ai生成的文章
            //aiCommon.addAiArticleCreationLog(article, hotNewsTitle, hotUrl, aiPlatForm, loginUser);
            ////操作浏览器进行文章发布
            //String values = Objects.requireNonNull(ChromePlatFormEnum.getValuesByName(platForm)).getValues();
            //操作浏览器
            //aiCommon.chromePublishArticle(values, account, article, imgMap);
        }, threadPoolExecutor);
    }
}