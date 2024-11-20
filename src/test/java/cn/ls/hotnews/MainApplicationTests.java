package cn.ls.hotnews;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.service.impl.TouTiaoEdgeDriverServiceImpl;
import cn.ls.hotnews.strategy.HotNewsStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 主类测试
 */
@SpringBootTest
class MainApplicationTests {
    @Resource
    TouTiaoEdgeDriverServiceImpl touTiaoEdgeDriverServiceImpl;

    @Test
    void contextLoads() {
        String s = HttpUtil.get("https://www.toutiao.com/hot-event/hot-board/?origin=toutiao_pc");
        //Object data = JSONUtil.parseObj(s).get("data");
        //JSONObject entries = JSONUtil.parseObj(data);
        JSONArray data = JSONUtil.parseObj(s).getJSONArray("data");
        //System.out.println(data);
        //for (Object datum : data) {
        Map<String, Object> map = (Map<String, Object>) data.get(0);
        Map<String, Object> imageMap = (Map<String, Object>) map.get("Image");
        //ClusterId
        //Title
        //Label
        //Url
        //HotValue
        //Image
        //LabelDesc
        map.get("ClusterId");
        map.get("Title");
        map.get("Label");
        map.get("Url");
        map.get("HotValue");
        map.get("LabelDesc");
        imageMap.get("url");

        //}


    }


    @Resource
    HotNewsStrategy hotNewsStrategy;

    private final int[] MIXIN_KEY_ENC_TAB = {
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49, 33, 9, 42, 19, 29, 28,
            14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40, 61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54,
            21, 56, 59, 6, 63, 57, 62, 11, 36, 20, 34, 44, 52
    };

    @Test
    void a() {
        String cookisUrl = "https://www.douyin.com/passport/general/login_guiding_strategy/?aid=6383";
        HttpResponse response = HttpUtil.createGet(cookisUrl).execute();

        if (response.getStatus() == 200) {
            List<String> setCookieHeaderList = response.headerList("Set-Cookie");
            if (!setCookieHeaderList.isEmpty()) {
                String body = HttpUtil.createGet("https://www.douyin.com/aweme/v1/web/hot/search/list/?device_platform=webapp&aid=6383&channel=channel_pc_web&detail_list=1")
                        .header("Cookie", "passport_csrf_token=" + setCookieHeaderList.get(2)).execute().body();
                Object data = JSONUtil.parseObj(body).get("data");
                JSONArray wordList = JSONUtil.parseObj(data).getJSONArray("word_list");
                Map<String, Object> map = (Map<String, Object>) wordList.get(0);
                System.out.println(map.get("sentence_id"));
                System.out.println(map.get("word"));
                JSONArray imageUrlList = JSONUtil.parseObj(map.get("word_cover")).getJSONArray("url_list");
                System.out.println(imageUrlList.get(0));
                //for (Object obj : wordList) {
                //    Map<String,Object> map =(Map<String,Object>) obj;
                //}

            }
        } else {
            System.err.println("GET 请求失败，响应码: " + response.getStatus());
        }
        System.out.println(hotNewsStrategy.getHotNewsByPlatform("douyin").hotNewsList());
        //System.out.println(hotNewsStrategy.getHotNewsByPlatform("toutiao").hotNewsList());
    }

    @Test
    void b() {
        //Map<String,List<String>> map =new HashMap<>();
        // 获取最新的 img_key 和 sub_key
        String bodyStr = HttpUtil.createGet("https://api.bilibili.com/x/web-interface/nav")
                .header("Cookie", "SESSDATA=xxxxxx")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                .header("Referer", "https://www.bilibili.com/").execute().body();
        Object data = JSONUtil.parseObj(bodyStr).get("data");
        Object wbi_img = JSONUtil.parseObj(data).get("wbi_img");
        JSONArray jsonArray = JSONUtil.parseArray(wbi_img);
        //System.out.println(jsonArray);
        Map<String, String> imgUrlMap = (Map<String, String>) jsonArray.get(0);
        Map<String, String> subUrlMap = (Map<String, String>) jsonArray.get(1);
        String imgUrl = imgUrlMap.get("img_url");
        String subUrl = subUrlMap.get("sub_url");
        String imgUrlSub = imgUrl.substring(imgUrl.lastIndexOf("/") + 1, imgUrl.lastIndexOf("."));
        String subUrlSub = subUrl.substring(subUrl.lastIndexOf("/") + 1, subUrl.lastIndexOf("."));
        Map<String,Object> map = new HashMap<>();
        map.put("foo","114");
        map.put("bar","514");
        map.put("baz",1919810);
        String wbi = encWbi(map, imgUrlSub, subUrlSub);
        //System.out.println(wbi);
        String url = String.format("https://api.bilibili.com/x/web-interface/ranking/v2?tid=0&type=all&%s", wbi);
        String body = HttpUtil.createGet(url)
                .header("Referer", "https://www.bilibili.com/ranking/all")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36").execute().body();
        System.out.println(body);
    }

    public String encWbi(Map<String, Object> params, String imgKey, String subKey) {
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

    public String getMixinKey(String orig) {
        StringBuilder sb = new StringBuilder();
        for (int index : MIXIN_KEY_ENC_TAB) {
            sb.append(orig.charAt(index));
        }
        return sb.toString().substring(0, 32);
    }
}
