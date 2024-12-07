package cn.ls.hotnews.ai;

import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.enums.AIPlatFormEnum;
import cn.ls.hotnews.exception.BusinessException;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.entity.*;
import cn.ls.hotnews.model.vo.ArticleVO;
import cn.ls.hotnews.service.AiConfigService;
import cn.ls.hotnews.service.HotApiService;
import cn.ls.hotnews.service.PromptService;
import cn.ls.hotnews.strategy.ChromeDriverStrategy;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.exception.SparkException;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkTextUsage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * title: XingHuoAIServiceImpl
 * author: liaoshuo
 * date: 2024/12/5 15:38
 * description: 讯飞星火 ai
 */
@Slf4j
@Service("xinghuo")
public class XingHuoAIServiceImpl implements AIService {

    @Resource
    private AiConfigService aiConfigService;
    @Resource
    private PromptService promptService;
    @Resource
    private HotApiService hotApiService;
    @Resource
    private ChromeDriverStrategy chromeDriverStrategy;

    /**
     * ai 文章创作
     *
     * @param hotUrlGainNewMap 热门 URL Gain 新地图
     * @param loginUser        登录用户
     *                                                                                                                                                 todo
     */
    @Override
    public void productionArticle(Map<String, Object> hotUrlGainNewMap, User loginUser) {
        String aiPlatForm = (String) hotUrlGainNewMap.get("aiPlatForm");
        hotUrlGainNewMap.remove("aiPlatForm");
        String promptName = (String) hotUrlGainNewMap.get("promptName");
        hotUrlGainNewMap.remove("promptName");
        String hotNewsTitle = (String) hotUrlGainNewMap.get("hotNewsTitle");
        hotUrlGainNewMap.remove("hotNewsTitle");
        String userIdStr = (String) hotUrlGainNewMap.get("userIdStr");
        hotUrlGainNewMap.remove("userIdStr");
        String thirdPartyFormName = (String) hotUrlGainNewMap.get("thirdPartyFormName");
        hotUrlGainNewMap.remove("thirdPartyFormName");
        Integer values = Objects.requireNonNull(AIPlatFormEnum.getValuesByName(aiPlatForm)).getValues();
        Long userId = loginUser.getId();

        List<String> articleList = new ArrayList<>(4);
        articleList.add(hotNewsTitle);
        Map<String, List<String>> map = new HashMap<>();
        //for (int i = 1; i <= 3; i++) {
        //    String key = "editing_" + i;
        //    ArticleVO articleVO = (ArticleVO) hotUrlGainNewMap.get(key);
        //    articleList.add(String.format("%s \n%s", articleVO.getTitle(), articleVO.getConText()));
        //    map.put(key+"img",articleVO.getImgList());
        //}
        for (String key : hotUrlGainNewMap.keySet()) {
            ArticleVO articleVO = (ArticleVO) hotUrlGainNewMap.get(key);
            articleList.add(String.format("%s \n%s", articleVO.getTitle(), articleVO.getConText()));
            map.put(key + "img", articleVO.getImgList());
        }


        //查询配置，并创建连接
        AiConfig aiConfig = aiConfigService.getAiConfigByUserIdInPlatForm(userId, values);
        //查询提示词 有指定提示词用指定的，没有则用默认的 default
        Prompt prompt = promptName == null ?
                promptService.queryByDefault() :
                promptService.queryByPromptName(promptName, loginUser);
        //将提示词、热点标题、相关文章喂给 ai
        String chatMeassages = constructRequest(createSparkClient(aiConfig), prompt, articleList);
        //处理ai生成的内容
        Article article = InterceptInfo(chatMeassages);
        HotApi platformAPI = hotApiService.getPlatformAPI("toutiao_article_publish");
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        //操作浏览器进行文章发布
        chromeDriverStrategy.getChromeDriverKey(thirdPartyFormName).chromePublishArticle(userIdStr, article, map);
    }

    /**
     * 构造ai请求
     *
     * @param sparkClient Spark 客户端
     * @param prompt      提示
     * @param articleList 文章列表
     * @return {@link String }
     */
    private String constructRequest(SparkClient sparkClient, Prompt prompt, List<String> articleList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < articleList.size(); i++) {
            if (i == 0) {
                stringBuilder.append(articleList.get(i)).append("\n\t");
            }
            stringBuilder.append(articleList.get(i)).append("\t");
        }

        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent(prompt.getPromptTemplate()));
        messages.add(SparkMessage.userContent(stringBuilder.toString()));
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传，默认为2048。
                // V1.5取值为[1,4096]
                // V2.0取值为[1,8192]
                // V3.0取值为[1,8192]
                .maxTokens(3000)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                // 指定请求版本，默认使用最新3.0版本
                .apiVersion(SparkApiVersion.V4_0)
                .build();
        try {
            // 同步调用
            SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
            SparkTextUsage textUsage = chatResponse.getTextUsage();
            String content = chatResponse.getContent();
            log.info("星火AI回答:\n{}", content);
            log.info("提问tokens:{},回答tokens:{},总消耗tokens:{}",
                    textUsage.getPromptTokens(),
                    textUsage.getCompletionTokens(),
                    textUsage.getTotalTokens());
            return content;
        } catch (SparkException e) {
            log.error("星火 AI 调用失败,错误信息:", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }

    /**
     * 处理ai返回信息
     *
     * @param chatResponseContent 聊天响应内容
     * @return {@link Article }
     */
    private Article InterceptInfo(String chatResponseContent) {
        String[] strings = chatResponseContent.trim().replace("'", "").split("【【【【【");
        Article article = new Article();
        article.setTitle(strings[1]);
        article.setConText(strings[2].trim().replace("**",""));
        if (strings.length > 3) {
            String string = strings[3];
            if (StringUtils.isNotBlank(string)) {
                String[] alternateTitle = string.trim().split("\n");
                article.setAlternateTitleList(new ArrayList<>(Arrays.asList(alternateTitle).subList(1, alternateTitle.length)));
            }
        }
        return article;
    }

    /**
     * 创建SparkClient实例
     */
    private SparkClient createSparkClient(AiConfig aiConfig) {
        SparkClient client = new SparkClient();
        client.appid = aiConfig.getAppId();
        client.apiKey = aiConfig.getApiKey();
        client.apiSecret = aiConfig.getApiSecret();
        return client;
    }
}
