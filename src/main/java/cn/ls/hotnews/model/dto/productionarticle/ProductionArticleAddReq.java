package cn.ls.hotnews.model.dto.productionarticle;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * title: ProductionArticleAddReq
 * author: liaoshuo
 * date: 2024/11/19 20:21
 * description:
 */
@ApiModel(value = "ProductionArticleAddReq", description = "文章生成")
@Data
public class ProductionArticleAddReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 标题
     */
    private String title;
    /**
     * 热门网址
     */
    private String hotURL;
    /**
     * 提示名称
     */
    private String promptName;
    /**
     * ai平台
     */
    private String aiPlatForm;

    /**
     * 用户 ID str
     */
    private String userIdStr;

    /**
     * 第三方平台名称
     */
    private String thirdPartyFormName;
}
