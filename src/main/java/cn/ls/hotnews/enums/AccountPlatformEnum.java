package cn.ls.hotnews.enums;

import lombok.Getter;

/**
 * title: AccountPlatformEnum
 * author: liaoshuo
 * date: 2024/11/19 10:21
 * description:
 */
@Getter
public enum AccountPlatformEnum {
    /**
     * 今日头条
     */
    TOUTIAO("toutiao","https://mp.toutiao.com/profile_v4/graphic/publish"),
    /**
     * 百佳
     */
    BAIJIA("baijia","https://baijiahao.baidu.com/builder/rc/edit?type=news&is_from_cms=1");


    private final String platform;
    private final String platformURL;


    AccountPlatformEnum(String platform, String platformURL) {
        this.platform = platform;
        this.platformURL = platformURL;
    }
}
