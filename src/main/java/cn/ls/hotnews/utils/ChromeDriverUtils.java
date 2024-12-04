package cn.ls.hotnews.utils;


import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * title: ChromeDriverUtils
 * author: liaoshuo
 * date: 2024/11/19 10:51
 * description:
 */
@Slf4j
public class ChromeDriverUtils {

    private static ChromeOptions init(String profileName) {
        System.setProperty("webdriver.chrome.driver", "D:\\桌面\\chrome-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments(String.format("user-data-dir=D:\\桌面\\chrome-win64\\selenium\\%s", profileName));
        options.addArguments("profile-directory=" + profileName);
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return options;
    }

    public static ChromeDriver initChromeDriver(String profileName) {
        return new ChromeDriver(init(profileName));
    }


    /**
     * 自动操作(不打开浏览器)
     *
     * @return {@link ChromeDriver }
     */
    public static ChromeDriver initHeadlessChromeDriver(String profileName) {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                log.info("初始化Chrome");
                ChromeOptions options = init(profileName);
                // 无头模式相关配置
                options.addArguments("--headless"); // 设定为无头模式
                return new ChromeDriver(options);
            } catch (Exception e) {
                retryCount++;
                log.error("初始化Chrome失败，尝试次数：{}/{}，错误：{}",
                        retryCount, maxRetries, e.getMessage());

                if (retryCount >= maxRetries) {
                    throw new RuntimeException("初始化Chrome失败，已超过最大重试次数", e);
                }

                try {
                    Thread.sleep(1000L * retryCount); // 递增等待时间
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("初始化过程被中断", ie);
                }
            }

        }
        throw new RuntimeException("初始化Chrome失败");
    }
}
