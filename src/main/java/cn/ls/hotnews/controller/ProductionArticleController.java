package cn.ls.hotnews.controller;

import cn.ls.hotnews.ai.XingHuoAIServiceImpl;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.hotnews.HotNewsAddReq;
import cn.ls.hotnews.model.dto.productionarticle.ProductionArticleAddReq;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.service.UserService;
import cn.ls.hotnews.strategy.HotNewsStrategy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static cn.ls.hotnews.constant.CommonConstant.TOUTIAO;

/**
 * title: ProductionArticleController
 * author: liaoshuo
 * date: 2024/12/5 15:25
 * description: ai 文章生成
 */

@Api(tags = "ai 文章生成")
@RestController
@RequestMapping("/production")
public class ProductionArticleController {

    @Resource
    private UserService userService;
    @Resource
    private HotNewsStrategy hotNewsStrategy;

    @Resource
    private XingHuoAIServiceImpl xingHuoAIService;

    @ApiOperation("文章生成")
    @PostMapping("/article")
    public void productionArticle(@RequestBody ProductionArticleAddReq articleAddReq, HttpServletRequest request) {
        String title = articleAddReq.getTitle();
        String hotURL = articleAddReq.getHotURL();
        String promptName = articleAddReq.getPromptName();
        String aiPlatForm = articleAddReq.getAiPlatForm();
        String userIdStr = articleAddReq.getUserIdStr();
        String thirdPartyFormName = articleAddReq.getThirdPartyFormName();


        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(title == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(hotURL == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(aiPlatForm == null, ErrorCode.PARAMS_ERROR);
        HotNewsAddReq hotNewsAddReq = new HotNewsAddReq();

        hotNewsAddReq.setTitle(title);
        hotNewsAddReq.setHotURL(hotURL);
        Map<String, Object> hotUrlGainNewMap = hotNewsStrategy.getHotNewsByPlatform(TOUTIAO).getHotUrlGainNew(hotNewsAddReq);
        hotUrlGainNewMap.put("aiPlatForm", aiPlatForm);
        hotUrlGainNewMap.put("promptName", promptName);
        hotUrlGainNewMap.put("userIdStr", userIdStr);
        hotUrlGainNewMap.put("thirdPartyFormName", thirdPartyFormName);
        xingHuoAIService.productionArticle(hotUrlGainNewMap, loginUser);
    }
}
