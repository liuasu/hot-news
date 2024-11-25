package cn.ls.hotnews.service.impl;

import cn.ls.hotnews.enums.AccountPlatformEnum;
import cn.ls.hotnews.service.EdgeDriverService;
import cn.ls.hotnews.utils.EdgeDriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.edge.EdgeDriver;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * title: TouTiaoEdgeDriverServiceImpl
 * author: liaoshuo
 * date: 2024/11/19 10:50
 * description: 头条操作
 */
@Slf4j
@Service
@Component("TouTiaoEdge")
public class TouTiaoEdgeDriverServiceImpl implements EdgeDriverService {


    @Resource
    private EdgeDriverUtils edgeDriverUtils;

    /**
     * 未登录 Cookie 列表
     */
    private final List<String> notLoginCookieList = Arrays.asList("x-jupiter-uuid");
    /**
     * 平台登录
     */
    @Override
    public void EdgeDriverPlatFormLogin() {
        AccountPlatformEnum toutiao = AccountPlatformEnum.TOUTIAO;
        EdgeDriver driver = edgeDriverUtils.initEdgeDriver();
        try {
            driver.get(toutiao.getPlatformURL());
            Set<Cookie> cookieSet = driver.manage().getCookies();
            if(cookieSet.size()<=26){
                //List<String> cookieNames= new ArrayList<>();
                //for (Cookie cookie : cookieSet) {
                //    cookieNames.add(cookie.getName());
                //}
                //boolean x = new HashSet<>(cookieNames).containsAll(notLoginCookieList);
                ////没有登录先睡15秒自动关闭，登录则用户手动关闭
                //if(x){
                //    Thread.sleep(10000);
                //    driver.quit();
                //}
                Thread.sleep(10000);
                driver.quit();
            }
        } catch (Exception e) {
            log.error("EdgeDriver 错误信息: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 操作发布文章
     */
    @Override
    public void EdgeDriver() {


    }
}
