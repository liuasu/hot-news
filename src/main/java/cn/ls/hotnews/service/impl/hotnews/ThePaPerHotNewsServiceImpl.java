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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.ls.hotnews.constant.CommonConstant.*;

/**
 * title: ThePaPerHotNewsServiceImpl
 * author: liaoshuo
 * date: 2024/12/2 21:39
 * description: 澎湃新闻
 */
@Slf4j
@Service("thepaper")
public class ThePaPerHotNewsServiceImpl extends HotNewsCommonAbstract implements HotNewsService {
    private static final String thePaPerUrl = "https://www.thepaper.cn/newsDetail_forward_%s";
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private HotApiService hotApiService;

    /**
     * 热点新闻列表
     *
     * @return {@link List }<{@link HotNewsVO }>
     */
    @Override
    public List<HotNewsVO> hotNewsList() {
        String thePaPerKey = REDIS_THEPAPER;
        List<HotNewsVO> hotNewsVOList = redisUtils.redisGet(thePaPerKey);
        if (hotNewsVOList != null) {
            return hotNewsVOList;
        }
        HotApi platformAPI = hotApiService.getPlatformAPI("thepaper");
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        try {
            String thePaPerStr = HttpUtil.get(platformAPI.getApiURL());
            if (thePaPerStr == null) {
                return new ArrayList<>();
            }
            Object entries = JSONUtil.parseObj(JSONUtil.parseObj(thePaPerStr)).get("data");
            List<Object> thePaPerList = (List<Object>) JSONUtil.parseObj(entries).get("hotNews");
            hotNewsVOList = new ArrayList<>();
            for (Object o : thePaPerList) {
                Map<String, Object> map = (Map<String, Object>) o;
                HotNewsVO hotNewsVO = new HotNewsVO();
                String contId = map.get("contId").toString();
                hotNewsVO.setId(Long.valueOf(contId));
                hotNewsVO.setTitle((String) map.get("name"));

                hotNewsVO.setHotURL(String.format(thePaPerUrl, contId));
                hotNewsVO.setImageURL((String) map.get("pic"));
                hotNewsVOList.add(hotNewsVO);
            }
            redisUtils.redisSetInOneHour(thePaPerKey, hotNewsVOList);
            redisUtils.redisSetInOneHour(REDIS_THEPAPER_DTATETIME, new DateTime());
        } catch (Exception e) {
            log.error("thepaper news is error, message:{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "澎湃新闻获取失败");
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
            String body = HttpUtil.get(String.format(platformAPI.getApiURL(), platformAPI.getApiParam()));
            List<HotNewsVO> list = new ArrayList<>();
            JsonArray asJsonArray = JsonParser.parseString(body)
                    .getAsJsonObject().get("pageProps")
                    .getAsJsonObject().get("data").getAsJsonObject().get("list").getAsJsonArray();
            for (JsonElement element : asJsonArray) {
                JsonObject asJsonObject = element.getAsJsonObject();
                String contId = asJsonObject.get("contId").getAsString();
                String name = asJsonObject.get("name").getAsString();
                String url = String.format(thePaPerUrl, contId);
                HotNewsVO hotNewsVO = new HotNewsVO();
                hotNewsVO.setId(Long.valueOf(contId));
                hotNewsVO.setTitle(name);
                hotNewsVO.setHotURL(url);
                list.add(hotNewsVO);
            }
            map.put("newsList", list);
        }

        List<HotApiVO> hotApiVOList = hotApiService.getPlatFormByLikeRightAPI("thepaper_");
        map.put("hotType", hotApiVOList);
        return map;
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

    /**
     * 按 DOC 进行编辑
     *
     * @param doc 医生
     * @return {@link ArticleVO }
     */
    public ArticleVO getEditingByDoc(Document doc) {
        ArticleVO articleVO = new ArticleVO();
        List<String> imgList = new ArrayList<>();
        Elements elementsByClass = doc.getElementsByClass("index_wrapper__L_zqV");
        for (Element byClass : elementsByClass) {
            imgList.add(byClass.getElementsByTag("img").attr("src"));
        }
        articleVO.setTitle(doc.getElementsByClass("index_title__B8mhI").text());
        articleVO.setConText(doc.getElementsByClass("index_cententWrap__Jv8jK").text());
        articleVO.setImgList(imgList);
        return articleVO;
    }

    /**
     * todo
     * 提取响应信息
     *
     * @param responsesInfo 回复信息
     * @param currentTime   当前时间
     */
    @Override
    public Map<String, Object> extractResponseInfo(String responsesInfo, LocalDateTime currentTime) {
        JsonArray asJsonArray = JsonParser.parseString(responsesInfo)
                .getAsJsonObject().get("pageProps")
                .getAsJsonObject().get("data").getAsJsonObject().get("list").getAsJsonArray();
        for (JsonElement element : asJsonArray) {
            JsonObject asJsonObject = element.getAsJsonObject();

            String pubTimeLong = asJsonObject.get("pubTimeLong").getAsString();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String publishTime = format.format(pubTimeLong + "L");
            String[] publishArray = publishTime.split(" ");
            String[] dayArray = publishArray[0].split("-");
            String[] timeArray = publishArray[1].split(":");

            // 示例时间
            LocalDateTime targetTime = LocalDateTime.of(Integer.parseInt(dayArray[0]),
                    Integer.parseInt(dayArray[1]),
                    Integer.parseInt(dayArray[2]),
                    Integer.parseInt(timeArray[0]),
                    Integer.parseInt(timeArray[1]),
                    Integer.parseInt(timeArray[2])
            );
            // 判断目标时间是否在当前时间的10分钟之内
            boolean isWithinOneHour = targetTime.isAfter(currentTime.minusMinutes(10)) && targetTime.isBefore(currentTime.plusMinutes(10));
            if (isWithinOneHour) {
                String contId = asJsonObject.get("contId").getAsString();
                String title = asJsonObject.get("name").getAsString();
                String url = String.format(thePaPerUrl, contId);
                if (!MonitorTheLatestInformationMap.containsKey(contId)) {
                    log.info("网易10分钟内发布文章\t{}:{}", title, url);
                    MonitorTheLatestInformationMap.put(contId, url);
                    return getMapCompletableFuture(url, title);
                }
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
        return null;
    }
}
