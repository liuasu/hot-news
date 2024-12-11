package cn.ls.hotnews.controller;

import cn.ls.hotnews.annotation.AuthCheck;
import cn.ls.hotnews.constant.UserConstant;
import cn.ls.hotnews.model.entity.OperLog;
import cn.ls.hotnews.service.OperLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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

    /**
    * 查询操作日志记列表
    */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation("查询操作日志记列表")
    @GetMapping("/list")
    public List<OperLog> list(OperLog operLog){
        return operLogService.findOperLogList(operLog);
    }
}