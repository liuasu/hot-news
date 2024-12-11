package cn.ls.hotnews.ai;

import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.BusinessException;
import cn.ls.hotnews.model.entity.AiConfig;
import cn.ls.hotnews.model.entity.Article;
import cn.ls.hotnews.model.entity.Prompt;
import cn.ls.hotnews.model.entity.User;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.exception.SparkException;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkTextUsage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private AICommon aiCommon;

    /**
     * ai 文章创作
     *
     * @param hotUrlGainNewMap 热门 URL Gain 新地图
     * @param loginUser        登录用户
     *                                                                                                                                                                                                                                                                                                                         todo
     */
    @Override
    public void productionArticle(Map<String, Object> hotUrlGainNewMap, User loginUser) {
        List<String> articleList = new ArrayList<>(4);
        Long userId = loginUser.getId();
        String hotNewsTitle = (String) hotUrlGainNewMap.get("hotNewsTitle");
        String hotUrl = (String) hotUrlGainNewMap.get("hotURL");
        String aiPlatForm = (String) hotUrlGainNewMap.get("aiPlatForm");
        String userIdStr = (String) hotUrlGainNewMap.get("userIdStr");
        String thirdPartyFormName = (String) hotUrlGainNewMap.get("thirdPartyFormName");
        articleList.add(hotNewsTitle);
        //查询配置

        AiConfig aiConfig = aiCommon.aiConfig(aiPlatForm, userId);
        //查询提示词 有指定提示词用指定的，没有则用默认的 default
        Prompt prompt = aiCommon.prompt((String) hotUrlGainNewMap.get("promptName"), loginUser);

        //以上的值在map中的值取出后进行删除
        aiCommon.removeByKey(hotUrlGainNewMap);
        //这个map是拿到hotUrlGainNewMap中相关文章的图片
        Map<String, List<String>> map = aiCommon.imgMap(hotUrlGainNewMap, articleList);
        //异步进行操作
        //CompletableFuture.runAsync(() -> {
        //创建连接 将提示词、热点标题、相关文章喂给 ai
        String chatMessages = constructRequest(createSparkClient(aiConfig), prompt, articleList);
        //处理ai生成的内容
        Article article = aiCommon.InterceptInfo(chatMessages);
        aiCommon.addAiArticleCreationLog(article, hotNewsTitle, hotUrl, aiPlatForm, loginUser);
        //操作浏览器进行文章发布
        aiCommon.chromePublishArticle(thirdPartyFormName, userIdStr, article, map);
        //}, threadPoolExecutor);

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
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent(prompt.getPromptTemplate()));
        messages.add(SparkMessage.userContent(aiCommon.assemblyContext(articleList)));
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
