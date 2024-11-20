package cn.ls.hotnews.service.impl;

import cn.ls.hotnews.model.vo.HotNewsVO;
import cn.ls.hotnews.service.HotNewsService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * title: BiliBiliHotNewsServiceImpl
 * author: liaoshuo
 * date: 2024/11/20 12:12
 * description:
 */
@Component("bilibili")
public class BiliBiliHotNewsServiceImpl implements HotNewsService {

    //type: {
    //    name: "排行榜分区",
    //    type: {
    //      0: "全站",
    //      1: "动画",
    //      3: "音乐",
    //      4: "游戏",
    //      5: "娱乐",
    //      36: "科技",
    //      119: "鬼畜",
    //      129: "舞蹈",
    //      155: "时尚",
    //      160: "生活",
    //      168: "国创相关",
    //      188: "数码",
    //      181: "影视",
    //    },
    //}
    //
    //https://api.bilibili.com/x/web-interface/ranking/v2?tid=${type}(这里接分区id)&type=all&${wbiData}



    /**
     * 热点新闻列表
     *
     * @return
     */
    @Override
    public List<HotNewsVO> hotNewsList() {
        return null;
    }
}
