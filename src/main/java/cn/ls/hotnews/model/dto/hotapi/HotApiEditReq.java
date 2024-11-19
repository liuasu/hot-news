package cn.ls.hotnews.model.dto.hotapi;


import java.util.Date;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * HotApiEditReq编辑请求实体
 *
 * @author ls
 * @createDate 2024-11-18 21:08:27
 */
@ApiModel(value = "HotApiEditReq", description = "HotApiEditReq编辑请求实体")
@Data
public class HotApiEditReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;
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
    /**
     * 接口地址
     */
    @ApiModelProperty("接口地址")
    private String apiURL;
    /**
     * 接口描述
     */
    @ApiModelProperty("接口描述")
    private String apiDescribe;
}
