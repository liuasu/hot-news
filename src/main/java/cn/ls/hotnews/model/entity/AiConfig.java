package cn.ls.hotnews.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ai 秘钥配置
 * @TableName ai_config
 */
@TableName(value ="ai_config")
@Data
public class AiConfig implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * appid
     */
    private String appId;
    /**
     * apiKey
     */
    private String apiKey;
    /**
     * apiSecret
     */
    private String apiSecret;
    /**
     * ai平台
     */
    private Integer aiPlatForm;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}