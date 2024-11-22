package cn.ls.hotnews.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.enums.HotPlatformEnum;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.vo.HotNewsVO;
import cn.ls.hotnews.service.HotApiService;
import cn.ls.hotnews.service.HotNewsService;
import cn.ls.hotnews.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.ls.hotnews.constant.CommonConstant.REDIS_TOUTIAO;
import static cn.ls.hotnews.constant.CommonConstant.REDIS_TOUTIAO_DTATETIME;

/**
 * title: TouTiaoHotNewsServiceImpl
 * author: liaoshuo
 * date: 2024/11/19 20:35
 * description:
 */
@Slf4j
@Component("toutiao")
public class TouTiaoHotNewsServiceImpl implements HotNewsService {

    @Resource
    private HotApiService hotApiService;
    private final String URL = "https://www.toutiao.com/trending/%s/";
    @Resource
    private RedisUtils redisUtils;
    //private final String mobileUrl= "https://api.toutiaoapi.com/feoffline/amos_land/new/html/main/index.html?topic_id=%s";

    /**
     * 热点新闻列表
     *
     * @return
     */
    @Override
    public List<HotNewsVO> hotNewsList() {
        List<HotNewsVO> touTiaoList = (List<HotNewsVO>) redisUtils.redisGet(REDIS_TOUTIAO);
        if (touTiaoList != null) {
            return touTiaoList;
        }
        touTiaoList = new ArrayList<>();
        HotPlatformEnum toutiao = HotPlatformEnum.TOUTIAO;
        HotApi platformAPI = hotApiService.getPlatformAPI(toutiao.getValues());
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        try {
            String touTiaoHotJsonStr = HttpUtil.get(platformAPI.getApiURL());
            if (StringUtils.isBlank(touTiaoHotJsonStr)) {
                return new ArrayList<>();
            }
            JSONArray data = JSONUtil.parseObj(touTiaoHotJsonStr).getJSONArray("data");
            for (int i = 0; i < 20; i++) {
                Map<String, Object> map = (Map<String, Object>) data.get(i);
                Map<String, Object> imageMap = (Map<String, Object>) map.get("Image");
                HotNewsVO hotNewsVO = new HotNewsVO();
                String str = (String) map.get("ClusterIdStr");
                Long clusterIdStr = Long.parseLong(str);
                hotNewsVO.setId(clusterIdStr);
                hotNewsVO.setTitle((String) map.get("Title"));
                hotNewsVO.setHotURL(String.format(URL, clusterIdStr));
                hotNewsVO.setImageURL((String) imageMap.get("url"));
                hotNewsVO.setHotDesc((String) map.get("LabelDesc"));
                touTiaoList.add(hotNewsVO);
            }
            redisUtils.redisSetInOneHour(REDIS_TOUTIAO, touTiaoList);
            redisUtils.redisSetInOneHour(REDIS_TOUTIAO_DTATETIME,new DateTime());
        } catch (Exception e) {
            log.error("toutiao hot news error:\t", e);
            throw new RuntimeException(e);
        }
        return touTiaoList;
    }
}
