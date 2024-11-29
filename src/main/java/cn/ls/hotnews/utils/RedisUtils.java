package cn.ls.hotnews.utils;

import cn.hutool.core.date.DateTime;
import cn.ls.hotnews.model.vo.HotNewsVO;
import cn.ls.hotnews.model.vo.ThirdPartyAccountVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        redisTemplate.opsForValue().set(key, hotNewsVOList, 1, TimeUnit.HOURS);
    }

    public void redisSetInMap(String key, Map<String, List<ThirdPartyAccountVO>> map) {
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.opsForHash().getOperations().expire(key, 10, TimeUnit.DAYS);
    }

    public void redisSetStrCookie(String key, String str) {
        redisTemplate.opsForValue().set(key, str, 10, TimeUnit.DAYS);
    }

    /**
     * 第三方平台账号存放
     *
     * @param key       钥匙
     * @param accountVO 账户 vo
     */
    public void redisSetThirdPartyAccount(String key, ThirdPartyAccountVO accountVO) {
        redisTemplate.opsForValue().set(key, accountVO);
    }

    public void redisSetInOneHour(String key, DateTime nowDateTime) {
        redisTemplate.opsForValue().set(key, nowDateTime, 1, TimeUnit.HOURS);
    }

    public List<HotNewsVO> redisGet(String key) {
        return (List<HotNewsVO>) redisTemplate.opsForValue().get(key);
    }

    /**
     * Redis 获取 1 小时时间
     *
     * @param key 钥匙
     * @return {@link DateTime }
     */
    public DateTime redisGetOneHourTime(String key) {
        return (DateTime) redisTemplate.opsForValue().get(key);
    }

    public ThirdPartyAccountVO redisGetThirdPartyAccount(String key) {
        return (ThirdPartyAccountVO) redisTemplate.opsForValue().get(key);
    }


    public Map<String, List<ThirdPartyAccountVO>> redisGetThirdPartyAccountByMap(String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        Map<String, List<ThirdPartyAccountVO>> map = new HashMap<>();
        for (Object o : entries.keySet()) {
            map.put((String) o, (List<ThirdPartyAccountVO>) entries.get(o));
        }
        return map;
    }

    public String redisGetStrCookie(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
}
