package cn.ls.hotnews.service.impl.chromedriver;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.manager.ChromeDriverManager;
import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountDelReq;
import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountQueryReq;
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

import static cn.ls.hotnews.constant.CommonConstant.*;
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

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private HotApiService hotApiService;

    /**
     * 平台登录
     *
     * @param loginUser
     */
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 平台登录
     */
    @Override
    public void ChromeDriverPlatFormLogin(User loginUser) {
        Long userId = loginUser.getId();
        HotApi login = hotApiService.getPlatformAPI("toutiao_login");
        HotApi userInfo = hotApiService.getPlatformAPI("toutiao_getUserInfo");
        HotApi userLoginStatus = hotApiService.getPlatformAPI("toutiao_user_login_status");
        ThrowUtils.throwIf(userInfo == null && userLoginStatus == null && login == null, ErrorCode.NOT_FOUND_ERROR);
        //然后从redis中查询多少账号
        String key = String.format(REDIS_THIRDPARTY_ACCOUNT, userId);
        Map<String, List<ThirdPartyAccountVO>> userAccountMap = redisUtils.redisGetThirdPartyAccountByMap(key);
        int accountCount = 1;
        if (CollectionUtil.isNotEmpty(userAccountMap)) {
            accountCount = userAccountMap.get(TOUTIAO).size() + 1;
        }
        //操作浏览器
        String proFileName = String.format("toutiao%s", accountCount);
        ChromeDriver driver = ChromeDriverUtils.initChromeDriver(proFileName);
        ChromeDriverManager.updateLastAccessTime(driver);
        Set<Cookie> cookieSet;
        try {
            //发送请求
            driver.get(login.getApiURL());
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

        extracted(cookieSet, userInfo, userLoginStatus, userAccountMap, key, proFileName);
    }

    private void extracted(Set<Cookie> cookieSet, HotApi userInfo, HotApi userLoginStatus, Map<String, List<ThirdPartyAccountVO>> userAccountMap, String key, String proFileName) {
        CompletableFuture.runAsync(() -> {
            String userInfoBody;
            String userLoginStatusInfoBody;
            List<ThirdPartyAccountVO> list;
            Map<String, String> map = cookieSet.stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
            StringBuilder stringBuilder = new StringBuilder();
            for (String cookieKey : TOUTIAO_COOKIE_SORT_LIST) {
                stringBuilder.append(String.format("%s=%s; ", cookieKey, map.get(cookieKey)));
            }
            String cookieStrValues = stringBuilder.toString();
            //通过发送请求获取到用户信息
            try {
                userInfoBody = HttpUtil.createGet(userInfo.getApiURL()).cookie(cookieStrValues).execute().body();
                userLoginStatusInfoBody = HttpUtil.createGet(userLoginStatus.getApiURL()).cookie(cookieStrValues).execute().body();
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
            String userIdStr = (String) userInfoBodyJson.get("user_id_str");
            thirdPartyAccountVO.setAccount(userIdStr);
            thirdPartyAccountVO.setUserName((String) userInfoBodyJson.get("name"));
            thirdPartyAccountVO.setPlatForm("toutiao");
            thirdPartyAccountVO.setIsDisabled(userStatusOk && isLogin);

            if (CollectionUtil.isEmpty(userAccountMap)) {
                list = new ArrayList<>();
                list.add(thirdPartyAccountVO);
            } else {
                list = userAccountMap.get(TOUTIAO);
                if (CollectionUtil.isEmpty(list)) {
                    list = new ArrayList<>();
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
            userAccountMap.put(TOUTIAO, list);
            redisUtils.redisSetInMap(key, userAccountMap);
            String cookieKey = String.format(REDIS_THIRDPARTY_ACCOUNT_COOKIE, TOUTIAO, thirdPartyAccountVO.getAccount());
            redisUtils.redisSetStrCookie(cookieKey, cookieStrValues);
            redisUtils.redisSetObj(String.format(REDIS_ACCOUNT_PROFILENAME, userIdStr), proFileName);
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

        String accountKey = String.format(REDIS_THIRDPARTY_ACCOUNT, loginUser.getId());
        String cookieKey = String.format(REDIS_THIRDPARTY_ACCOUNT_COOKIE, TOUTIAO, account);
        String proFileNameKey = String.format(REDIS_ACCOUNT_PROFILENAME, account);
        String proFileName = (String) redisUtils.redisGetObj(proFileNameKey);
        Map<String, List<ThirdPartyAccountVO>> map = redisUtils.redisGetThirdPartyAccountByMap(accountKey);
        List<ThirdPartyAccountVO> list = map.get(thirdPartyFormName);
        if (CollectionUtil.isNotEmpty(list)) {
            ThirdPartyAccountVO thirdPartyAccountVO = list.get(index);
            if (thirdPartyAccountVO.getAccount().equals(account)) {
                list.remove(thirdPartyAccountVO);
            }
            map.put(thirdPartyFormName, list);
            redisUtils.redisSetInMap(accountKey, map);
            redisUtils.redisDelObj(Arrays.asList(proFileNameKey,cookieKey));
            FileUtil.del(String.format("D:\\桌面\\chrome-win64\\selenium\\%s", proFileName));
        }
    }

    /**
     * 按用户 ID 查询
     *
     * @param queryReq  查询 req
     * @param loginUser 登录用户
     */
    @Override
    public void queryByUserIdStr(ThirdPartyAccountQueryReq queryReq, User loginUser) {
        try {
            String userIdStr = queryReq.getUserIdStr();
            String proFileName = (String) redisUtils.redisGetObj(String.format(REDIS_ACCOUNT_PROFILENAME, userIdStr));
            HotApi platformAPI = hotApiService.getPlatformAPI("toutiao_page_index");
            ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
            ChromeDriver driver = ChromeDriverUtils.initChromeDriver(proFileName);
            driver.get(platformAPI.getApiURL());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
