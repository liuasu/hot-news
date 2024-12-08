package cn.ls.hotnews.controller;

import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.common.ResultUtils;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.hotnews.HotNewsAddReq;
import cn.ls.hotnews.model.dto.productionarticle.ProductionArticleAddReq;
import cn.ls.hotnews.model.entity.Task;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.service.TaskService;
import cn.ls.hotnews.service.UserService;
import cn.ls.hotnews.strategy.AIStrategy;
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
    private AIStrategy aiStrategy;
    @Resource
    private TaskService taskService;

    @ApiOperation("文章生成")
    @PostMapping("/article")
    public BaseResponse<Boolean> productionArticle(@RequestBody ProductionArticleAddReq articleAddReq, HttpServletRequest request) {
        String taskId = articleAddReq.getTaskId();
        String title = articleAddReq.getTitle();
        String hotURL = articleAddReq.getHotURL();
        String promptName = articleAddReq.getPromptName();
        String aiPlatForm = articleAddReq.getAiPlatForm();
        String userIdStr = articleAddReq.getUserIdStr();
        String thirdPartyFormName = articleAddReq.getThirdPartyFormName();
        String thirdHotPartyFormName = articleAddReq.getThirdHotPartyFormName();


        ThrowUtils.throwIf(title == null, ErrorCode.PARAMS_ERROR, "热点标题不能为空");
        ThrowUtils.throwIf(hotURL == null, ErrorCode.PARAMS_ERROR, "请选择对应的热点");
        ThrowUtils.throwIf(aiPlatForm == null, ErrorCode.PARAMS_ERROR, "请选择使用的ai模型");
        ThrowUtils.throwIf(userIdStr == null, ErrorCode.PARAMS_ERROR, "请选择发布账号");
        ThrowUtils.throwIf(thirdPartyFormName == null, ErrorCode.PARAMS_ERROR, "请选择发布平台");
        User loginUser = userService.getLoginUser(request);

        HotNewsAddReq hotNewsAddReq = new HotNewsAddReq();
        hotNewsAddReq.setTitle(title);
        hotNewsAddReq.setHotURL(hotURL);
        Map<String, Object> hotUrlGainNewMap = hotNewsStrategy.getHotNewsByPlatform(thirdHotPartyFormName).getHotUrlGainNew(hotNewsAddReq);
        hotUrlGainNewMap.put("aiPlatForm", aiPlatForm);
        hotUrlGainNewMap.put("promptName", promptName);
        hotUrlGainNewMap.put("userIdStr", userIdStr);
        hotUrlGainNewMap.put("thirdPartyFormName", thirdPartyFormName);
        aiStrategy.getAiByKey(aiPlatForm).productionArticle(hotUrlGainNewMap, loginUser);
        Task task = new Task();
        task.setId(Long.valueOf(taskId));
        task.setUserId(loginUser.getId());
        return ResultUtils.success(taskService.editTask(task));
    }
}
