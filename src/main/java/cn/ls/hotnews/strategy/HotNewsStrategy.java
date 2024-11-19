package cn.ls.hotnews.strategy;

import cn.ls.hotnews.service.HotNewsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * title: HotNewsStrategy
 * author: liaoshuo
 * date: 2024/11/19 22:03
 * description: 策略
 */
@Component
public class HotNewsStrategy {

    @Resource
    private Map<String, HotNewsService> hotNewsServiceMap;

    public HotNewsService getHotNewsByPlatform(String key){
        return hotNewsServiceMap.get(key);
    }
}
