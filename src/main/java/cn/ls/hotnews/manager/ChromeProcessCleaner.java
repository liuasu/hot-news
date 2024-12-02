package cn.ls.hotnews.manager;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ChromeProcessCleaner {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        // 启动定时清理任务
        startCleanupTask();
        // 添加JVM关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    /**
     * 启动定时清理任务
     */
    private void startCleanupTask() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                log.info("开始执行ChromeDriver进程清理...");
                cleanupChromeDriverProcess();
            } catch (Exception e) {
                log.error("清理ChromeDriver进程失败", e);
            }
        }, 0, 10, TimeUnit.MINUTES);  // 初始延迟0分钟，每10分钟执行一次
    }

    /**
     * 清理ChromeDriver进程
     */
    private void cleanupChromeDriverProcess() {
        try {
            if (SystemUtils.IS_OS_WINDOWS) {
                Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
                log.info("Windows系统 - ChromeDriver进程已清理");
            }
        } catch (IOException e) {
            log.error("执行进程清理命令失败", e);
        }
    }

    /**
     * 手动触发清理
     */
    public void cleanupNow() {
        log.info("手动触发ChromeDriver进程清理");
        cleanupChromeDriverProcess();
    }

    /**
     * 关闭调度器
     */
    @PreDestroy
    public void shutdown() {
        log.info("关闭ChromeDriver进程清理调度器...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}