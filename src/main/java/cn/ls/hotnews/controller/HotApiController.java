package cn.ls.hotnews.controller;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.ls.hotnews.annotation.AuthCheck;
import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.common.ResultUtils;
import cn.ls.hotnews.constant.FileConstant;
import cn.ls.hotnews.constant.UserConstant;
import cn.ls.hotnews.exception.BusinessException;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.file.UploadFileRequest;
import cn.ls.hotnews.model.dto.hotapi.HotApiQueryReq;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.enums.FileUploadBizEnum;
import cn.ls.hotnews.model.vo.HotApiVO;
import cn.ls.hotnews.service.UserService;
import cn.ls.hotnews.utils.ExcelUtils;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.service.HotApiService;
import cn.ls.hotnews.model.dto.hotapi.HotApiAddReq;
import cn.ls.hotnews.model.dto.hotapi.HotApiEditReq;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 热点信息接口地Controller
 *
 * @author ls
 * @createDate 2024-11-18 21:08:27
 */

@Api(tags = "热点信息接口地")
@RestController
@RequestMapping("/hotApi")
public class HotApiController {

    @Resource
    private HotApiService hotApiService;
    @Resource
    private UserService userService;

    /**
     * 查询热点信息接口地列表
     */
    @ApiOperation("查询热点信息接口地列表")
    @GetMapping("/list")
    public BaseResponse<List<HotApiVO>> list(HotApiQueryReq queryReq, HttpServletRequest request) {
        userService.getLoginUser(request);
        return ResultUtils.success(hotApiService.findHotApiList(queryReq));
    }

    /**
     * 按id获取热点信息接口地
     */
    @ApiOperation("按id获取热点信息接口地")
    @GetMapping("/{id}")
    public BaseResponse<HotApiVO> findHotApiById(@PathVariable("id") Long id, HttpServletRequest request) {
        userService.getLoginUser(request);
        ThrowUtils.throwIf(id < 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(hotApiService.toHotAPIVO(hotApiService.getById(id)));
    }

    /**
     * 添加热点信息接口地
     */
    @ApiOperation("添加热点信息接口地")
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> add(@RequestBody HotApiAddReq hotApiAddReq, HttpServletRequest request) {
        ThrowUtils.throwIf(hotApiAddReq == null, ErrorCode.PARAMS_ERROR);
        userService.getLoginUser(request);
        return ResultUtils.success(hotApiService.addHotApi(hotApiAddReq));
    }

    /**
     * excel添加
     *
     * @param multipartFile
     * @param request
     * @return
     */
    @PostMapping("/excel/add")
    public BaseResponse<Boolean> excelAdd(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) {
        userService.getLoginUser(request);
        ThrowUtils.throwIf(multipartFile==null,ErrorCode.PARAMS_ERROR);
        List<HotApi> hotApiList = ExcelUtils.exceltoHotAPIList(multipartFile);
        return ResultUtils.success(hotApiService.saveBatch(hotApiList));
    }

    /**
     * 修改热点信息接口地
     */
    @ApiOperation("修改热点信息接口地")
    @PostMapping("/edit")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> edit(@RequestBody HotApiEditReq hotApiEditReq, HttpServletRequest request) {
        ThrowUtils.throwIf(hotApiEditReq == null, ErrorCode.PARAMS_ERROR);
        userService.getLoginUser(request);
        return ResultUtils.success(hotApiService.editHotApi(hotApiEditReq));
    }

    /**
     * 删除热点信息接口地
     */
    @ApiOperation("删除热点信息接口地")
    @PostMapping("/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> edit(@PathVariable("id") Long id, HttpServletRequest request) {
        userService.getLoginUser(request);
        ThrowUtils.throwIf(id < 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(hotApiService.delById(id));
    }
}