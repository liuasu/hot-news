package cn.ls.hotnews.model.dto.task;


import cn.ls.hotnews.common.PageRequest;
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
@ApiModel(value = "TaskQueryReq", description = "TaskQueryReq搜索请求实体")
@Data
public class TaskQueryReq extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 第三方账号平台
     */
    @ApiModelProperty("第三方账号平台")
    private String platForm;


    /**
     * 任务状态 0-已配置 1-已生产
     */
    @ApiModelProperty("任务状态")
    private Integer taskStatus;
}
