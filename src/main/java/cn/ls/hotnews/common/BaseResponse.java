package cn.ls.hotnews.common;

import cn.hutool.core.date.DateTime;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    private DateTime currentDateTime;

    private DateTime updateDateTime;

    public BaseResponse(int code, T data, String message, DateTime currentDateTime, DateTime updateDateTime) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.currentDateTime = currentDateTime;
        this.updateDateTime = updateDateTime;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "",new DateTime(),new DateTime());
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(),new DateTime(),new DateTime());
    }
}
