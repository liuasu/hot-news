package cn.ls.hotnews.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.enums.HotPlatformEnum;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.vo.HotNewsVO;
import cn.ls.hotnews.service.HotApiService;
import cn.ls.hotnews.service.HotNewsService;
import cn.ls.hotnews.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.ls.hotnews.constant.CommonConstant.REDIS_BILIBILI;
import static cn.ls.hotnews.constant.CommonConstant.REDIS_BILIBILI_DTATETIME;

/**
 * title: BiliBiliHotNewsServiceImpl
 * author: liaoshuo
 * date: 2024/11/20 12:12
 * description:
 */
@Slf4j
@Component("bilibili")
public class BiliBiliHotNewsServiceImpl implements HotNewsService {

    //type: {
    //    name: "排行榜分区",
    //    type: {
    //      0: "全站",
    //      1: "动画",
    //      3: "音乐",
    //      4: "游戏",
    //      5: "娱乐",
    //      36: "科技",
    //      119: "鬼畜",
    //      129: "舞蹈",
    //      155: "时尚",
    //      160: "生活",
    //      168: "国创相关",
    //      188: "数码",
    //      181: "影视",
    //    },
    //}
    //
    //https://api.bilibili.com/x/web-interface/ranking/v2?tid=${type}(这里接分区id)&type=all&${wbiData}


    private final int[] MIXIN_KEY_ENC_TAB = {
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49, 33, 9, 42, 19, 29, 28,
            14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40, 61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54,
            21, 56, 59, 6, 63, 57, 62, 11, 36, 20, 34, 44, 52
    };
    @Resource
    private HotApiService hotApiService;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 热点新闻列表
     *
     * @return
     */
    @Override
    public List<HotNewsVO> hotNewsList() {
        List<HotNewsVO> biliHotList = redisUtils.redisGet(REDIS_BILIBILI);
        if (biliHotList != null) {
            return biliHotList;
        }
        biliHotList = new ArrayList<>();
        HotApi platformAPI = hotApiService.getPlatformAPI(HotPlatformEnum.BILIBILI.getValues());
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        String url = String.format("https://api.bilibili.com/x/web-interface/ranking/v2?tid=0&type=all&%s", gainWBI());
        try {
            String body = HttpUtil.createGet(url)
                    .header("Referer", "https://www.bilibili.com/ranking/all")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36").execute().body();
            if (StringUtils.isBlank(body)) {
                return new ArrayList<>();
            }
            Object bodyData = JSONUtil.parseObj(body).get("data");
            JSONArray jsonArrayList = JSONUtil.parseObj(bodyData).getJSONArray("list");
            for (int i = 0; i < 20; i++) {
                Map<String, Object> hotMap = (Map<String, Object>) jsonArrayList.get(i);
                HotNewsVO hotNewsVO = new HotNewsVO();
                String bvidStr = String.valueOf(hotMap.get("bvid"));
                hotNewsVO.setBiId(bvidStr);
                hotNewsVO.setTitle((String) hotMap.get("title"));
                hotNewsVO.setHotURL((String) hotMap.get("short_link_v2"));
                hotNewsVO.setImageURL((String) hotMap.get("pic"));
                hotNewsVO.setHotDesc((String) hotMap.get("desc"));
                biliHotList.add(hotNewsVO);
            }
            redisUtils.redisSetInOneHour(REDIS_BILIBILI, biliHotList);
            redisUtils.redisSetInOneHour(REDIS_BILIBILI_DTATETIME, new DateTime());
        } catch (HttpException e) {
            log.error("bilibili hot news error:\t", e);
            throw new RuntimeException(e);
        }

        return biliHotList;
    }


    private String gainWBI() {
        String bodyStr = HttpUtil.createGet("https://api.bilibili.com/x/web-interface/nav")
                .header("Cookie", "SESSDATA=xxxxxx")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                .header("Referer", "https://www.bilibili.com/").execute().body();
        Object data = JSONUtil.parseObj(bodyStr).get("data");
        Object wbi_img = JSONUtil.parseObj(data).get("wbi_img");
        JSONArray jsonArray = JSONUtil.parseArray(wbi_img);
        Map<String, String> imgUrlMap = (Map<String, String>) jsonArray.get(0);
        Map<String, String> subUrlMap = (Map<String, String>) jsonArray.get(1);
        String imgUrl = imgUrlMap.get("img_url");
        String subUrl = subUrlMap.get("sub_url");
        String imgUrlSub = imgUrl.substring(imgUrl.lastIndexOf("/") + 1, imgUrl.lastIndexOf("."));
        String subUrlSub = subUrl.substring(subUrl.lastIndexOf("/") + 1, subUrl.lastIndexOf("."));
        Map<String, Object> map = new HashMap<>();
        map.put("foo", "114");
        map.put("bar", "514");
        map.put("baz", 1919810);
        return encWbi(map, imgUrlSub, subUrlSub);
    }


    private String encWbi(Map<String, Object> params, String imgKey, String subKey) {
        String mixinKey = getMixinKey(imgKey + subKey);
        long currTime = System.currentTimeMillis() / 1000;
        params.put("wts", currTime);
        String query = params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue().toString().replaceAll("[!'()*]", "");
                    return key + "=" + value;
                })
                .collect(Collectors.joining("&"));
        String wbiSign = DigestUtil.md5Hex(query + mixinKey);
        return query + "&w_rid=" + wbiSign;
    }

    private String getMixinKey(String orig) {
        StringBuilder sb = new StringBuilder();
        for (int index : MIXIN_KEY_ENC_TAB) {
            sb.append(orig.charAt(index));
        }
        return sb.substring(0, 32);
    }
}
