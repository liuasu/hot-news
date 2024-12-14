package cn.ls.hotnews.controller;

import cn.ls.hotnews.annotation.AuthCheck;
import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.common.PageRequest;
import cn.ls.hotnews.common.ResultUtils;
import cn.ls.hotnews.constant.UserConstant;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.prompt.PromptAddReq;
import cn.ls.hotnews.model.dto.prompt.PromptEditReq;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.PromptVO;
import cn.ls.hotnews.service.PromptService;
import cn.ls.hotnews.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * ai提示词Controller
 *
 * @author ls
 * @createDate 2024-12-05 11:16:10
 */

@Api(tags = "ai提示词")
@RestController
@RequestMapping("/prompt")
public class PromptController {

    @Resource
    private PromptService promptService;
    @Resource
    private UserService userService;

    /**
     * 查询ai提示词列表
     */
    @ApiOperation("查询ai提示词列表")
    @GetMapping("/list")
    public BaseResponse<List<PromptVO>> list(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(promptService.findPromptList(loginUser));
    }

    /**
     * 查询ai提示词列表
     */
    @ApiOperation("查询ai提示词列表")
    @GetMapping("/admin/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<PromptVO>> list(PageRequest pageRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(promptService.findPromptList(pageRequest, loginUser));
    }

    @ApiOperation("按id查询提示词")
    @GetMapping("/{id}")
    public BaseResponse<PromptVO> getById(@PathVariable("id") Long id, HttpServletRequest request) {
        userService.getLoginUser(request);
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(promptService.PromptToVO(promptService.getById(id)));
    }

    /**
     * 添加ai提示词
     */
    @ApiOperation("添加ai提示词")
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody PromptAddReq promptAddReq, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(promptAddReq == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(promptService.addPrompt(promptAddReq, loginUser));
    }

    /**
     * 修改ai提示词
     */
    @ApiOperation("修改ai提示词")
    @PostMapping("/edit")
    public BaseResponse<Boolean> edit(@RequestBody PromptEditReq promptEditReq, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(promptEditReq == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(promptService.editPrompt(promptEditReq, loginUser));
    }

    /**
     * 删除ai提示词
     */
    @ApiOperation("删除ai提示词")
    @PostMapping("/delete//{id}")
    public BaseResponse<Boolean> delete(@PathVariable("id") Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(id == null ||id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(promptService.delById(id, loginUser));
    }
}