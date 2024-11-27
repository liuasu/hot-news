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
     * 百度
     */
    BAIDU("百度", ""),
    /**
     * 知乎
     */
    ZHIHU("知乎", ""),
    /**
     * 百度贴吧
     */
    BAIDUTIEBA("百度贴吧", ""),
    /**
     * 少数派
     */
    SHAOSHUPAI("少数派", ""),
    /**
     * IT之家
     */
    ITHOME("IT之家", ""),
    /**
     * 澎湃新闻
     */
    PENGPAI("澎湃新闻", ""),
    /**
     * 今日头条
     */
    TOUTIAO("今日头条", "toutiao"),
    /**
     * 微博
     */
    WEIBO("微博热搜", ""),
    /**
     * 36氪
     */
    THIRTYIX("36氪", ""),
    /**
     * 稀土掘金
     */
    JUEJIN("稀土掘金", ""),
    /**
     * 腾讯新闻
     */
    TENGXUN("腾讯新闻", ""),
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
