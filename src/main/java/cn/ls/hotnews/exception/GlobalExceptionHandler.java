package cn.ls.hotnews.exception;

import cn.ls.hotnews.common.BaseResponse;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.common.ResultUtils;
import cn.ls.hotnews.manager.ChromeProcessCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Resource
    private ChromeProcessCleaner chromeProcessCleaner;

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        chromeProcessCleaner.cleanupNow();
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
