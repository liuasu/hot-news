package cn.ls.hotnews.model.dto.aiconfig;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * AiConfigEditReq编辑请求实体
 *
 * @author ls
 * @createDate 2024-12-05 11:16:10
 */
@ApiModel(value = "AiConfigEditReq", description = "AiConfigEditReq编辑请求实体")
@Data
public class AiConfigEditReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;
    /**
     * appid
     */
    @ApiModelProperty("appid")
    private String appId;
    /**
     * apiKey
     */
    @ApiModelProperty("apiKey")
    private String apiKey;
    /**
     * apiSecret
     */
    @ApiModelProperty("apiSecret")
    private String apiSecret;
    /**
     * ai平台
     */
    @ApiModelProperty("ai平台")
    private String aiPlatForm;
}
