package cn.ls.hotnews.enums;

import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.ThrowUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * title: ChromePlatFormEnum
 * author: liaoshuo
 * date: 2024/11/19 10:20
 * description:
 */
@Getter
public enum ChromePlatFormEnum {
    /**
     * 今日头条
     */
    TOUTIAO("toutiao", "toutiaoChrome"),
    ;

    private final String platFormName;
    private final String values;


    ChromePlatFormEnum(String platFormName, String values) {
        this.platFormName = platFormName;
        this.values = values;
    }

    public static ChromePlatFormEnum getValuesByName(String platFormName) {
        ThrowUtils.throwIf(StringUtils.isBlank(platFormName),ErrorCode.PARAMS_ERROR);
        for (ChromePlatFormEnum value : ChromePlatFormEnum.values()) {
            if(value.getPlatFormName().equals(platFormName)){
                return value;
            }
        }
        return null;
    }
}
