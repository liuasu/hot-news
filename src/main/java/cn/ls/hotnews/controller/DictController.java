package cn.ls.hotnews.controller;

import cn.ls.hotnews.annotation.AuthCheck;
import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ResultUtils;
import cn.ls.hotnews.constant.UserConstant;
import cn.ls.hotnews.model.dto.dict.DictAddReq;
import cn.ls.hotnews.model.dto.dict.DictEditReq;
import cn.ls.hotnews.model.entity.Dict;
import cn.ls.hotnews.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
*
* 字典Controller
* @createDate 2024-12-31 11:00:38
* @author ls
*/

@Api(tags = "字典")
@RestController
@RequestMapping("/dict")
public class DictController {

    @Resource
    private DictService dictService;

    /**
    * 查询字典列表
    */
    @ApiOperation("查询字典列表")
    @GetMapping("/list")
    public BaseResponse<List<Dict>> list(Dict dict){
        return ResultUtils.success(dictService.findDictList(dict));
    }

    /**
    *  按id获取字典
    */
    @ApiOperation("按id获取字典")
    @GetMapping("/{id}")
    public BaseResponse<Dict> findDictById(@PathVariable("id") Long id){
    return ResultUtils.success(dictService.getById(id));
    }

    /**
    *  添加字典
    */
    @ApiOperation("添加字典")
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> add(@RequestBody DictAddReq dictAddReq){
        return ResultUtils.success(dictService.addDict(dictAddReq));
    }

    /**
    *  修改字典
    */
    @ApiOperation("修改字典")
    @PostMapping("/edit")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> edit(@RequestBody DictEditReq dictEditReq){
        return ResultUtils.success(dictService.editDict(dictEditReq));
    }

    /**
    *  删除字典
    */
    @ApiOperation("删除字典")
    @PostMapping("/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> delete(@PathVariable("id") Long id){
        return ResultUtils.success(dictService.delById(id));
    }
}