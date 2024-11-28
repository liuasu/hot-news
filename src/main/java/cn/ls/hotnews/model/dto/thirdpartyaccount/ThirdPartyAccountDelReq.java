package cn.ls.hotnews.model.dto.thirdpartyaccount;

import lombok.Data;

import java.io.Serializable;

/**
 * title: ThirdPartyAccountAddReq
 * author: liaoshuo
 * date: 2024/11/28 11:49
 * description:
 */
@Data
public class ThirdPartyAccountDelReq implements Serializable {


    private static final long serialVersionUID = 1L;
    /**
     * 第三方平台名称
     */
    private String thirdPartyFormName;

    private Integer index;

    private String account;

}
