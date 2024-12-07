package cn.ls.hotnews.strategy;

import cn.ls.hotnews.ai.AIService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * title: AIStrategy
 * author: liaoshuo
 * date: 2024/12/7 14:41
 * description:
 */
@Component
public class AIStrategy {

    @Resource
    private Map<String, AIService> aiServiceMap;

    public AIService getAiByKey(String key) {
        return aiServiceMap.get(key);
    }
}
