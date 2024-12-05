package cn.ls.hotnews.controller;

import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.common.ResultUtils;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.aiconfig.AiConfigAddReq;
import cn.ls.hotnews.model.dto.aiconfig.AiConfigEditReq;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.AiConfigVO;
import cn.ls.hotnews.service.AiConfigService;
import cn.ls.hotnews.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * ai 秘钥配Controller
 *
 * @author ls
 * @createDate 2024-12-05 11:16:10
 */

@Api(tags = "ai 秘钥配")
@RestController
@RequestMapping("/aiConfig")
public class AiConfigController {

    @Resource
    private AiConfigService aiConfigService;
    @Resource
    private UserService userService;

    /**
     * 查询ai 秘钥配列表
     */
    @ApiOperation("查询ai 秘钥配列表")
    @GetMapping("/list")
    public BaseResponse<List<AiConfigVO>> list(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(aiConfigService.findAiConfigList(loginUser));
    }

    /**
     * 按id获取ai 秘钥配
     */
    @ApiOperation("按id获取ai 秘钥配")
    @GetMapping("/{id}")
    public BaseResponse<AiConfigVO> findAiConfigById(@PathVariable("id") Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(aiConfigService.AIConfigToVO(aiConfigService.getById(id)));
    }

    /**
     * 添加ai 秘钥配
     */
    @ApiOperation("添加ai 秘钥配")
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody AiConfigAddReq aiConfigAddReq, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(aiConfigAddReq == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(aiConfigService.addAiConfig(aiConfigAddReq, loginUser));
    }

    /**
     * 修改ai 秘钥配
     */
    @ApiOperation("修改ai 秘钥配")
    @PostMapping("/edit")
    public BaseResponse<Boolean> edit(@RequestBody AiConfigEditReq aiConfigEditReq, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(aiConfigEditReq == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(aiConfigService.editAiConfig(aiConfigEditReq, loginUser));
    }

    /**
     * 删除ai 秘钥配
     */
    @ApiOperation("删除ai 秘钥配")
    @PostMapping("/{id}")
    public BaseResponse<Boolean> edit(@PathVariable("id") Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(aiConfigService.delById(id, loginUser));
    }
}