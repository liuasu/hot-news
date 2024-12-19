package cn.ls.hotnews.utils;


import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;

/**
 * title: ChromeDriverUtils
 * author: liaoshuo
 * date: 2024/11/19 10:51
 * description:
 */
@Slf4j
public class ChromeDriverUtils {

    public static Map<String, Object> getDriverPath() {
        Yaml yaml = new Yaml();
        InputStream inputStream = ChromeDriverUtils.class.getClassLoader().getResourceAsStream("application.yml");
        Map<String, Object> config = yaml.load(inputStream);
        Object o = ((Map<String, Object>) config.get("webdriver")).get("chrome");
        return  (Map<String, Object>) o;
    }

    private static ChromeOptions init(String profileName) {
        Map<String, Object> path = getDriverPath();
        System.setProperty("webdriver.chrome.driver", String.valueOf(path.get("driver_path")));
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments(String.format("user-data-dir=%s\\%s", path.get("user_data"), profileName));
        options.addArguments("profile-directory=" + profileName);
        options.addArguments("--no-sandbox"); // 禁用沙盒
        options.addArguments("--disable-dev-shm-usage"); // 禁用共享内存

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);
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
