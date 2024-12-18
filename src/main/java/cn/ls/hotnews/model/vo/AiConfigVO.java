package cn.ls.hotnews.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ai 秘钥配置
 * @TableName ai_config
 */

@Data
public class AiConfigVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
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
    private String aiPlatForm;

    private String aiPlatFormName;
}