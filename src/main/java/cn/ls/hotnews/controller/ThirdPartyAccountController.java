package cn.ls.hotnews.controller;

import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ResultUtils;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.ThirdPartyAccountVO;
import cn.ls.hotnews.service.ThirdPartyAccountService;
import cn.ls.hotnews.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * title: ThirdPartyAccountController
 * author: liaoshuo
 * date: 2024/11/23 20:35
 * description: 第三方账号
 */

@Api(tags = "账号中心")
@RestController
@RequestMapping("/third-party")
public class ThirdPartyAccountController {

    @Resource
    private UserService userService;
    @Resource
    private ThirdPartyAccountService thirdPartyAccountService;


    @ApiOperation("获取第三方账号集合")
    @GetMapping("/list")
    public BaseResponse<List<ThirdPartyAccountVO>> getThirdPartyAccountList(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
    return ResultUtils.success(thirdPartyAccountService.getThirdPartyAccountList(loginUser));
    }
}
