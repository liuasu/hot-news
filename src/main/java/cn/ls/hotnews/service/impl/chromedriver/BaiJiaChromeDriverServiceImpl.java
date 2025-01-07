package cn.ls.hotnews.service.impl.chromedriver;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.BusinessException;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.manager.ChromeDriverManager;
import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountDelReq;
import cn.ls.hotnews.model.dto.thirdpartyaccount.ThirdPartyAccountQueryReq;
import cn.ls.hotnews.model.entity.Article;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.ThirdPartyAccountVO;
import cn.ls.hotnews.service.ChromeDriverService;
import cn.ls.hotnews.service.HotApiService;
import cn.ls.hotnews.utils.ChromeDriverUtils;
import cn.ls.hotnews.utils.RedisUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.ls.hotnews.constant.CommonConstant.*;

/**
 * title: BaiJiaChromeDriverServiceImpl
 * author: liaoshuo
 * date: 2024/11/19 10:50
 * description: 头条操作
 */
@Slf4j
@Service("baijiaChrome")
public class BaiJiaChromeDriverServiceImpl implements ChromeDriverService {

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private HotApiService hotApiService;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 平台登录
     *
     * @param loginUser
     */
    @Override
    public void ChromeDriverPlatFormLogin(User loginUser) {
        Long userId = loginUser.getId();
        //查询登录地址、首页地址
        HotApi login = hotApiService.getPlatformAPI("baijia_login");
        HotApi indexPage = hotApiService.getPlatformAPI("baijia_index");
        ThrowUtils.throwIf(indexPage == null && login == null, ErrorCode.NOT_FOUND_ERROR);
        //然后从redis中查询多少账号
        String key = String.format(REDIS_THIRDPARTY_ACCOUNT, userId);
        Map<String, List<ThirdPartyAccountVO>> userAccountMap = redisUtils.redisGetThirdPartyAccountByMap(key);
        int accountCount = 1;
        if (CollectionUtil.isNotEmpty(userAccountMap)) {
            List<ThirdPartyAccountVO> accountVOList = userAccountMap.get(BAIJIA);
            if (CollectionUtil.isEmpty(accountVOList)) {
                userAccountMap.put("baijia", new ArrayList<>());
            } else {
                accountCount = accountVOList.size() + 1;
            }

        }
        //操作浏览器
        String proFileName = String.format("baijia%s", accountCount);
        ChromeDriver driver = ChromeDriverUtils.initChromeDriver(proFileName);
        ChromeDriverManager.updateLastAccessTime(driver);
        try {
            //发送请求
            driver.get(login.getApiURL());
            Thread.sleep(15000);
        } catch (Exception e) {
            log.error("ChromeDriver error message: ", e);
            throw new RuntimeException(e);
        }

        driver.quit();
        getBaiJiaCookie(indexPage, userAccountMap, key, proFileName);
    }

    /**
     * 获取百佳饼干
     *
     * @param indexPage      百家号首页
     * @param userAccountMap 用户帐户映射
     * @param key            钥匙
     * @param proFileName    pro 文件名
     */
    private void getBaiJiaCookie(HotApi indexPage, Map<String, List<ThirdPartyAccountVO>> userAccountMap, String key, String proFileName) {
        CompletableFuture.runAsync(() -> {
            List<ThirdPartyAccountVO> list;
            //使用无头浏览器进行操作
            ChromeDriver driver = ChromeDriverUtils.initHeadlessChromeDriver(proFileName);
            ChromeDriverManager.updateLastAccessTime(driver);
            driver.get(indexPage.getApiURL());
            //请求的日志
            LogEntries performanceLog = driver.manage().logs().get(LogType.PERFORMANCE);
            //关闭浏览器
            driver.quit();
            //提取Cookie
            Set<String> baiJiaCookie = extractCookies(performanceLog);
            //通过 cookie 获取到首页所发的请求中的请求头信息
            Map<String, String> headMap = extractRequestHead(baiJiaCookie);
            //获取到用户信息
            ThrowUtils.throwIf(CollectionUtil.isEmpty(headMap), ErrorCode.NOT_FOUND_ERROR);
            ThirdPartyAccountVO thirdPartyAccountVO = extractUserInfo(headMap);

            if (CollectionUtil.isEmpty(userAccountMap)) {
                list = new ArrayList<>();
                list.add(thirdPartyAccountVO);
            } else {
                list = userAccountMap.get(BAIJIA);
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


            userAccountMap.put(BAIJIA, list);
            redisUtils.redisSetInMap(key, userAccountMap);
            String cookieKey = String.format(REDIS_THIRDPARTY_ACCOUNT_COOKIE, BAIJIA, thirdPartyAccountVO.getAccount());
            redisUtils.redisSetStrCookie(cookieKey, headMap.get("cookie"));
            redisUtils.redisSetObj(String.format(REDIS_ACCOUNT_PROFILENAME, thirdPartyAccountVO.getAccount()), proFileName);
        }, threadPoolExecutor);
    }

    /**
     * 提取用户信息
     *
     * @param extractRequestHead 提取请求头
     * @return {@link ThirdPartyAccountVO }
     */
    private ThirdPartyAccountVO extractUserInfo(Map<String, String> extractRequestHead) {
        ThirdPartyAccountVO thirdPartyAccountVO = new ThirdPartyAccountVO();

        String cookie = extractRequestHead.get("cookie");
        String token = extractRequestHead.get("token");
        //发送请求
        String body = HttpUtil.createGet("https://baijiahao.baidu.com/builder/app/appinfo")
                .header("token", token)
                .cookie(cookie).execute().body();
        if (StringUtils.isNotBlank(body)) {
            JsonElement jsonElement = JsonParser.parseString(body);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject userObject = jsonObject.getAsJsonObject("data").getAsJsonObject("user");
            thirdPartyAccountVO.setAccount(userObject.get("shoubai_c_appid").getAsString());
            thirdPartyAccountVO.setUserName(userObject.get("name").getAsString());
            thirdPartyAccountVO.setPlatForm("baijia");
            thirdPartyAccountVO.setIsDisabled(true);
            return thirdPartyAccountVO;
        }
        return null;
    }

    /**
     * 提取 Token
     *
     * @param baiJiaCookie 百家饼干
     * @return {@link String }
     */
    private Map<String, String> extractRequestHead(Set<String> baiJiaCookie) {
        Map<String, String> haderMap = new HashMap<>();
        for (String cookie : baiJiaCookie) {
            String body = HttpUtil.createGet("https://baijiahao.baidu.com/pcui/im/getimtoken").cookie(cookie).execute().body();
            if (StringUtils.isNotBlank(body)) {
                JSONObject bodyJson = JSONUtil.parseObj(body);
                String errno = bodyJson.get("errno").toString();
                if (errno.equals("0")) {
                    haderMap.put("cookie", cookie);
                    haderMap.put("token", (String) JSONUtil.parseObj(bodyJson.get("data")).get("token"));
                    return haderMap;
                }
            }
        }
        return null;
    }

    /**
     * 提取 Cookie
     *
     * @param performanceLog 性能日志
     * @return {@link Set }<{@link String }>
     */
    private Set<String> extractCookies(LogEntries performanceLog) {
        Set<String> cookies = new HashSet<>();

        for (LogEntry entry : performanceLog) {
            String message = entry.getMessage();
            JSONObject jsonMessage = JSONUtil.parseObj(message).getJSONObject("message");
            String packetMethod = jsonMessage.getStr("method");

            if (isNetworkMethod(packetMethod)) {
                extractCookieFromParams(jsonMessage.getJSONObject("params"), cookies);
            }
        }

        return cookies;
    }

    /**
     * 是网络方法
     *
     * @param method 方法
     * @return boolean
     */
    private boolean isNetworkMethod(String method) {
        return method.startsWith("Network");
    }

    /**
     * 从参数中提取 cookie
     *
     * @param params  参数
     * @param cookies 饼干
     */
    private void extractCookieFromParams(JSONObject params, Set<String> cookies) {
        if (params.containsKey("headers")) {
            JSONObject headersJson = params.getJSONObject("headers");
            String cookie = headersJson.getStr("Cookie");

            if (isValidCookie(cookie)) {
                cookies.add(cookie);
            }
        }
    }

    /**
     * 是有效 Cookie
     *
     * @param cookie 饼干
     * @return boolean
     */
    private boolean isValidCookie(String cookie) {
        if (cookie == null || cookie.isEmpty()) {
            return false;
        }

        int lastIndexOf = cookie.lastIndexOf("; ") + 2;
        return lastIndexOf < cookie.length() - 1 &&
                cookie.charAt(lastIndexOf) == 'R' &&
                cookie.charAt(lastIndexOf + 1) == 'T' &&
                cookie.charAt(cookie.length() - 1) == '"';
    }

    /**
     * 发布文章
     * todo
     *
     * @param userIdStr 用户 ID str
     * @param article   品
     * @param imgMap    IMG 地图
     */
    @Override
    public void chromePublishArticle(String userIdStr, Article article, Map<String, List<String>> imgMap) {
        //先查询头条的发布文章的地址
        HotApi platformAPI = hotApiService.getPlatformAPI("biajia_publish");
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        //从redis中拿出登录所放的文件地址
        String proFileName = (String) redisUtils.redisGetObj(String.format(REDIS_ACCOUNT_PROFILENAME, userIdStr));
        ThrowUtils.throwIf(proFileName == null, ErrorCode.PARAMS_ERROR, "百家号未登录,请先登录");
        //浏览器进程操作
        ChromeDriver driver = null;
        try {
            driver = ChromeDriverUtils.initChromeDriver(proFileName);
            driver.get(platformAPI.getApiURL());
            Thread.sleep(3000);
            // 输入文章标题
            WebElement titleInput = ChromeDriverUtils.driverFindElementByCssSelector(driver, "textarea[placeholder='请输入标题（8 - 30字）']");
            titleInput.click();
            titleInput.sendKeys(article.getTitle());
            titleInput.sendKeys(Keys.TAB);
            // 输入文章内容并插入图片
            WebElement contentIframe = ChromeDriverUtils.driverFindElementByCssSelector(driver, "#ueditor_0");

            disposeConTextByImages(driver, contentIframe, article.getConText(), imgMap);

            Thread.sleep(5000);
        } catch (Exception e) {
            log.error("百家号文章发布失败,错误信息:{}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

    }


    /**
     * 按图像处理文本
     *
     * @param context 上下文
     * @param imgMap  IMG 地图
     */
    private void disposeConTextByImages(ChromeDriver driver, WebElement proseMirror, String context, Map<String, List<String>> imgMap) {
        context = context.replace("\n\n", "\n");

        //根据。进行分割，每两个句号为一组
        String[] strings = context.split("。");
        List<String> imgList = new ArrayList<>();
        for (String key : imgMap.keySet()) {
            imgList.addAll(imgMap.get(key));
        }
        //文章中图片随机根据 firstIndex、lastIndex 出现
        int length = strings.length;
        // 确保索引不会越界
        int firstIndex = 0;
        int lastIndex = 3;
        try {
            //睡眠3秒后打开一个新标签页进行复制图片
            Thread.sleep(3000);
            ((JavascriptExecutor) driver).executeScript("window.open()");
            //获取标签集合,根据下标选中操作的标签页
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            //打开新的标签页用于复制图片
            for (String imgUrl : imgList) {
                driver.switchTo().window(tabs.get(1));
                driver.get(imgUrl);
                //直接通过键盘属性 "ctrl + c" 进程操作
                pasteDownChrome(driver, proseMirror, tabs);
                proseMirror.sendKeys(getContextByIndex(firstIndex, lastIndex, strings));
                Thread.sleep(1500);
                firstIndex = lastIndex + 1;
                lastIndex = lastIndex + lastIndex;
            }
            //文章最后内容
            proseMirror.sendKeys(getContextByIndex(lastIndex, length - 1, strings));
        } catch (InterruptedException e) {
            log.error("百家号文章发布异常,异常信息:{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "百家号文章发布异常");
        }

        // 设置文章封面
        proseMirror.sendKeys(Keys.PAGE_DOWN);
        String str = imgList.size() >= 3 ? "three" : "one";
        ChromeDriverUtils.driverFindElementByCssSelector(driver, "input[value='" + str + "']").click();
        choosePicture(driver,str);

        // 发布文章
        driver.findElement(By.cssSelector("button[class*='always-blue']")).click();
    }

    /**
     * 封面选择
     */
    private void choosePicture(ChromeDriver driver,String str) {
        // 选择封面图片
        WebElement coverImageBtn = driver.findElement(By.cssSelector("html > body > div:nth-of-type(1) > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div > div:nth-of-type(1) > div > form > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div:nth-of-type(1) > div > div"));
        coverImageBtn.click();
        WebElement imageSelect = driver.findElement(By.cssSelector("html > body > div:nth-of-type(6) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div > div > div:nth-of-type(2) > div > div > div > div:nth-of-type(1) > div > div"));
        imageSelect.click();
        // 确认封面图片
        driver.findElement(By.cssSelector("button[class='cheetah-btn css-11mocfm cheetah-btn-primary cheetah-btn-solid cheetah-public acss-t8dvyk acss-1kjo6pu acss-1g9lkh4 acss-18ub98p acss-uv0qn4 acss-58e25w acss-1grxnxm acss-1izrri0 cheetah-btn-L cheetah-btn-text-primary']")).click();

        if(str.equals("three")){
            coverImageBtn = driver.findElement(By.cssSelector("html > body > div:nth-of-type(1) > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div > div:nth-of-type(1) > div > form > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div:nth-of-type(1) > div > div"));
            coverImageBtn.click();
            imageSelect = ChromeDriverUtils.driverFindElementByCssSelector(driver, "html > body > div:nth-of-type(6) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div > div > div:nth-of-type(2) > div > div > div > div:nth-of-type(2) > div > div");
            imageSelect.click();
            driver.findElement(By.cssSelector("button[class='cheetah-btn css-11mocfm cheetah-btn-primary cheetah-btn-solid cheetah-public acss-t8dvyk acss-1kjo6pu acss-1g9lkh4 acss-18ub98p acss-uv0qn4 acss-58e25w acss-1grxnxm acss-1izrri0 cheetah-btn-L cheetah-btn-text-primary']")).click();

            coverImageBtn = driver.findElement(By.cssSelector("html > body > div:nth-of-type(1) > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div > div:nth-of-type(1) > div > form > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(3) > div:nth-of-type(1) > div > div"));
            coverImageBtn.click();
            imageSelect = ChromeDriverUtils.driverFindElementByCssSelector(driver, "html > body > div:nth-of-type(6) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div > div > div:nth-of-type(2) > div > div > div > div:nth-of-type(3) > div > div");
            imageSelect.click();
            driver.findElement(By.cssSelector("button[class='cheetah-btn css-11mocfm cheetah-btn-primary cheetah-btn-solid cheetah-public acss-t8dvyk acss-1kjo6pu acss-1g9lkh4 acss-18ub98p acss-uv0qn4 acss-58e25w acss-1grxnxm acss-1izrri0 cheetah-btn-L cheetah-btn-text-primary']")).click();
        }
    }

    /**
     * 图片复制粘贴
     *
     * @param driver      司机
     * @param proseMirror 散文镜
     * @param tabs        制表符
     */
    private void pasteDownChrome(ChromeDriver driver, WebElement proseMirror, ArrayList<String> tabs) {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "c");
        driver.switchTo().window(tabs.get(0));
        proseMirror.sendKeys(Keys.CONTROL + "v");
        proseMirror.sendKeys(Keys.CONTROL, Keys.ARROW_DOWN);
    }

    /**
     * 按索引获取上下文(插入图片操作)
     *
     * @param inderNumber 内部编号
     * @param number      数
     * @param str         str
     * @return {@link String }
     */
    private String getContextByIndex(int inderNumber, int number, String[] str) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = inderNumber; i < number; i++) {
            stringBuilder.append(str[i]).append("\n");
        }
        return stringBuilder.toString();
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
        String cookieKey = String.format(REDIS_THIRDPARTY_ACCOUNT_COOKIE, BAIJIA, account);
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
            redisUtils.redisDelObj(Arrays.asList(proFileNameKey, cookieKey));

            FileUtil.del(String.format("%s\\%s", ChromeDriverUtils.getDriverPath().get("user_data"), proFileName));
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
            HotApi platformAPI = hotApiService.getPlatformAPI("baijia_index");
            ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
            ChromeDriver driver = ChromeDriverUtils.initChromeDriver(proFileName);
            driver.get(platformAPI.getApiURL());
            ChromeDriverManager.updateLastAccessTime(driver);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

