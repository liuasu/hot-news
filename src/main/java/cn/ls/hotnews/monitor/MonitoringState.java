package cn.ls.hotnews.monitor;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 监控状态
 *
 * @author 32093
 * @date 2024/12/27
 */
@Data
public class MonitoringState {
    private static final long MIN_INTERVAL = 15; // 最小发布间隔(分钟)
    private LocalDateTime lastPublishTime;

    public boolean canPublish() {
        if (lastPublishTime == null) return true;
        
        return LocalDateTime.now()
                .isAfter(lastPublishTime.plusMinutes(MIN_INTERVAL));
    }

    public void updateLastPublishTime() {
        this.lastPublishTime = LocalDateTime.now();
    }
} 