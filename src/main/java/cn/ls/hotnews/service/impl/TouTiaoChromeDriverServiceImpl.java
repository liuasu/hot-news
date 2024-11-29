package cn.ls.hotnews.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.enums.AccountPlatformEnum;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountDelReq;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.ThirdPartyAccountVO;
import cn.ls.hotnews.service.ChromeDriverService;
import cn.ls.hotnews.service.HotApiService;
import cn.ls.hotnews.utils.ChromeDriverUtils;
import cn.ls.hotnews.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static cn.ls.hotnews.constant.CommonConstant.REDIS_THIRDPARTY_ACCOUNT;
import static cn.ls.hotnews.constant.CommonConstant.REDIS_THIRDPARTY_ACCOUNT_COOKIE;
import static cn.ls.hotnews.constant.UserConstant.TOUTIAO_COOKIE_SORT_LIST;

/**
 * title: TouTiaoChromeDriverServiceImpl
 * author: liaoshuo
 * date: 2024/11/19 10:50
 * description: 头条操作
 * <p>
 * 操作发布文章
 * <p>
 * 操作发布文章
 */

/**
 * 操作发布文章
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
    @Resource
    private HotApiService hotApiService;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 平台登录
     */
    @Override
    public void ChromeDriverPlatFormLogin(User loginUser) {
        Long userId = loginUser.getId();
        AccountPlatformEnum toutiao = AccountPlatformEnum.TOUTIAO_LOGIN;
        HotApi userInfo = hotApiService.getPlatformAPI("toutiao_getUserInfo");
        HotApi userLoginStatus = hotApiService.getPlatformAPI("toutiao_user_login_status");
        ThrowUtils.throwIf(userInfo == null && userLoginStatus == null, ErrorCode.NOT_FOUND_ERROR);
        //操作浏览器
        ChromeDriver driver = ChromeDriverUtils.initChromeDriver();
        Set<Cookie> cookieSet;
        try {
            //发送请求
            driver.get(toutiao.getPlatformURL());
            Thread.sleep(5000);
            //获取到cookie
            cookieSet = driver.manage().getCookies();
            //这里是未登录的
            if (cookieSet.size() <= 26) {
                Thread.sleep(5000);
                driver.quit();
            }
        } catch (Exception e) {
            log.error("ChromeDriver error message: ", e);
            throw new RuntimeException(e);
        }
        CompletableFuture.runAsync(() -> {
            List<ThirdPartyAccountVO> list;
            String userInfoBody;
            String userLoginStatusInfoBody;

            Map<String, String> map = cookieSet.stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
            StringBuilder stringBuilder = new StringBuilder();
            for (String key : TOUTIAO_COOKIE_SORT_LIST) {
                stringBuilder.append(String.format("%s=%s; ", key, map.get(key)));
            }
            String cookieStrValues = stringBuilder.toString();
            //通过发送请求获取到用户信息
            try {
                userInfoBody = HttpUtil.createGet(userInfo.getApiURL())
                        .cookie(cookieStrValues)
                        .execute()
                        .body();
                userLoginStatusInfoBody = HttpUtil.createGet(userLoginStatus.getApiURL())
                        .cookie(cookieStrValues).execute().body();
            } catch (HttpException e) {
                log.error("toutiao get user info error message:\t", e);
                throw new RuntimeException(e);
            }
            JSONObject userInfoBodyJson = JSONUtil.parseObj(userInfoBody);

            JSONObject userLoginStatusInfoBodyJson = JSONUtil.parseObj(userLoginStatusInfoBody);
            JSONObject dataJson = JSONUtil.parseObj(userLoginStatusInfoBodyJson.get("data"));
            JSONObject userJson = JSONUtil.parseObj(dataJson.get("user"));
            Boolean userStatusOk = (Boolean) userJson.get("user_status_ok");
            Boolean isLogin = (Boolean) dataJson.get("is_login");

            ThirdPartyAccountVO thirdPartyAccountVO = new ThirdPartyAccountVO();
            thirdPartyAccountVO.setAccount((String) userInfoBodyJson.get("user_id_str"));
            thirdPartyAccountVO.setUserName((String) userInfoBodyJson.get("name"));
            thirdPartyAccountVO.setPlatForm(toutiao.getPlatform());
            thirdPartyAccountVO.setIsDisabled(userStatusOk && isLogin);

            String key = String.format(REDIS_THIRDPARTY_ACCOUNT, userId);
            Map<String, List<ThirdPartyAccountVO>> userAccountMap = redisUtils.redisGetThirdPartyAccountByMap(key);
            if (CollectionUtil.isEmpty(userAccountMap)) {
                list = new ArrayList<>();
                list.add(thirdPartyAccountVO);
            } else {
                list = userAccountMap.get(AccountPlatformEnum.TOUTIAO.getPlatform());
                if (CollectionUtil.isEmpty(list)) {
                    list.add(thirdPartyAccountVO);
                } else {
                    //list.add();
                    //这里需要判断账号是否在集合中，存在将原来的删除，用新的替换
                    for (ThirdPartyAccountVO accountVO : list) {
                        if (accountVO.getAccount().equals(thirdPartyAccountVO.getAccount()) && accountVO.getIsDisabled()) {
                            list.remove(accountVO);
                            break;
                        }
                    }
                    list.add(thirdPartyAccountVO);
                }
            }
            userAccountMap.put(AccountPlatformEnum.TOUTIAO.getPlatform(), list);
            redisUtils.redisSetInMap(key, userAccountMap);
            String cookieKey = String.format(REDIS_THIRDPARTY_ACCOUNT_COOKIE, AccountPlatformEnum.TOUTIAO.getPlatform(), thirdPartyAccountVO.getAccount());
            redisUtils.redisSetStrCookie(cookieKey, cookieStrValues);
        }, threadPoolExecutor);
    }

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
        map.put(thirdPartyFormName, list);
        redisUtils.redisSetInMap(key, map);
    }
}

