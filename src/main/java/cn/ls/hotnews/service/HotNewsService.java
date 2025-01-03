package cn.ls.hotnews.service;

import cn.ls.hotnews.model.dto.hotnews.HotNewsAddReq;
import cn.ls.hotnews.model.dto.hotnews.HotNewsQueryReq;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.vo.HotNewsVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * title: HotNewsService
 * author: liaoshuo
 * date: 2024/11/19 20:20
 * description:
 */
public interface HotNewsService {

    /**
     * 热点新闻列表
     *
     * @return {@link List }<{@link HotNewsVO }>
     */
    List<HotNewsVO> hotNewsList();
    Map<String,Object> hotNewsList(HotNewsQueryReq hotNewsQueryReq);

    /**
     * 根据热点链接获取相关文章
     *
     * @param req 要求
     * @return {@link Map }<{@link String }, {@link String }>
     */
    Map<String, Object> getHotUrlGainNew(HotNewsAddReq req);

    /**
     * 提取响应信息
     *
     * @param responsesInfo 回复信息
     * @param currentTime   当前时间
     */
    Map<String,Object> extractResponseInfo(String responsesInfo, LocalDateTime currentTime);

    /**
     * 提取 urlinfo
     *
     * @param hotApi 热门 API
     * @return {@link String }
     */
    String extractURLInfo(HotApi hotApi);


}
