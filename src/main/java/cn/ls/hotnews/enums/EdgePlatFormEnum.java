package cn.ls.hotnews.enums;

import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.ThrowUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * title: EdgePlatFormEnum
 * author: liaoshuo
 * date: 2024/11/19 10:20
 * description:
 */
@Getter
public enum EdgePlatFormEnum {
    /**
     * 今日头条
     */
    TOUTIAO("toutiao", "TouTiaoEdge"),
    ;

    private final String platFormName;
    private final String values;


    EdgePlatFormEnum(String platFormName, String values) {
        this.platFormName = platFormName;
        this.values = values;
    }

    public static EdgePlatFormEnum getValuesByName(String platFormName) {
        ThrowUtils.throwIf(StringUtils.isBlank(platFormName),ErrorCode.PARAMS_ERROR);
        for (EdgePlatFormEnum value : EdgePlatFormEnum.values()) {
            if(value.getPlatFormName().equals(platFormName)){
                return value;
            }
        }
        return null;
    }
}
