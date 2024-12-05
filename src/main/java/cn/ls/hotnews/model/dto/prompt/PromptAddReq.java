package cn.ls.hotnews.model.dto.prompt;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * PromptAddReq添加请求实体
 *
 * @author ls
 * @createDate 2024-12-05 11:16:10
 */
@ApiModel(value = "PromptAddReq", description = "PromptAddReq添加请求实体")
@Data
@AllArgsConstructor
public class PromptAddReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板名称
     */
    @ApiModelProperty("模板名称")
    private String promptName;
    /**
     * 提示词模板
     */
    @ApiModelProperty("提示词模板")
    private String promptTemplate;
}
