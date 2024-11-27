package cn.ls.hotnews.model.dto.task;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * TaskAddReq添加请求实体
 *
 * @author ls
 * @createDate 2024-11-23 17:27:51
 */
@ApiModel(value = "TaskAddReq", description = "TaskAddReq添加请求实体")
@Data
public class TaskAddReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    @ApiModelProperty("任务名称")
    private String taskName;
    /**
     * 第三方账号
     */
    @ApiModelProperty("第三方账号")
    private String platFormAccount;
    /**
     * 热点标题
     */
    @ApiModelProperty("热点标题")
    private String hotNewTitle;
    /**
     * 热点url
     */
    @ApiModelProperty("url")
    private String hotUrl;
    /**
     * 热点平台
     */
    @ApiModelProperty("热点平台")
    private String hotPlatForm;
}
