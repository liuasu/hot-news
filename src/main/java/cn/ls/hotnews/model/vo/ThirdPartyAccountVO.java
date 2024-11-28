package cn.ls.hotnews.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * title: ThirdPartyAccountVO
 * author: liaoshuo
 * date: 2024/11/23 20:39
 * description:
 */
@Data
public class ThirdPartyAccountVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 帐户
     */
    private String account;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 平台
     */
    private String platForm;

    /**
     * 是否失效
     */
    private Boolean isDisabled;

}
