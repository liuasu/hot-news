package cn.ls.hotnews.controller;

import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.common.ResultUtils;
import cn.ls.hotnews.enums.ChromePlatFormEnum;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountAddReq;
import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountDelReq;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.AccountCentreVO;
import cn.ls.hotnews.model.vo.ThirdPartyAccountVO;
import cn.ls.hotnews.service.ThirdPartyAccountService;
import cn.ls.hotnews.service.UserService;
import cn.ls.hotnews.strategy.ChromeDriverStrategy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

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
    @Resource
    private ChromeDriverStrategy edgeDriverStrategy;


    /**
     * 获取第三方账户列表 - 任务中心获取
     *
     * @param request 请求
     * @return {@link BaseResponse }<{@link List }<{@link ThirdPartyAccountVO }>>
     */
    @ApiOperation("获取第三方账户列表 - 任务中心获取")
    @GetMapping("/list")
    public BaseResponse<List<ThirdPartyAccountVO>> getThirdPartyAccountList(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
    return ResultUtils.success(thirdPartyAccountService.getThirdPartyAccountList(loginUser));
    }

    /**
     * 获取第三方账户列表 - 账号中心
     *
     * @param request 请求
     * @return {@link BaseResponse }<{@link List }<{@link ThirdPartyAccountVO }>>
     */
    @ApiOperation("获取第三方账户列表 - 账号中心")
    @GetMapping("/account_centre/list")
    public BaseResponse<List<AccountCentreVO>> getThirdPartyAccountListByAccountCentre(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        List<AccountCentreVO> list= thirdPartyAccountService.getThirdPartyAccountListByAccountCentre(loginUser);
        return ResultUtils.success(list);
    }


    @ApiOperation("账号登录")
    @PostMapping("/add")
    public BaseResponse<Boolean> addThirdPartyAccountList(@RequestBody ThirdPartyAccountAddReq addReq, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        String thirdPartyFormName = addReq.getThirdPartyFormName();
        ThrowUtils.throwIf(thirdPartyFormName ==null, ErrorCode.PARAMS_ERROR);
        String values = Objects.requireNonNull(ChromePlatFormEnum.getValuesByName(thirdPartyFormName)).getValues();
        //考虑这里做异步
        edgeDriverStrategy.getChromeDriverKey(values).ChromeDriverPlatFormLogin(loginUser);
        return ResultUtils.success(true);
    }

    @ApiOperation("账号删除")
    @PostMapping("/del")
    public BaseResponse<Boolean> delThirdPartyAccount(@RequestBody ThirdPartyAccountDelReq delReq, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(delReq ==null, ErrorCode.PARAMS_ERROR);
        String values = ChromePlatFormEnum.getValuesByName(delReq.getThirdPartyFormName()).getValues();
        edgeDriverStrategy.getChromeDriverKey(values).delPlatFormAccount(delReq,loginUser);
        return ResultUtils.success(true);
    }

}
