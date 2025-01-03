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
import java.util.*;

import static cn.ls.hotnews.constant.CommonConstant.*;

/**
 * title: WangYiHotNewsServiceImpl
 * author: liaoshuo
 * date: 2024/12/9 13:11
 * description: 网易新闻
 */
@Slf4j
@Service("wangyi")
public class WangYiHotNewsServiceImpl extends HotNewsCommonAbstract implements HotNewsService {
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
        String Key = REDIS_WANGYI;
        List<HotNewsVO> hotNewsVOList = redisUtils.redisGet(Key);
        if (hotNewsVOList != null) {
            return hotNewsVOList;
        }
        HotApi platformAPI = hotApiService.getPlatformAPI("wangyi");
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        try {
            String str = HttpUtil.get(platformAPI.getApiURL());
            Object JsonData = JSONUtil.parseObj(str).get("data");
            List<Object> objList = (List<Object>) JSONUtil.parseObj(JsonData).get("list");
            hotNewsVOList = new ArrayList<>();
            String url = "https://www.163.com/dy/article/%s.html";
            for (int i = 0; i < 30; i++) {
                Map<String, Object> map = (Map<String, Object>) objList.get(i);
                HotNewsVO hotNewsVO = new HotNewsVO();
                String docid = (String) map.get("docid");
                hotNewsVO.setBiId(docid);
                hotNewsVO.setTitle((String) map.get("title"));
                hotNewsVO.setHotURL(String.format(url, docid));
                hotNewsVO.setImageURL((String) map.get("imgsrc"));
                hotNewsVOList.add(hotNewsVO);
            }
            redisUtils.redisSetInOneHour(Key, hotNewsVOList);
            redisUtils.redisSetInOneHour(REDIS_WANGYI_DTATETIME, new DateTime());
        } catch (Exception e) {
            log.error("wangyi news is error, message:{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "网易新闻热点获取失败");
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
            List<HotNewsVO> list = new ArrayList<>();
            HotApi platformAPI = hotApiService.getPlatformAPI(hotType);
            ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
            String str = HttpUtil.get(platformAPI.getApiURL());
            str = str.substring(str.indexOf("(") + 1, str.lastIndexOf(")"))
                    .replaceAll(" ", "");
            for (JsonElement item : JsonParser.parseString(str).getAsJsonArray()) {
                JsonObject asJsonObject = item.getAsJsonObject();

                String title = asJsonObject.get("title").getAsString();
                String docUrl = asJsonObject.get("docurl").getAsString();
                String docId = docUrl.substring(docUrl.lastIndexOf("/") + 1, docUrl.indexOf(".html"));

                HotNewsVO hotNewsVO = new HotNewsVO();
                hotNewsVO.setBiId(docId);
                hotNewsVO.setTitle(title);
                hotNewsVO.setHotURL(docUrl);
                list.add(hotNewsVO);
            }
            map.put("newsList", list);
        }
        List<HotApiVO> hotApiVOList = hotApiService.getPlatFormByLikeRightAPI("wangyi_");
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
        String postTitle = doc.getElementsByClass("post_title").text();
        String postBody = doc.getElementsByClass("post_body").text();
        Elements select = doc.select(".f_center");
        for (Element element : select) {
            imgList.add(element.getElementsByTag("img").attr("src"));
        }
        articleVO.setTitle(postTitle);
        articleVO.setConText(CommonUtils.cleanText(postBody));
        articleVO.setImgList(imgList);
        return articleVO;
    }

    /**
     * 提取响应信息
     *
     * @param responsesInfo 回复信息
     * @param currentTime   当前时间
     */
    @Override
    public Map<String, Object> extractResponseInfo(String responsesInfo, LocalDateTime currentTime) {
        String str = responsesInfo.substring(responsesInfo.indexOf("(") + 1, responsesInfo.lastIndexOf(")"));
        str = str.replaceAll("\n", "").replaceAll(" ", "");
        return getLastHotInfo(str, currentTime);
    }


    /**
     * 获取最新热门信息
     *
     * @param str         str
     * @param currentTime 当前时间
     */
    private Map<String, Object> getLastHotInfo(String str, LocalDateTime currentTime) {
        for (JsonElement item : JsonParser.parseString(str).getAsJsonArray()) {
            JsonObject asJsonObject = item.getAsJsonObject();
            String dataTimeStr = asJsonObject.get("time").getAsString();
            String data = dataTimeStr.substring(0, dataTimeStr.lastIndexOf("/") + 5);
            String time = dataTimeStr.substring(dataTimeStr.lastIndexOf("/") + 5);
            String[] dateArry = data.split("/");
            String[] newsTimeArry = time.split(":");

            LocalDateTime targetTime = LocalDateTime.of(Integer.parseInt(dateArry[2]),
                    Integer.parseInt(dateArry[0]),
                    Integer.parseInt(dateArry[1]),
                    Integer.parseInt(newsTimeArry[0]),
                    Integer.parseInt(newsTimeArry[1]),
                    Integer.parseInt(newsTimeArry[2])
            ); // 示例时间

            // 判断目标时间是否在当前时间的10分钟之内
            boolean isWithinOneHour = targetTime.isAfter(currentTime.minusMinutes(10)) && targetTime.isBefore(currentTime.plusMinutes(10));
            if (isWithinOneHour) {
                String title = asJsonObject.get("title").getAsString();
                String docUrl = asJsonObject.get("docurl").getAsString();
                String docId = docUrl.substring(docUrl.lastIndexOf("/") + 1, docUrl.indexOf(".html"));
                //判断 MonitorTheLatestInformationMap 是否有刚刚更新的
                if (!MonitorTheLatestInformationMap.containsKey(docId)) {
                    log.info("网易10分钟内发布文章\t{}:{}", title, docUrl);
                    MonitorTheLatestInformationMap.put(docId, docUrl);
                    return getMapCompletableFuture(docUrl, title);
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
        return Objects.requireNonNull(CommonUtils.doSecureGet(hotApi.getApiURL())).replaceAll(" ", "");
    }
}
