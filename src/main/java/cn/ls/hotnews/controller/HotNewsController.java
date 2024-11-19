package cn.ls.hotnews.controller;

import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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



    @GetMapping("/toutiao")
    @ApiOperation("头条热点")
    public BaseResponse<List<Object>> touTiaoHotNews(){

        return ResultUtils.success(new ArrayList<>());
    }
}
