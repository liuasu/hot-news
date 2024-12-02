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

    // 头条 start
    String TOUTIAO="toutiao";
    String TOUTIAO_EDGE_DRIVER_LOGIN = "TouTiaoChrome";
    String REDIS_TOUTIAO = "hot-news:toutiaoHotNews:toutiao";
    String REDIS_TOUTIAO_DTATETIME = "hot-news:datetime:toutiao";
    // end

    //哔哩哔哩 start
    String BILIBILI="bilibili";
    String REDIS_BILIBILI = "hot-news:bilibiliHotNews:bilibili";
    String REDIS_BILIBILI_DTATETIME = "hot-news:datetime:bilibili";
    //end

    //抖音 start
    String DOUYIN="douyin";
    String REDIS_DY = "hot-news:dyHotNews:dy";
    String REDIS_DY_DTATETIME = "hot-news:datetime:dy";
    //end

    // 澎湃新闻 start
    String THEPAPER="thepaper";
    String REDIS_THEPAPER = "hot-news:toutiaoHotNews:thepaper";
    String REDIS_THEPAPER_DTATETIME = "hot-news:datetime:thepaper";
    //end




    Integer ZERO = 0;
    Integer ONE = 1;

    String REDIS_THIRDPARTY_ACCOUNT="hot-news:thirdparty:account:%s";
    String REDIS_THIRDPARTY_ACCOUNT_COOKIE="hot-news:thirdparty:%s:account:cookie:%s";
    String REDIS_ACCOUNT_PROFILENAME ="hot-news:account:profilename:%s";





}
