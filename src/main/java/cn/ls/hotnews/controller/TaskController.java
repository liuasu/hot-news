package cn.ls.hotnews.controller;

import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.common.ResultUtils;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.hotnews.HotNewsAddReq;
import cn.ls.hotnews.model.dto.task.TaskAddReq;
import cn.ls.hotnews.model.dto.task.TaskEditReq;
import cn.ls.hotnews.model.dto.task.TaskQueryReq;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.TaskVO;
import cn.ls.hotnews.service.TaskService;
import cn.ls.hotnews.service.UserService;
import cn.ls.hotnews.strategy.HotNewsStrategy;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 任务中心Controller
 *
 * @author ls
 * @createDate 2024-11-23 17:27:51
 */

@Api(tags = "任务中心")
@RestController
@RequestMapping("/task")
public class TaskController {

    @Resource
    private TaskService taskService;
    @Resource
    private UserService userService;
    @Resource
    private HotNewsStrategy hotNewsStrategy;


    /**
     * 查询任务中心列表
     */
    @ApiOperation("查询任务中心列表")
    @GetMapping("/list")
    public BaseResponse<Page<TaskVO>> list(TaskQueryReq taskQueryReq, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(taskService.findTaskList(taskQueryReq, loginUser));
    }


    /**
     * 添加任务中心
     */
    @ApiOperation("添加任务中心")
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody TaskAddReq taskAddReq, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(taskAddReq == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(taskService.addTask(taskAddReq, loginUser));
    }

    /**
     * 修改任务中心
     */
    @ApiOperation("修改任务中心")
    @PostMapping("/edit")
    public BaseResponse<Boolean> edit(@RequestBody TaskEditReq taskEditReq, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(taskEditReq == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(taskService.editTask(taskEditReq,loginUser));
    }

    /**
     * 删除任务中心
     */
    @ApiOperation("删除任务中心")
    @PostMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable("id") Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(id == null || id < 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(taskService.delById(id,loginUser));
    }

    /**
     * 查看热点相关文章
     */
    @ApiOperation("查看热点相关文章")
    @PostMapping("/query/article")
    public BaseResponse<Map<String, Object>> hotNewsQueryArticles(@RequestBody HotNewsAddReq hotNewsAddReq, HttpServletRequest request) {
        String title = hotNewsAddReq.getTitle();
        String hotURL = hotNewsAddReq.getHotURL();
        String platformName = hotNewsAddReq.getPlatformName();

        userService.getLoginUser(request);
        ThrowUtils.throwIf(title == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(hotURL == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(platformName == null, ErrorCode.PARAMS_ERROR);
        Map<String, Object> hotUrlGainNew = hotNewsStrategy.getHotNewsByPlatform(platformName).getHotUrlGainNew(hotNewsAddReq);
        return ResultUtils.success(hotUrlGainNew);
    }
}