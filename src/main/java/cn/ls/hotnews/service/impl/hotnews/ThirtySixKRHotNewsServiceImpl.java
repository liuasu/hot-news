package cn.ls.hotnews.service.impl.hotnews;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.BusinessException;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.hotnews.HotNewsAddReq;
import cn.ls.hotnews.model.dto.hotnews.HotNewsQueryReq;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.vo.ArticleVO;
import cn.ls.hotnews.model.vo.HotNewsVO;
import cn.ls.hotnews.service.HotApiService;
import cn.ls.hotnews.service.HotNewsService;
import cn.ls.hotnews.utils.CommonUtils;
import cn.ls.hotnews.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.ls.hotnews.constant.CommonConstant.REDIS_ThirtySixKR;
import static cn.ls.hotnews.constant.CommonConstant.REDIS_ThirtySixKR_DTATETIME;

/**
 * title: ThirtySixKRHotNewsServiceImpl
 * author: liaoshuo
 * date: 2024/12/3 10:40
 * description: 36氪
 */
@Slf4j
@Service("thirtysix")
public class ThirtySixKRHotNewsServiceImpl extends HotNewsCommonAbstract implements HotNewsService {

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private HotApiService hotApiService;


    /**
     * 热点新闻列表
     * <a href="https://gateway.36kr.com/api/mis/nav/home/nav/rank/{type}"></a>
     * type: {
     * hot: "人气榜",默认为hot
     * video: "视频榜",
     * comment: "热议榜",
     * collect: "收藏榜",
     * }
     *
     * @return {@link List }<{@link HotNewsVO }>
     */
    @Override
    public List<HotNewsVO> hotNewsList() {
        String thirtySixKey = REDIS_ThirtySixKR;
        List<HotNewsVO> hotNewsVOList = redisUtils.redisGet(thirtySixKey);
        if (hotNewsVOList != null) {
            return hotNewsVOList;
        }
        HotApi platformAPI = hotApiService.getPlatformAPI("thirtysix");
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        try {
            String krStr = HttpUtil.createPost("https://gateway.36kr.com/api/mis/nav/home/nav/rank/hot")
                    .header("Content-Type", "application/json; charset=utf-8")
                    .body(JSONUtil.toJsonStr(getEntries()))
                    .execute().body();
            Object dataJson = JSONUtil.parseObj(JSONUtil.parseObj(krStr)).get("data");
            List<Object> hotRankList = (List<Object>) JSONUtil.parseObj(dataJson).get("hotRankList");
            hotNewsVOList = new ArrayList<>();
            String thirtySixUrl = "https://www.36kr.com/p/%s";
            for (Object o : hotRankList) {
                Map<String, Object> map = (Map<String, Object>) o;
                Map<String, Object> templateMaterialMap = (Map<String, Object>) map.get("templateMaterial");
                HotNewsVO hotNewsVO = new HotNewsVO();
                Object itemId = templateMaterialMap.get("itemId");
                hotNewsVO.setId((Long) itemId);
                hotNewsVO.setTitle((String) templateMaterialMap.get("widgetTitle"));
                hotNewsVO.setHotURL(String.format(thirtySixUrl, itemId));
                hotNewsVO.setImageURL((String) templateMaterialMap.get("widgetImage"));
                hotNewsVOList.add(hotNewsVO);
            }
            redisUtils.redisSetInOneHour(thirtySixKey, hotNewsVOList);
            redisUtils.redisSetInOneHour(REDIS_ThirtySixKR_DTATETIME, new DateTime());
        } catch (Exception e) {
            log.error("ThirtySixKR news is error, message:{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "36氪获取失败");
        }
        return hotNewsVOList;
    }


    /**
     * @param hotNewsQueryReq
     * @return
     */
    @Override
    public Map<String, Object> hotNewsList(HotNewsQueryReq hotNewsQueryReq) {
        return null;
    }

    private JSONObject getEntries() {
        JSONObject paramObject = new JSONObject();
        paramObject.set("siteId", 1);
        paramObject.set("platformId", 2);
        JSONObject bodyObject = new JSONObject();
        bodyObject.set("partner_id", "wap");
        bodyObject.set("param", paramObject);
        bodyObject.set("timestamp", System.currentTimeMillis());
        return bodyObject;
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

        String title = doc.getElementsByClass("article-title margin-bottom-20 common-width").text();
        Elements elementsByClass = doc.getElementsByClass("common-width content articleDetailContent kr-rich-text-wrapper");
        elementsByClass.select("strong").remove();
        elementsByClass.select("a").remove();
        System.out.println(title);
        System.out.println(CommonUtils.cleanText(elementsByClass.text()));
        for (Element byClass : elementsByClass) {
            imgList.add(byClass.getElementsByTag("img").attr("src"));
        }
        articleVO.setTitle(title);
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
