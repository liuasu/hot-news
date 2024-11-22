package cn.ls.hotnews.utils;

import cn.hutool.core.date.DateTime;
import cn.ls.hotnews.model.vo.HotNewsVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * title: RedisUtils
 * author: liaoshuo
 * date: 2024/11/22 21:46
 * description:
 */
@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 有效期一小时
     *
     * @param key           钥匙
     * @param hotNewsVOList 热门新闻 Volist
     */
    public void redisSetInOneHour(String key, List<HotNewsVO> hotNewsVOList) {
        redisTemplate.opsForValue().set(key, hotNewsVOList,1, TimeUnit.HOURS);
    }

    public void redisSetInOneHour(String key, DateTime nowDateTime) {
        redisTemplate.opsForValue().set(key, nowDateTime,1, TimeUnit.HOURS);
    }

    public List<HotNewsVO> redisGet(String key) {
        return (List<HotNewsVO>) redisTemplate.opsForValue().get(key);
    }

    public DateTime redisGetOneHourTime(String key) {
        return (DateTime) redisTemplate.opsForValue().get(key);
    }
}
