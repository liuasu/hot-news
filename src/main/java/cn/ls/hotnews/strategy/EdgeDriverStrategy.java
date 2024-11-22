package cn.ls.hotnews.strategy;

import cn.ls.hotnews.service.EdgeDriverService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * title: EdgeDriverStrategy
 * author: liaoshuo
 * date: 2024/11/20 14:56
 * description:
 */
@Component
public class EdgeDriverStrategy {

    @Resource
    private Map<String, EdgeDriverService> edgeDriverServiceMap;

    public EdgeDriverService getEdgeDriverKey(String key){
       return edgeDriverServiceMap.get(key);
    }


}
