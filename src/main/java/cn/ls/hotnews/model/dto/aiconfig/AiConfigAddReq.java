package cn.ls.hotnews.model.dto.aiconfig;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * AiConfigAddReq添加请求实体
 *
 * @author ls
 * @createDate 2024-12-05 11:16:10
 */
@ApiModel(value = "AiConfigAddReq", description = "AiConfigAddReq添加请求实体")
@Data
public class AiConfigAddReq implements Serializable {

    private static final long serialVersionUID = 1L;

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
