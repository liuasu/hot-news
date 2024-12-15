package cn.ls.hotnews.service.impl.chromedriver;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpException;
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
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
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
@Service("toutiaoChrome")
public class TouTiaoChromeDriverServiceImpl implements ChromeDriverService {

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
        HotApi platformAPI = hotApiService.getPlatformAPI("toutiao_article_publish");
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        //从redis中拿出登录所放的文件地址
        String proFileName = (String) redisUtils.redisGetObj(String.format(REDIS_ACCOUNT_PROFILENAME, userIdStr));
        ThrowUtils.throwIf(proFileName == null, ErrorCode.PARAMS_ERROR, "头条号未登录,请先登录");
        //浏览器进程操作
        ChromeDriver driver = null;
        try {
            driver = ChromeDriverUtils.initChromeDriver(proFileName);
            driver.get(platformAPI.getApiURL());
            //先点击该页面让遮挡的部分收起来
            Thread.sleep(3000);
            driver.findElement(By.cssSelector("body")).click();
            //通过css获取元素
            WebElement title = driver.findElement(By.cssSelector("textarea[placeholder='请输入文章标题（2～30个字）']"));
            // 点击文本框
            title.click();
            // 输入文本
            title.sendKeys(article.getTitle().replace("\n", "").trim());
            //通过className获取到编辑正文元素，并点击(聚焦)
            WebElement proseMirror = driver.findElement(By.className("ProseMirror"));
            proseMirror.click();
            disposeConTextByImages(driver, proseMirror, article.getConText(), imgMap);

            Thread.sleep(5000);
        } catch (Exception e) {
            log.error("头条文章发布失败,错误信息:{}", e.getMessage());
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
        //没有图片就直接将文章写入
        if (imgMap.isEmpty()) {
            proseMirror.sendKeys(context);
            //无封面点击
            WebElement element = driver.findElement(By.cssSelector("html > body > div:nth-of-type(1) > div > div:nth-of-type(3) > section > main > div:nth-of-type(2) > div > div > div:nth-of-type(2) > div > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div:nth-of-type(2) > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > label:nth-of-type(3)"));
            //driver.executeScript("arguments[0].scrollIntoView(true);", element);
            driver.executeScript("arguments[0].scrollIntoView();" +
                    "window.scrollBy(0, -window.innerHeight * 0.75);", element);
            element.click();
        } else {
            //根据。进行分割，每两个句号为一组
            String[] strings = context.split("。");
            List<String> imgList = new ArrayList<>();
            for (String key : imgMap.keySet()) {
                imgList.addAll(imgMap.get(key));
            }
            if (CollectionUtil.isNotEmpty(imgList)) {
                //文章中图片随机根据 firstIndex、lastIndex 出现
                int length = strings.length;
                int firstIndex = RandomUtil.randomInt(0, length);
                int lastIndex = RandomUtil.randomInt(firstIndex, length);

                proseMirror.sendKeys(getContextByIndex(0, firstIndex, strings));
                try {
                    //睡眠3秒后打开一个新标签页进行复制图片
                    Thread.sleep(3000);
                    ((JavascriptExecutor) driver).executeScript("window.open()");
                    //获取标签集合,根据下标选中操作的标签页
                    ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
                    //打开新的标签页用于复制图片
                    driver.switchTo().window(tabs.get(1));
                    driver.get(imgList.get(0));
                    //直接通过键盘属性 "ctrl + c" 进程操作+
                    pasteDownChrome(driver, proseMirror, tabs);
                    proseMirror.sendKeys(getContextByIndex(firstIndex, lastIndex, strings));

                    Thread.sleep(3000);
                    if (imgList.size() > 1) {
                        driver.switchTo().window(tabs.get(1));
                        driver.get(imgList.get(1));
                    }
                    pasteDownChrome(driver, proseMirror, tabs);
                    //文章最后内容
                    proseMirror.sendKeys(getContextByIndex(lastIndex, length, strings));
                } catch (InterruptedException e) {
                    log.error("头条文章发布异常,异常信息:{}", e.getMessage());
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "头条文章发布异常");
                }
            } else {
                proseMirror.sendKeys(context);
                //无封面点击
                WebElement element = driver.findElement(By.cssSelector("html > body > div:nth-of-type(1) > div > div:nth-of-type(3) > section > main > div:nth-of-type(2) > div > div > div:nth-of-type(2) > div > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div:nth-of-type(2) > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > label:nth-of-type(3)"));
                //driver.executeScript("arguments[0].scrollIntoView(true);", element);
                driver.executeScript("arguments[0].scrollIntoView();" +
                        "window.scrollBy(0, -window.innerHeight * 0.75);", element);
                element.click();
            }

        }
        //作品声明ai创作点击
        WebElement element = driver.findElement(By.cssSelector("html > body > div:nth-of-type(1) > div > div:nth-of-type(3) > section > main > div:nth-of-type(2) > div > div > div:nth-of-type(2) > div > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div:nth-of-type(2) > div:nth-of-type(9) > div > div:nth-of-type(2) > div > div > span > span:nth-of-type(1)"));
        driver.executeScript("arguments[0].scrollIntoView(true);", element);
        element.findElement(By.tagName("label")).click();
        //点击预览发布按钮
        driver.findElement(
                By.cssSelector("button.byte-btn.byte-btn-primary.byte-btn-size-large.byte-btn-shape-square.publish-btn.publish-btn-last")
        ).click();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        driver.findElement(By.cssSelector("html > body > div:nth-of-type(1) > div > div:nth-of-type(3) > section > main > div:nth-of-type(2) > div > div > div:nth-of-type(2) > div > div > div:nth-of-type(1) > div > div:nth-of-type(3) > div > button:nth-of-type(2)")
        ).click();
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
            HotApi platformAPI = hotApiService.getPlatformAPI("toutiao_page_index");
            ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
            ChromeDriver driver = ChromeDriverUtils.initChromeDriver(proFileName);
            driver.get(platformAPI.getApiURL());
            ChromeDriverManager.updateLastAccessTime(driver);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

