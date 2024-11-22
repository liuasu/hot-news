package cn.ls.hotnews.common;

import cn.hutool.core.date.DateTime;

/**
 * 返回工具类
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok", new DateTime(), new DateTime());
    }

    /**
     * 成功
     *
     * @param data
     * @param updateDateTime 更新日期时间
     * @return
     */
    public static <T> BaseResponse<T> success(T data,  DateTime updateDateTime) {
        return new BaseResponse<>(0, data, "ok", new DateTime(), updateDateTime);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message, new DateTime(), new DateTime());
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message, new DateTime(), new DateTime());
    }
}
