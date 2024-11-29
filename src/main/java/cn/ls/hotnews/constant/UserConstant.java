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


    String[] TOUTIAO_COOKIE_SORT_LIST = {"s_v_web_id", "ttwid", "passport_csrf_token", "passport_csrf_token_default", "n_mh", "sso_uid_tt", "sso_uid_tt_ss", "toutiao_sso_user", "toutiao_sso_user_ss", "sid_ucp_sso_v1", "ssid_ucp_sso_v1", "passport_auth_status", "uid_tt", "uid_tt_ss", "sid_tt", "sessionid", "sessionid_ss", "is_staff_user", "sid_ucp_v1", "ssid_ucp_v1", "store-region", "store-region-src", "gfkadpd", "ttcid", "odin_tt", "tt_scid", "csrf_session_id",};

    // endregion
}
