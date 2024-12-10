package cn.ls.hotnews.enums;

import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.ThrowUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * title: HotPlatformEnum
 * author: liaoshuo
 * date: 2024/11/19 10:20
 * description: 热点平台枚举(哔哩哔哩，百度，知乎，百度贴吧，少数派，IT之家，澎湃新闻，今日头条，微博热搜，36氪，稀土掘金，腾讯新闻)
 */
@Getter
public enum HotPlatformEnum {
    /**
     * 哔哩哔哩
     */
    BILIBILI("哔哩哔哩", "bilibili"),
    /**
     * 抖音
     */
    DOUYIN("抖音", "douyin"),

    /**
     * 澎湃新闻
     */
    THEPAPER("澎湃新闻", "thepaper"),
    /**
     * 今日头条
     */
    TOUTIAO("今日头条", "toutiao"),
    /**
     * 36氪
     */
    THIRTYIX("36氪", "thirtysix"),
    /**
     * 稀土掘金
     */
    WANGYI("网易新闻", "wangyi"),
    /**
     * 腾讯新闻
     */
    QQ_NEWS("腾讯新闻", "qq_news"),
    ;

    private final String platFormName;
    private final String values;


    HotPlatformEnum(String platFormName, String values) {
        this.platFormName = platFormName;
        this.values = values;
    }

    public static HotPlatformEnum getValuesByName(String platFormName) {
        ThrowUtils.throwIf(StringUtils.isBlank(platFormName),ErrorCode.PARAMS_ERROR);
        for (HotPlatformEnum value : HotPlatformEnum.values()) {
            if(value.getPlatFormName().equals(platFormName)){
                return value;
            }
        }
        return null;
    }
}
