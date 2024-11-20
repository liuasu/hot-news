package cn.ls.hotnews.service;

import cn.ls.hotnews.model.vo.HotNewsVO;

import java.util.List;

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
}
