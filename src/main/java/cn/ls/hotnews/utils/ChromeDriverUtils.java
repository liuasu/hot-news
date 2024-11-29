package cn.ls.hotnews.utils;


import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * title: ChromeDriverUtils
 * author: liaoshuo
 * date: 2024/11/19 10:51
 * description:
 */

public class ChromeDriverUtils {

    private static ChromeOptions init() {
        System.setProperty("webdriver.chrome.driver", "D:\\桌面\\chrome-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("user-data-dir=D:\\桌面\\chrome-win64\\selenium");
        return options;
    }

    public static ChromeDriver initChromeDriver() {
        return new ChromeDriver(init());
    }


    /**
     * 自动操作(不打开浏览器)
     *
     * @return {@link ChromeDriver }
     */
    public static ChromeDriver initChromeDriverNotHeadless() {
        return new ChromeDriver(init().setHeadless(true));
    }
}
