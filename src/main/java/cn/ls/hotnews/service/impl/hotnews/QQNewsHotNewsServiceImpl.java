package cn.ls.hotnews.service.impl.hotnews;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.BusinessException;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.hotnews.HotNewsAddReq;
import cn.ls.hotnews.model.dto.hotnews.HotNewsQueryReq;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.vo.ArticleVO;
import cn.ls.hotnews.model.vo.HotApiVO;
import cn.ls.hotnews.model.vo.HotNewsVO;
import cn.ls.hotnews.service.HotApiService;
import cn.ls.hotnews.service.HotNewsService;
import cn.ls.hotnews.utils.CommonUtils;
import cn.ls.hotnews.utils.RedisUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.ls.hotnews.constant.CommonConstant.*;

/**
 * title: QQNewsHotNewsServiceImpl
 * author: liaoshuo
 * date: 2024/12/9 14:17
 * description:
 */
@Slf4j
@Service("qqnews")
public class QQNewsHotNewsServiceImpl extends HotNewsCommonAbstract implements HotNewsService {

    @Resource
    private HotApiService hotApiService;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 热点新闻列表
     *
     * @return {@link List }<{@link HotNewsVO }>
     */
    @Override
    public List<HotNewsVO> hotNewsList() {
        String Key = REDIS_QQNEWS;
        List<HotNewsVO> hotNewsVOList = redisUtils.redisGet(Key);
        if (hotNewsVOList != null) {
            return hotNewsVOList;
        }
        HotApi platformAPI = hotApiService.getPlatformAPI("qq_news");
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        try {
            String str = HttpUtil.get(platformAPI.getApiURL());
            List<Object> idlist = (List<Object>) JSONUtil.parseObj(str).get("idlist");
            List<Object> newslist = (List<Object>) JSONUtil.parseObj(idlist.get(0)).get("newslist");
            newslist.remove(0);
            hotNewsVOList = new ArrayList<>();
            for (Object o : newslist) {
                Map<String, Object> map = (Map<String, Object>) o;
                HotNewsVO hotNewsVO = new HotNewsVO();
                hotNewsVO.setBiId((String) map.get("id"));
                hotNewsVO.setTitle((String) map.get("title"));
                hotNewsVO.setHotURL((String) map.get("url"));
                hotNewsVOList.add(hotNewsVO);
            }
            redisUtils.redisSetInOneHour(Key, hotNewsVOList);
            redisUtils.redisSetInOneHour(REDIS_QQNEWS_DTATETIME, new DateTime());
        } catch (Exception e) {
            log.error("qq news news is error, message:{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "腾讯新闻热点获取失败");
        }
        return hotNewsVOList;
    }

    /**
     * @param hotNewsQueryReq
     * @return
     */
    @Override
    public Map<String, Object> hotNewsList(HotNewsQueryReq hotNewsQueryReq) {
        Map<String, Object> map = new HashMap<>();
        String hotType = hotNewsQueryReq.getHotType();
        if (StringUtils.isBlank(hotType)) {
            map.put("newsList", hotNewsList());
        } else {
            HotApi platformAPI = hotApiService.getPlatformAPI(hotType);
            ThrowUtils.throwIf(platformAPI == null, ErrorCode.PARAMS_ERROR);
            String body = HttpUtil.createPost(platformAPI.getApiURL())
                    .body(platformAPI.getApiParam()).execute().body();
            JsonArray asJsonArray = JsonParser.parseString(body).getAsJsonObject().get("data").getAsJsonArray();
            List<HotNewsVO> list = new ArrayList<>();
            extractedUrlReturnInfo(asJsonArray, list);
            map.put("newsList", list);
        }

        List<HotApiVO> hotApiVOList = hotApiService.getPlatFormByLikeRightAPI("qq_news_");
        map.put("hotType", hotApiVOList);
        return map;
    }


    /**
     * 提取 URL 返回信息
     *
     * @param asJsonArray 作为 JSON 数组
     * @param list        列表
     */
    private void extractedUrlReturnInfo(JsonArray asJsonArray, List<HotNewsVO> list) {
        asJsonArray.forEach(item -> {
            JsonObject asJsonObject = item.getAsJsonObject();
            if (asJsonObject.getAsJsonObject().has("sub_item")) {
                JsonArray subItem = asJsonObject.get("sub_item").getAsJsonArray();
                subItem.forEach(li -> {
                    JsonObject jsonObject = li.getAsJsonObject();
                    if (jsonObject.get("articletype").getAsString().equals("0")) {
                        String id = jsonObject.get("id").getAsString();
                        String title = jsonObject.get("title").getAsString();
                        String url = jsonObject.get("link_info").getAsJsonObject().get("url").getAsString();
                        HotNewsVO hotNewsVO = new HotNewsVO();
                        hotNewsVO.setBiId(id);
                        hotNewsVO.setTitle(title);
                        hotNewsVO.setHotURL(url);
                        list.add(hotNewsVO);
                    }

                });
            } else {
                if (asJsonObject.get("articletype").getAsString().equals("0")) {
                    String id = asJsonObject.get("id").getAsString();
                    String title = asJsonObject.get("title").getAsString();
                    String url = asJsonObject.get("link_info").getAsJsonObject().get("url").getAsString();
                    HotNewsVO hotNewsVO = new HotNewsVO();
                    hotNewsVO.setBiId(id);
                    hotNewsVO.setTitle(title);
                    hotNewsVO.setHotURL(url);
                    list.add(hotNewsVO);
                }
            }
        });
    }


    /**
     * 根据热点链接获取相关文章
     *
     * @param req 要求
     * @return {@link Map }<{@link String }, {@link String }>
     */
    @Override
    public Map<String, Object> getHotUrlGainNew(HotNewsAddReq req) {
        return extractHotURLGainNewInfo(req);
    }


    public ArticleVO getEditingByDoc(Document doc) {
        ArticleVO articleVO = new ArticleVO();
        List<String> imgList = new ArrayList<>();
        Elements elementsByClass = doc.getElementsByClass("rich_media_content");
        elementsByClass.select("strong").remove();
        for (Element byClass : elementsByClass) {
            imgList.add(byClass.getElementsByTag("img").attr("src"));
        }
        articleVO.setTitle(doc.select("h1").text());
        articleVO.setConText(CommonUtils.cleanText(elementsByClass.text()));
        articleVO.setImgList(imgList);
        return articleVO;
    }

    /**
     * 提取响应信息
     * todo
     *
     * @param responsesInfo 回复信息
     * @param currentTime   当前时间
     */
    @Override
    public Map<String, Object> extractResponseInfo(String responsesInfo, LocalDateTime currentTime) {
        JsonArray asJsonArray = JsonParser.parseString(responsesInfo)
                .getAsJsonObject().get("data")
                .getAsJsonArray();
        for (JsonElement jsonElement : asJsonArray) {
            JsonObject asJsonObject = jsonElement.getAsJsonObject();
            if (asJsonObject.getAsJsonObject().has("sub_item")) {
                JsonArray subItem = asJsonObject.get("sub_item").getAsJsonArray();
                for (JsonElement element : subItem) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    if (jsonObject.get("articletype").getAsString().equals("0")) {
                        return isCheckPublishTime(currentTime, jsonObject);
                    }
                }
            } else {
                if (asJsonObject.get("articletype").getAsString().equals("0")) {
                    return isCheckPublishTime(currentTime, asJsonObject);
                }
            }
        }
        return null;
    }

    /**
     * 是检查发布时间在指定时间内
     *
     * @param currentTime 当前时间
     * @param jsonObject  JSON 对象
     * @return {@link Map }<{@link String }, {@link Object }>
     */
    private Map<String, Object> isCheckPublishTime(LocalDateTime currentTime, JsonObject jsonObject) {
        String publishTime = jsonObject.get("publish_time").getAsString();
        String[] publishTimeArray = publishTime.split(" ");
        String[] dayArray = publishTimeArray[0].split("-");
        String[] timeArray = publishTimeArray[1].split(":");
        // 示例时间
        LocalDateTime targetTime = LocalDateTime.of(Integer.parseInt(dayArray[0]),
                Integer.parseInt(dayArray[1]),
                Integer.parseInt(dayArray[2]),
                Integer.parseInt(timeArray[0]),
                Integer.parseInt(timeArray[1]),
                Integer.parseInt(timeArray[2])
        );
        boolean isWithinOneHour = targetTime.isAfter(currentTime.minusMinutes(10)) && targetTime.isBefore(currentTime.plusMinutes(10));
        if (isWithinOneHour) {
            String id = jsonObject.get("id").getAsString();
            String title = jsonObject.get("title").getAsString();
            String url = jsonObject.get("link_info").getAsJsonObject().get("url").getAsString();
            if (!MonitorTheLatestInformationMap.containsKey(id)) {
                log.info("腾讯10分钟内发布文章\t{}:{}", title, url);
                MonitorTheLatestInformationMap.put(id, url);
                return getMapCompletableFuture(url, title);
            }
        }
        return null;
    }

    /**
     * 提取 urlinfo
     *
     * @param hotApi 热门 API
     * @return {@link String }
     */
    @Override
    public String extractURLInfo(HotApi hotApi) {
        return CommonUtils.doSecurePost(hotApi.getApiURL(), hotApi.getApiParam());
    }
}
