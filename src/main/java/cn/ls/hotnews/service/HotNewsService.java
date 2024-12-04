package cn.ls.hotnews.service;

import cn.ls.hotnews.model.dto.hotnews.HotNewsAddReq;
import cn.ls.hotnews.model.vo.HotNewsVO;

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

    /**
     * 根据热点链接获取相关文章
     *
     * @param req 要求
     * @return {@link Map }<{@link String }, {@link String }>
     */
    Map<String, Object> getHotUrlGainNew(HotNewsAddReq req);
}
