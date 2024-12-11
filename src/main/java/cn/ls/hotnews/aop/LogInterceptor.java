package cn.ls.hotnews.aop;

import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.model.entity.OperLog;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.service.OperLogService;
import cn.ls.hotnews.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 请求响应日志 AOP
 **/
@Aspect
@Component
@Slf4j
public class LogInterceptor {

    @Resource
    private OperLogService operLogService;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     */
    @Around("execution(* cn.ls.hotnews.controller.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 获取请求路径
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(httpServletRequest);
        // 生成请求唯一 id
        String requestId = UUID.randomUUID().toString();
        String url = httpServletRequest.getRequestURI();
        // 获取请求参数
        Object[] args = point.getArgs();
        String methodName = point.getSignature().getName();
        // 获取请求方式
        String requestMethod = httpServletRequest.getMethod();
        // 获取类名
        String className = point.getTarget().getClass().getName();
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
        // 输出请求日志
        String ip = httpServletRequest.getRemoteHost();
        log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url,
                ip, reqParam);
        // 执行原方法
        Object result = point.proceed();
        // 输出响应日志
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
        recordLog(url, methodName, className, requestMethod, ip, reqParam, loginUser.getUserName(), 0L, null, totalTimeMillis, JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 拦截异常操作
     *
     * @param point 切点
     * @param e         异常
     */
    @AfterThrowing(value = "execution(* cn.ls.hotnews.controller.*.*(..))", throwing = "e")
    public void doAfterThrowing(JoinPoint point, Exception e) {
        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(httpServletRequest);
        // 生成请求唯一 id
        String requestId = UUID.randomUUID().toString();
        String url = httpServletRequest.getRequestURI();
        // 获取请求参数
        Object[] args = point.getArgs();
        String methodName = point.getSignature().getName();
        // 获取请求方式
        String requestMethod = httpServletRequest.getMethod();
        // 获取类名
        String className = point.getTarget().getClass().getName();
        //请求参数
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
        String ip = httpServletRequest.getRemoteHost();
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        recordLog(url, methodName, className, requestMethod, ip, reqParam, loginUser.getUserName(), 1L, e.getMessage(), totalTimeMillis,null);
    }

    public void recordLog(String url,
                           String methodName,
                           String className,
                           String requestMethod,
                           String ip,
                           String param,
                           String userName,
                           Long status,
                           String errorMsg,
                           long totalTimeMillis,
                           String result
    ) {
        CompletableFuture.runAsync(() -> {
            OperLog operLog = new OperLog();
            operLog.setClassName(className);
            operLog.setMethod(methodName);
            operLog.setRequestMethod(requestMethod);
            operLog.setOperUser(userName);
            operLog.setOperUrl(url);
            operLog.setOperIp(ip);
            operLog.setOperParam(param);
            operLog.setJsonResult( result);
            operLog.setStatus(status);
            if (errorMsg != null) {
                operLog.setErrorMsg(errorMsg);
            }
            operLog.setOperTime(new Date());
            operLog.setCostTime(totalTimeMillis);
            operLogService.addOperLog(operLog);
        }, threadPoolExecutor);
    }
}

