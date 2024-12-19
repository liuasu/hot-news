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
    String TOUTIAO = "toutiao";
    String TOUTIAO_EDGE_DRIVER_LOGIN = "TouTiaoChrome";
    String REDIS_TOUTIAO = "hot-news:hotNews:toutiao";
    String REDIS_TOUTIAO_DTATETIME = "hot-news:datetime:toutiao";
    // end

    // 百家 start
    String BAIJIA = "baijia";
    // end

    //哔哩哔哩 start
    String BILIBILI = "bilibili";
    String REDIS_BILIBILI = "hot-news:hotNews:bilibili";
    String REDIS_BILIBILI_DTATETIME = "hot-news:datetime:bilibili";
    //end

    //抖音 start
    String DOUYIN = "douyin";
    String REDIS_DY = "hot-news:hotNews:dy";
    String REDIS_DY_DTATETIME = "hot-news:datetime:dy";
    //end

    // 澎湃新闻 start
    String THEPAPER = "thepaper";
    String REDIS_THEPAPER = "hot-news:hotNews:thepaper";
    String REDIS_THEPAPER_DTATETIME = "hot-news:datetime:thepaper";
    //end

    // 36氪 start
    String ThirtySixKR = "thirtysix";
    String REDIS_ThirtySixKR = "hot-news:hotNews:thirtysix";
    String REDIS_ThirtySixKR_DTATETIME = "hot-news:datetime:thirtysix";
    //end

    // 网易新闻 start
    String WANGYI = "wangyi";
    String REDIS_WANGYI = "hot-news:hotNews:wangyi";
    String REDIS_WANGYI_DTATETIME = "hot-news:datetime:wangyi";
    //end


    // 腾讯新闻 start
    String QQNEWS = "qq_news";
    String REDIS_QQNEWS = "hot-news:hotNews:qq_news";
    String REDIS_QQNEWS_DTATETIME = "hot-news:datetime:qq_news";
    //end


    // start
    //end


    Integer ZERO = 0;
    Integer ONE = 1;

    String REDIS_THIRDPARTY_ACCOUNT = "hot-news:thirdparty:account:%s";
    String REDIS_THIRDPARTY_ACCOUNT_COOKIE = "hot-news:thirdparty:%s:account:cookie:%s";
    String REDIS_ACCOUNT_PROFILENAME = "hot-news:account:profilename:%s";


}
