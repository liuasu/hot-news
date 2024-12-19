package cn.ls.hotnews.ai;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.productionarticle.ProductionTrusteeshipAddReq;
import cn.ls.hotnews.model.entity.*;
import cn.ls.hotnews.service.HotApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * title: ZhiPuAIServiceImpl
 * author: liaoshuo
 * date: 2024/12/7 22:35
 * description: 智普AI模型
 */
@Slf4j
@Service("zhipu")
public class ZhiPuAIServiceImpl implements AIService {

    @Resource
    private AICommon aiCommon;
    @Resource
    private HotApiService hotApiService;

    /**
     * ai 文章创作
     *
     * @param hotUrlGainNewMap 热门 URL 增益 新
     * @param loginUser        登录用户
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

        //创建连接 将提示词、热点标题、相关文章喂给 ai
        String chatMessages = getJSONByStr(constructRequest(prompt.getPromptTemplate(), articleList, aiConfig.getApiKey()));
        //处理ai生成的内容
        Article article = aiCommon.InterceptInfo(chatMessages);
        aiCommon.addAiArticleCreationLog(article, hotNewsTitle, hotUrl, aiPlatForm, loginUser);
        //操作浏览器进行文章发布
        aiCommon.chromePublishArticle(thirdPartyFormName, userIdStr, article, map);
    }


    /**
     * 构造请求
     *
     * @param promptTemplate 提示模板
     * @param articleList    文章列表
     * @param apiKey         API 密钥
     * @return {@link String }
     */
    private String constructRequest(String promptTemplate, List<String> articleList, String apiKey) {
        HotApi platformAPI = hotApiService.getPlatformAPI("zhipu_ai");
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        JSONArray messages = new JSONArray();
        messages.add(new JSONObject()
                .set("role", "system")
                .set("content", promptTemplate));
        messages.add(new JSONObject()
                .set("role", "user")
                .set("content", aiCommon.assemblyContext(articleList)));
        return HttpRequest.post(platformAPI.getApiURL())
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("Bearer %s", apiKey))
                .body(String.valueOf(new JSONObject().set("model", "glm-4-plus").set("messages", messages))).execute().body();
    }


    /**
     * 获取AI回答的JSON内容
     *
     * @param result 结果
     * @return {@link String }
     */
    private String getJSONByStr(String result) {
        List<Object> choicesList = (List<Object>) JSONUtil.parseObj(JSONUtil.parseObj(result)).get("choices");
        Map<String, Object> map = (Map<String, Object>) choicesList.get(0);
        JSONObject entries = JSONUtil.parseObj(map.get("message"));
        String str = (String) entries.get("content");
        log.info("智普AI模型回答\n{}", str);
        return str;
    }

    /**
     * 托管
     *
     * @param trusteeshipAddReq 托管 add req
     * @param loginUser         登录用户
     */
    @Override
    public void Trusteeship(ProductionTrusteeshipAddReq trusteeshipAddReq, User loginUser) {

    }
}
