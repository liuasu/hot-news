package cn.ls.hotnews.controller;

import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.common.ResultUtils;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.hotnews.HotNewsQueryReq;
import cn.ls.hotnews.model.vo.HotNewsVO;
import cn.ls.hotnews.service.UserService;
import cn.ls.hotnews.strategy.HotNewsStrategy;
import cn.ls.hotnews.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static cn.ls.hotnews.constant.CommonConstant.*;

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
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserService userService;


    //@GetMapping("/toutiao")
    //@ApiOperation("头条热点")
    public BaseResponse<List<HotNewsVO>> touTiaoHotNews(HttpServletRequest request) {
        userService.getLoginUser(request);
        List<HotNewsVO> hotNewsVOList = hotNewsStrategy.getHotNewsByPlatform(TOUTIAO).hotNewsList();
        return ResultUtils.success(hotNewsVOList, redisUtils.redisGetOneHourTime(REDIS_BILIBILI_DTATETIME));
    }

    //@GetMapping("/dy")
    //@ApiOperation("抖音热点")
    public BaseResponse<List<HotNewsVO>> DyHotNews(HttpServletRequest request) {
        userService.getLoginUser(request);
        List<HotNewsVO> hotNewsVOList = hotNewsStrategy.getHotNewsByPlatform(DOUYIN).hotNewsList();
        return ResultUtils.success(hotNewsVOList, redisUtils.redisGetOneHourTime(REDIS_DY_DTATETIME));
    }

    //@GetMapping("/bilibili")
    //@ApiOperation("bilibili热点")
    public BaseResponse<List<HotNewsVO>> BiLiBiLiHotNews(HttpServletRequest request) {
        userService.getLoginUser(request);
        List<HotNewsVO> hotNewsVOList = hotNewsStrategy.getHotNewsByPlatform(BILIBILI).hotNewsList();
        return ResultUtils.success(hotNewsVOList, redisUtils.redisGetOneHourTime(REDIS_BILIBILI_DTATETIME));
    }

    @GetMapping("/thepaper")
    @ApiOperation("澎湃热点")
    public BaseResponse<List<HotNewsVO>> ThePaPerHotNews(HttpServletRequest request) {
        userService.getLoginUser(request);
        List<HotNewsVO> hotNewsVOList = hotNewsStrategy.getHotNewsByPlatform(THEPAPER).hotNewsList();
        return ResultUtils.success(hotNewsVOList, redisUtils.redisGetOneHourTime(REDIS_THEPAPER_DTATETIME));
    }

    @GetMapping("/thirtysix")
    @ApiOperation("36氪热点")
    public BaseResponse<List<HotNewsVO>> ThirtySixKRHotNews(HttpServletRequest request) {
        userService.getLoginUser(request);
        List<HotNewsVO> hotNewsVOList = hotNewsStrategy.getHotNewsByPlatform(ThirtySixKR).hotNewsList();
        return ResultUtils.success(hotNewsVOList, redisUtils.redisGetOneHourTime(REDIS_THEPAPER_DTATETIME));
    }

    @GetMapping("/wangyi")
    @ApiOperation("网易热点")
    public BaseResponse<List<HotNewsVO>> WangYiHotNews(HttpServletRequest request) {
        userService.getLoginUser(request);
        List<HotNewsVO> hotNewsVOList = hotNewsStrategy.getHotNewsByPlatform(WANGYI).hotNewsList();
        return ResultUtils.success(hotNewsVOList, redisUtils.redisGetOneHourTime(REDIS_WANGYI_DTATETIME));
    }

    @PostMapping("/wangyi2")
    @ApiOperation("网易热点2")
    public BaseResponse<Map<String, Object>> WangYiHotNews2(@RequestBody HotNewsQueryReq hotNewsQueryReq, HttpServletRequest request) {
        userService.getLoginUser(request);
        ThrowUtils.throwIf(hotNewsQueryReq==null, ErrorCode.PARAMS_ERROR);
        Map<String, Object> map = hotNewsStrategy.getHotNewsByPlatform(WANGYI).hotNewsList(hotNewsQueryReq);
        return ResultUtils.success(map, redisUtils.redisGetOneHourTime(REDIS_WANGYI_DTATETIME));
    }

    @GetMapping("/qq_news")
    @ApiOperation("腾讯新闻热点")
    public BaseResponse<List<HotNewsVO>> QQNewsHotNews(HttpServletRequest request) {
        userService.getLoginUser(request);
        List<HotNewsVO> hotNewsVOList = hotNewsStrategy.getHotNewsByPlatform(QQNEWS).hotNewsList();
        return ResultUtils.success(hotNewsVOList, redisUtils.redisGetOneHourTime(REDIS_QQNEWS_DTATETIME));
    }
}
