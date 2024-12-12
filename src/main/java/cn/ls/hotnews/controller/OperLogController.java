package cn.ls.hotnews.controller;

import cn.ls.hotnews.annotation.AuthCheck;
import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ResultUtils;
import cn.ls.hotnews.constant.UserConstant;
import cn.ls.hotnews.model.dto.log.LogQueryRes;
import cn.ls.hotnews.model.entity.OperLog;
import cn.ls.hotnews.service.OperLogService;
import cn.ls.hotnews.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
*
* 操作日志记Controller
* @createDate 2024-12-11 15:04:25
* @author ls
*/

@Api(tags = "操作日志记")
@RestController
@RequestMapping("/operLog")
public class OperLogController {

    @Resource
    private OperLogService operLogService;
    @Resource
    private UserService userService;

    /**
    * 查询操作日志记列表
    */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation("查询操作日志记列表")
    @GetMapping("/list")
    public BaseResponse<Page<OperLog>> list(LogQueryRes operLog, HttpServletRequest request){
        userService.getLoginUser(request);
        return ResultUtils.success(operLogService.findOperLogList(operLog));
    }
}