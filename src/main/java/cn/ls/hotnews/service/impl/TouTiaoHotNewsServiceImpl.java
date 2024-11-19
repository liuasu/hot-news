package cn.ls.hotnews.service.impl;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    /**
     * 热点新闻列表
     *
     * @return
     */
    @Override
    public List<HotNewsVO> hotNewsList() {
        List<HotNewsVO> touTiaoList = new ArrayList<>();
        HotPlatformEnum toutiao = HotPlatformEnum.TOUTIAO;
        HotApi platformAPI = hotApiService.getPlatformAPI(toutiao.getValues());
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        try {
            String touTiaoHotJsonStr = HttpUtil.get(platformAPI.getApiURL());
            JSONArray data = JSONUtil.parseObj(touTiaoHotJsonStr).getJSONArray("data");
            for (Object datum : data) {
                Map<String, Object> map = (Map<String, Object>) datum;
                Map<String, Object> imageMap = (Map<String, Object>) map.get("Image");
                HotNewsVO hotNewsVO = new HotNewsVO();
                hotNewsVO.setId((Long) map.get("ClusterId"));
                hotNewsVO.setTitle((String) map.get("Title"));
                hotNewsVO.setHotURL((String) map.get("Url"));
                hotNewsVO.setImageURL((String) imageMap.get("url"));
                hotNewsVO.setHotDesc((String) map.get("LabelDesc"));
                touTiaoList.add(hotNewsVO);
            }
        } catch (Exception e) {
            log.error("toutiao hot news error:\t",e);
            throw new RuntimeException(e);
        }
        return touTiaoList;
    }
}
