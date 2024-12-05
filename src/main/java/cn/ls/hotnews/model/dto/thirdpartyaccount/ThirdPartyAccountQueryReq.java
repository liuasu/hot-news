package cn.ls.hotnews.model.dto.thirdpartyaccount;

import lombok.Data;

/**
 * title: ThirdPartyAccountAddReq
 * author: liaoshuo
 * date: 2024/11/28 11:49
 * description:
 */
@Data
public class ThirdPartyAccountQueryReq {

    /**
     * 用户 ID str
     */
    private String userIdStr;

    /**
     * 第三方平台名称
     */
    private String thirdPartyFormName;

}
