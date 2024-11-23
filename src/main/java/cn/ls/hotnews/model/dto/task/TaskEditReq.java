package cn.ls.hotnews.model.dto.task;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * TaskEditReq编辑请求实体
 *
 * @author ls
 * @createDate 2024-11-23 17:27:51
 */
@ApiModel(value = "TaskEditReq", description = "TaskEditReq编辑请求实体")
@Data
public class TaskEditReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;
    /**
     * 任务状态
     */
    @ApiModelProperty("任务状态")
    private Integer taskStatus;
}
