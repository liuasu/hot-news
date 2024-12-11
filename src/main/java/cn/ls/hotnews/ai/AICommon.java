package cn.ls.hotnews.ai;

import cn.hutool.core.collection.CollectionUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.enums.AIPlatFormEnum;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.entity.*;
import cn.ls.hotnews.model.vo.ArticleVO;
import cn.ls.hotnews.service.AiArticleCreationLogService;
import cn.ls.hotnews.service.AiConfigService;
import cn.ls.hotnews.service.PromptService;
import cn.ls.hotnews.strategy.ChromeDriverStrategy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * title: AICommon
 * author: liaoshuo
 * date: 2024/12/7 21:04
 * description:
 */
@Service
public class AICommon {


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

    public void chromePublishArticle(String thirdPartyFormName, String userIdStr, Article article, Map<String, List<String>> map) {
        chromeDriverStrategy.getChromeDriverKey(thirdPartyFormName).chromePublishArticle(userIdStr, article, map);
    }

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
}
