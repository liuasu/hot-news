package cn.ls.hotnews.service.impl;

import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.enums.AccountPlatformEnum;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.service.EdgeDriverService;
import cn.ls.hotnews.utils.EdgeDriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.edge.EdgeDriver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * title: TouTiaoEdgeDriverServiceImpl
 * author: liaoshuo
 * date: 2024/11/19 10:50
 * description: 头条操作
 */
@Slf4j
@Service
public class TouTiaoEdgeDriverServiceImpl implements EdgeDriverService {


    @Resource
    private EdgeDriverUtils edgeDriverUtils;

    /**
     * 平台登录
     */
    @Override
    public void EdgeDriverPlatFormLogin() {
        AccountPlatformEnum toutiao = AccountPlatformEnum.TOUTIAO;
        EdgeDriver driver = edgeDriverUtils.initEdgeDriver(toutiao.getPlatform());
        try {
            driver.get(toutiao.getPlatformURL());
            //Thread.sleep(10000);
        } catch (Exception e) {
            log.error("EdgeDriver 错误信息: ",e);
            throw new RuntimeException(e);
        } finally {
            //driver.close();
        }
    }

    /**
     * 边缘驱动程序
     */
    @Override
    public void EdgeDriver() {


    }
}
