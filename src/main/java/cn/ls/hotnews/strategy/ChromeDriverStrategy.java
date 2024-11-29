package cn.ls.hotnews.strategy;

import cn.ls.hotnews.service.ChromeDriverService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * title: ChromeDriverStrategy
 * author: liaoshuo
 * date: 2024/11/20 14:56
 * description:
 */
@Component
public class ChromeDriverStrategy {

    @Resource
    private Map<String, ChromeDriverService> chromeDriverServiceMap;

    public ChromeDriverService getChromeDriverKey(String key){
       return chromeDriverServiceMap.get(key);
    }


}
