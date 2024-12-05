package cn.ls.hotnews.enums;

import cn.hutool.core.util.ObjectUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.ThrowUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * title: AIPlatFormEnum
 * author: liaoshuo
 * date: 2024/11/19 10:20
 * description:
 */
@Getter
public enum AIPlatFormEnum {
    /**
     * 今日头条
     */
    XINGHUO("xinghuo", "讯飞星火", 1),
    ZHIPU("zhipu", "智普", 2),
    JIEYUEXINGCHEN("jieyuexingchen", "阶跃星辰", 3),
    ;

    private final String platFormName;
    private final String name;
    private final Integer values;


    AIPlatFormEnum(String platFormName, String name, Integer values) {
        this.platFormName = platFormName;
        this.name = name;
        this.values = values;
    }

    public static AIPlatFormEnum getValuesByName(String platFormName) {
        ThrowUtils.throwIf(StringUtils.isBlank(platFormName), ErrorCode.PARAMS_ERROR);
        for (AIPlatFormEnum value : AIPlatFormEnum.values()) {
            if (value.getPlatFormName().equals(platFormName)) {
                return value;
            }
        }
        return null;
    }

    public static AIPlatFormEnum getNameByValues(Integer values) {
        ThrowUtils.throwIf(ObjectUtil.isEmpty(values), ErrorCode.PARAMS_ERROR);
        for (AIPlatFormEnum value : AIPlatFormEnum.values()) {
            if (Objects.equals(value.getValues(), values)) {
                return value;
            }
        }
        return null;
    }
}
