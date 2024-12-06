package cn.ls.hotnews.service;

import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountDelReq;
import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountQueryReq;
import cn.ls.hotnews.model.entity.Article;
import cn.ls.hotnews.model.entity.User;

import java.util.List;
import java.util.Map;

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

    /**
     * 发布文章
     *
     * @param userIdStr 用户 ID str
     * @param article   品
     * @param imgMap       根据相关文章获取的的图片
     */
    void chromePublishArticle(String userIdStr, Article article, Map<String, List<String>> imgMap);

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
