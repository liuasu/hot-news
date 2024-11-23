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
@ApiModel(value = "TaskQueryReq", description = "TaskQueryReq搜索请求实体")
@Data
public class TaskQueryReq implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 第三方账号
     */
    @ApiModelProperty("第三方账号平台")
    private String platFormAccount;
}
