package cn.ls.hotnews.controller;

import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ResultUtils;
import cn.ls.hotnews.enums.HotPlatformEnum;
import cn.ls.hotnews.model.vo.HotNewsVO;
import cn.ls.hotnews.strategy.HotNewsStrategy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * title: HotNowsController
 * author: liaoshuo
 * date: 2024/11/19 14:09
 * description:
 */
@Api(tags = "平台热点")
@RestController
@RequestMapping("/hot_new")
public class HotNewsController {

    @Resource
    private HotNewsStrategy hotNewsStrategy;


    @GetMapping("/toutiao")
    @ApiOperation("头条热点")
    public BaseResponse<List<HotNewsVO>> touTiaoHotNews(){
        List<HotNewsVO> hotNewsVOList = hotNewsStrategy.getHotNewsByPlatform(HotPlatformEnum.TOUTIAO.getValues()).hotNewsList();
        return ResultUtils.success(hotNewsVOList);
    }

    @GetMapping("/dy")
    @ApiOperation("抖音热点")
    public BaseResponse<List<HotNewsVO>> DyHotNews(){
        List<HotNewsVO> hotNewsVOList = hotNewsStrategy.getHotNewsByPlatform(HotPlatformEnum.DOUYIN.getValues()).hotNewsList();
        return ResultUtils.success(hotNewsVOList);
    }

    @GetMapping("/bilibili")
    @ApiOperation("bilibili热点")
    public BaseResponse<List<HotNewsVO>> BiLiBiLiHotNews(){
        List<HotNewsVO> hotNewsVOList = hotNewsStrategy.getHotNewsByPlatform(HotPlatformEnum.BILIBILI.getValues()).hotNewsList();
        return ResultUtils.success(hotNewsVOList);
    }
}
