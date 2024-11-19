package cn.ls.hotnews.model.dto.hotapi;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * HotApiQueryReq搜索请求实体
 *
 * @author ls
 * @createDate 2024-11-18 21:08:27
 */
@ApiModel(value = "HotApiQueryReq", description = "HotApiQueryReq搜索请求实体")
@Data
public class HotApiQueryReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 平台
     */
    @ApiModelProperty("平台")
    private String platform;

    /**
     * 接口名称
     */
    @ApiModelProperty("接口名称")
    private String apiName;
}
