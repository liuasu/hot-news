package cn.ls.hotnews.service;

import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountDelReq;
import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountQueryReq;
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
    void delPlatFormAccount(ThirdPartyAccountDelReq delReq, User loginUser);


    /**
     * 按用户 ID 查询
     *
     * @param queryReq  查询 req
     * @param loginUser 登录用户
     */
    void queryByUserIdStr(ThirdPartyAccountQueryReq queryReq, User loginUser);
}
