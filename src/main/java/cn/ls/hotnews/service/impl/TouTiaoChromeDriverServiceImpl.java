package cn.ls.hotnews.service.impl;

import cn.ls.hotnews.enums.AccountPlatformEnum;
import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountDelReq;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.ThirdPartyAccountVO;
import cn.ls.hotnews.service.ChromeDriverService;
import cn.ls.hotnews.utils.ChromeDriverUtils;
import cn.ls.hotnews.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.ls.hotnews.constant.CommonConstant.REDIS_THIRDPARTY_ACCOUNT;

/**
 * title: TouTiaoChromeDriverServiceImpl
 * author: liaoshuo
 * date: 2024/11/19 10:50
 * description: 头条操作
 */
@Slf4j
@Service
@Component("TouTiaoChrome")
public class TouTiaoChromeDriverServiceImpl implements ChromeDriverService {


    /**
     * 未登录 Cookie 列表
     */
    private final List<String> notLoginCookieList = Arrays.asList("x-jupiter-uuid");
    @Resource
    private RedisUtils redisUtils;

    /**
     * 平台登录
     */
    @Override
    public void ChromeDriverPlatFormLogin(User loginUser) {
        Long userId = loginUser.getId();
        AccountPlatformEnum toutiao = AccountPlatformEnum.TOUTIAO;
        ChromeDriver driver = ChromeDriverUtils.initChromeDriver();
        List<ThirdPartyAccountVO> list = null;
        try {
            driver.get(toutiao.getPlatformURL());
            Set<Cookie> cookieSet = driver.manage().getCookies();
            if (cookieSet.size() <= 26) {
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
            log.error("ChromeDriver 错误信息: ", e);
            throw new RuntimeException(e);
        }
        //todo 账号登录后将账户信息存放到redis中
        //String key = String.format(REDIS_THIRDPARTY_ACCOUNT, userId);
        //Map<String, List<ThirdPartyAccountVO>> map = redisUtils.redisGetThirdPartyAccountByMap(key);
        //list = map.get(AccountPlatformEnum.TOUTIAO.getPlatform());
        //if (CollectionUtil.isEmpty(list)) {
        //    //list.add()
        //} else {
        //    //list.add();
        //    //这里需要判断账号是否在集合中，存在将原来的删除，用新的替换
        //    //for (ThirdPartyAccountVO thirdPartyAccountVO : list) {
        //    //
        //    //}
        //}
        //map.put(AccountPlatformEnum.TOUTIAO.getPlatform(), list);
        //redisUtils.redisSetInMap(key, map);
    }

    /**
     * 操作发布文章
     */
    @Override
    public void ChromeDriver() {


    }


    /**
     * 删除账号
     *
     * @param delReq del req
     */
    @Override
    public void delPlatFormAccount(ThirdPartyAccountDelReq delReq, User loginUser) {
        String thirdPartyFormName = delReq.getThirdPartyFormName();
        Integer index = delReq.getIndex();
        String account = delReq.getAccount();
        String key = String.format(REDIS_THIRDPARTY_ACCOUNT, loginUser.getId());
        Map<String, List<ThirdPartyAccountVO>> map = redisUtils.redisGetThirdPartyAccountByMap(key);
        List<ThirdPartyAccountVO> list = map.get(thirdPartyFormName);
        ThirdPartyAccountVO thirdPartyAccountVO = list.get(index);
        if (thirdPartyAccountVO.getAccount().equals(account)) {
            list.remove(thirdPartyAccountVO);
        }
        map.put(thirdPartyFormName,list);
        redisUtils.redisSetInMap(key,map);
    }
}
