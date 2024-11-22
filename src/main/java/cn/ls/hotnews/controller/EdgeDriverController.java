package cn.ls.hotnews.controller;

import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ResultUtils;
import cn.ls.hotnews.service.UserService;
import cn.ls.hotnews.strategy.EdgeDriverStrategy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static cn.ls.hotnews.constant.CommonConstant.TOUTIAO_EDGE_DRIVER_LOGIN;

/**
 * title: EdgeDriverController
 * author: liaoshuo
 * date: 2024/11/19 14:09
 * description: 平台登录(头条、百家)
 */
@Api(tags = "平台登录")
@RestController
@RequestMapping("/edge/driver")
public class EdgeDriverController {

    @Resource
    private EdgeDriverStrategy edgeDriverStrategy;
    @Resource
    private UserService userService;


    @ApiOperation("头条号登录")
    @GetMapping("/toutiao/login")
    public BaseResponse<Boolean> touTiaoLogin(HttpServletRequest request){
        userService.getLoginUser(request);
        edgeDriverStrategy.getEdgeDriverKey(TOUTIAO_EDGE_DRIVER_LOGIN).EdgeDriverPlatFormLogin();
        return ResultUtils.success(true);
    }



}
