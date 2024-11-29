package cn.ls.hotnews.service;

import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountDelReq;
import cn.ls.hotnews.model.entity.User;

/**
 * title: ChromeDriverService
 * author: liaoshuo
 * date: 2024/11/19 10:42
 * description:
 */
public interface ChromeDriverService {
    /**
     * 平台登录
     */
    void ChromeDriverPlatFormLogin(User loginUser);

    void ChromeDriver();

    /**
     * 删除账号
     *
     * @param delReq del req
     */
    void delPlatFormAccount(ThirdPartyAccountDelReq delReq,User loginUser);
}
