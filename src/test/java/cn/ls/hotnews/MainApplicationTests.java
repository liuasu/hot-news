package cn.ls.hotnews;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.service.impl.TouTiaoEdgeDriverServiceImpl;
import cn.ls.hotnews.strategy.HotNewsStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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

    @Test
    void a() {
        //String cookisUrl = "https://www.douyin.com/passport/general/login_guiding_strategy/?aid=6383";
        //HttpResponse response = HttpUtil.createGet(cookisUrl).execute();
        //
        //if (response.getStatus() == 200) {
        //    List<String> setCookieHeaderList = response.headerList("Set-Cookie");
        //    if (!setCookieHeaderList.isEmpty()) {
        //        String body = HttpUtil.createGet("https://www.douyin.com/aweme/v1/web/hot/search/list/?device_platform=webapp&aid=6383&channel=channel_pc_web&detail_list=1")
        //                .header("Cookie", "passport_csrf_token=" + setCookieHeaderList.get(2)).execute().body();
        //        Object data = JSONUtil.parseObj(body).get("data");
        //        JSONArray wordList = JSONUtil.parseObj(data).getJSONArray("word_list");
        //        Map<String, Object> map = (Map<String, Object>) wordList.get(0);
        //        System.out.println(map.get("sentence_id"));
        //        System.out.println(map.get("word"));
        //        JSONArray imageUrlList = JSONUtil.parseObj(map.get("word_cover")).getJSONArray("url_list");
        //        System.out.println(imageUrlList.get(0));
        //        //for (Object obj : wordList) {
        //        //    Map<String,Object> map =(Map<String,Object>) obj;
        //        //}
        //
        //    }
        //} else {
        //    System.err.println("GET 请求失败，响应码: " + response.getStatus());
        //}
        System.out.println(hotNewsStrategy.getHotNewsByPlatform("douyin").hotNewsList());
        //System.out.println(hotNewsStrategy.getHotNewsByPlatform("toutiao").hotNewsList());
    }


}
