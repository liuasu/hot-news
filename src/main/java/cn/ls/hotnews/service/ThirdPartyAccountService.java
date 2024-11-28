package cn.ls.hotnews.service;

import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.AccountCentreVO;
import cn.ls.hotnews.model.vo.ThirdPartyAccountVO;

import java.util.List;

/**
 * title: ThirdPartyAccountService
 * author: liaoshuo
 * date: 2024/11/23 20:46
 * description:
 */
public interface ThirdPartyAccountService {
    /**
     * 获取第三方账户列表
     *
     * @param loginUser 登录用户
     * @return {@link List }<{@link ThirdPartyAccountVO }>
     */
    List<ThirdPartyAccountVO> getThirdPartyAccountList(User loginUser);

    /**
     * 按账户中心获取第三方账户列表
     *
     * @param loginUser 登录用户
     * @return
     */
    List<AccountCentreVO> getThirdPartyAccountListByAccountCentre(User loginUser);
}
