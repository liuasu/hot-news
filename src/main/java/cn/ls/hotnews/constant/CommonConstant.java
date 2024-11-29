package cn.ls.hotnews.constant;

/**
 * 通用常量
 */
public interface CommonConstant {

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = "descend";

    String TOUTIAO_EDGE_DRIVER_LOGIN = "TouTiaoChrome";

    String REDIS_BILIBILI = "hot-news:bilibiliHotNews:bilibili";
    String REDIS_DY = "hot-news:dyHotNews:dy";
    String REDIS_TOUTIAO = "hot-news:toutiaoHotNews:toutiao";
    String REDIS_BILIBILI_DTATETIME = "hot-news:datetime:bilibili";
    String REDIS_DY_DTATETIME = "hot-news:datetime:dy";
    String REDIS_TOUTIAO_DTATETIME = "hot-news:datetime:toutiao";

    Integer ZERO = 0;
    Integer ONE = 1;

    String REDIS_THIRDPARTY_ACCOUNT="hot-news:thirdparty:account:%s";
    String REDIS_THIRDPARTY_ACCOUNT_COOKIE="hot-news:thirdparty:%s:account:cookie:%s";


}
