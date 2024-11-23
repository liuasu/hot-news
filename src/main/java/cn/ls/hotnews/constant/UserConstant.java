package cn.ls.hotnews.constant;

/**
 * 用户常量
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

    String Default_User_Avatar = "https://www.code-nav.cn/logo.png";

    /**
     * 第三方账号存放redis中
     * 解释：hot-news:third:party:account:%s(平台):%s(用户id)
     */
    String REDIS_THIRD_PARTY_ACCOUNT = "hot-news:third:party:account:%s:%s";

    // endregion
}
