package cn.ls.hotnews.ai;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.entity.*;
import cn.ls.hotnews.service.HotApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * title: JieYueAIServiceImpl
 * author: liaoshuo
 * date: 2024/12/7 20:50
 * description: 阶跃星辰AI模型
 */
@Slf4j
@Service("jieyue")
public class JieYueAIServiceImpl implements AIService {

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
        articleList.add((String) hotUrlGainNewMap.get("hotNewsTitle"));
        //查询配置
        AiConfig aiConfig = aiCommon.aiConfig((String) hotUrlGainNewMap.get("aiPlatForm"), userId);
        //查询提示词 有指定提示词用指定的，没有则用默认的 default
        Prompt prompt = aiCommon.prompt((String) hotUrlGainNewMap.get("promptName"), loginUser);
        String userIdStr = (String) hotUrlGainNewMap.get("userIdStr");
        String thirdPartyFormName = (String) hotUrlGainNewMap.get("thirdPartyFormName");

        //以上的值在map中的值取出后进行删除
        aiCommon.removeByKey(hotUrlGainNewMap);
        //这个map是拿到hotUrlGainNewMap中相关文章的图片
        Map<String, List<String>> map = aiCommon.imgMap(hotUrlGainNewMap, articleList);

        //创建连接 将提示词、热点标题、相关文章喂给 ai
        String chatMessages = getJSONByStr(constructRequest(prompt.getPromptTemplate(), articleList, aiConfig.getApiKey()));
        //处理ai生成的内容
        Article article = aiCommon.InterceptInfo(chatMessages);
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
        HotApi platformAPI = hotApiService.getPlatformAPI("jieyue_xingchen_ai");
        ThrowUtils.throwIf(platformAPI==null, ErrorCode.NOT_FOUND_ERROR);
        return HttpRequest.post(platformAPI.getApiURL())
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("Bearer %s", apiKey))
                .body(JSONUtil.createObj()
                        .set("model", "step-1-8k")
                        .set("messages",
                                JSONUtil.createArray()
                                        .set(JSONUtil.createObj()
                                                .set("role", "system")
                                                .set("content", promptTemplate)
                                        )
                                        .set(JSONUtil.createObj()
                                                .set("role", "user")
                                                .set("content", aiCommon.assemblyContext(articleList))
                                        )
                        )
                        .toString()
                )
                .execute().body();
    }


    /**
     * 获取AI回答的JSON内容
     *
     * @param result 结果
     * @return {@link String }
     */
    private String getJSONByStr(String result) {
        List<Object> list = (List<Object>) JSONUtil.parseObj(JSONUtil.parseObj(result)).get("choices");
        JSONObject entries = JSONUtil.parseObj(JSONUtil.parseObj(list.get(0)));
        String str = (String) JSONUtil.parseObj(entries.get("message")).get("content");
        log.info("阶跃星辰AI模型回答\n{}", str);
        return str;
    }

}
