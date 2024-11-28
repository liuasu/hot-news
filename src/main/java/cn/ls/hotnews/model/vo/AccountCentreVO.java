package cn.ls.hotnews.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * title: AccountCentreVO
 * author: liaoshuo
 * date: 2024/11/28 15:38
 * description:
 */
@Data
public class AccountCentreVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Integer count;

    private List<ThirdPartyAccountVO> thirdPartyAccountVOList;
}
