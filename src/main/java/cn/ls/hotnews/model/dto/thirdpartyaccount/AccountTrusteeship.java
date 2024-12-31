package cn.ls.hotnews.model.dto.thirdpartyaccount;

import cn.ls.hotnews.model.vo.ThirdPartyAccountVO;
import lombok.Data;

/**
 * title: AccountTrusteeship
 * author: liaoshuo
 * date: 2024/12/25 10:53
 * description: 托管账号
 */
@Data
public class AccountTrusteeship extends ThirdPartyAccountVO {
    /**
     *  热点类型
     */
    private String hotType;

    /**
     * 发布最大数量
     */
    private Integer publishMaxNumber;
}
