package cn.ls.hotnews.service;

import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountDelReq;
import cn.ls.hotnews.model.entity.User;

/**
 * title: EdgeDriverService
 * author: liaoshuo
 * date: 2024/11/19 10:42
 * description:
 */
public interface EdgeDriverService {

    /**
     * 平台登录
     */
    void EdgeDriverPlatFormLogin(User loginUser);

    void EdgeDriver();

    /**
     * 删除账号
     *
     * @param delReq del req
     */
    void delPlatFormAccount(ThirdPartyAccountDelReq delReq,User loginUser);
}
