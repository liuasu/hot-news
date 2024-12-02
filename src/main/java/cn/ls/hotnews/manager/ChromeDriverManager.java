package cn.ls.hotnews.manager;

import cn.hutool.core.date.DateTime;
import cn.ls.hotnews.utils.ChromeDriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Slf4j
public class ChromeDriverManager {
    private static final long INACTIVITY_TIMEOUT = 5 * 60 * 1000; // 5分钟无操作超时
    private static final Map<ChromeDriver, Long> driverLastAccessTime = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    static {
        // 启动定时检查任务
        scheduler.scheduleAtFixedRate(ChromeDriverManager::checkInactiveBrowsers, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * 获取 ChromeDriver 实例
     */
    public static ChromeDriver getDriver() {
        ChromeDriver driver = ChromeDriverUtils.initHeadlessChromeDriver(null);
        driverLastAccessTime.put(driver, System.currentTimeMillis());
        return driver;
    }

    /**
     * 更新最后访问时间
     */
    public static void updateLastAccessTime(ChromeDriver driver) {
        driverLastAccessTime.put(driver, System.currentTimeMillis());
    }

    /**
     * 关闭并移除 driver
     */
    public static void closeDriver(ChromeDriver driver) {
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            driverLastAccessTime.remove(driver);
        }
    }

    /**
     * 检查并关闭不活跃的浏览器
     */
    private static void checkInactiveBrowsers() {
        long currentTime = System.currentTimeMillis();

        driverLastAccessTime.entrySet().removeIf(entry -> {
            ChromeDriver driver = entry.getKey();
            long lastAccessTime = entry.getValue();

            if (currentTime - lastAccessTime > INACTIVITY_TIMEOUT) {
                try {
                    log.info("关闭不活跃的浏览器实例，最后活动时间：{}",
                            new DateTime(lastAccessTime).toString("yyyy-MM-dd HH:mm:ss"));
                    driver.quit();
                    scheduler.shutdown();
                    driverLastAccessTime.clear();
                    //shutdown();
                } catch (Exception e) {
                    log.error("关闭浏览器时发生错误", e);
                }
                return true; // 从 Map 中移除
            }
            return false;
        });
    }

    /**
     * 关闭定时任务线程池
     */
    public static void shutdown() {
        scheduler.shutdown();
        // 关闭所有残留的浏览器实例
        driverLastAccessTime.keySet().forEach(driver -> {
            try {
                driver.quit();
            } catch (Exception e) {
                log.error("关闭浏览器时发生错误", e);
            }
        });
        driverLastAccessTime.clear();
    }
}