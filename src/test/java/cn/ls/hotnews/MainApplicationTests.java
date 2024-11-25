package cn.ls.hotnews;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.enums.AccountPlatformEnum;
import cn.ls.hotnews.model.vo.ThirdPartyAccountVO;
import cn.ls.hotnews.service.impl.TouTiaoEdgeDriverServiceImpl;
import cn.ls.hotnews.strategy.HotNewsStrategy;
import cn.ls.hotnews.utils.RedisUtils;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.exception.SparkException;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkTextUsage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static cn.ls.hotnews.constant.UserConstant.REDIS_THIRD_PARTY_ACCOUNT;

/**
 * 主类测试
 */
@SpringBootTest
class MainApplicationTests {
    private final int[] MIXIN_KEY_ENC_TAB = {
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49, 33, 9, 42, 19, 29, 28,
            14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40, 61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54,
            21, 56, 59, 6, 63, 57, 62, 11, 36, 20, 34, 44, 52
    };
    @Resource
    TouTiaoEdgeDriverServiceImpl touTiaoEdgeDriverServiceImpl;
    @Resource
    HotNewsStrategy hotNewsStrategy;
    @Resource
    RedisUtils redisUtils;

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

    /**
     * 抖音
     */
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

    /**
     * bilibil
     */
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
        Map<String, Object> map = new HashMap<>();
        map.put("foo", "114");
        map.put("bar", "514");
        map.put("baz", 1919810);
        String wbi = encWbi(map, imgUrlSub, subUrlSub);
        //System.out.println(wbi);
        String url = String.format("https://api.bilibili.com/x/web-interface/ranking/v2?tid=0&type=all&%s", wbi);
        String body = HttpUtil.createGet(url)
                .header("Referer", "https://www.bilibili.com/ranking/all")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36").execute().body();
        Object bodyData = JSONUtil.parseObj(body).get("data");
        JSONArray jsonArrayList = JSONUtil.parseObj(bodyData).getJSONArray("list");
        //System.out.println(jsonArrayList);
        Object o = jsonArrayList.get(0);
        Map<String, Object> hotMap = (Map<String, Object>) o;
        hotMap.get("bvid");
        hotMap.get("title");
        hotMap.get("short_link_v2");
        hotMap.get("pic");
        hotMap.get("desc");

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

    /**
     * ai test
     */
    @Test
    void c() {
        SparkClient sparkClient = new SparkClient();

        // 设置认证信息
        sparkClient.appid = "3ce98aad";
        sparkClient.apiKey = "999036d18c01d38fa64a2a7d9cd3ca7f";
        sparkClient.apiSecret = "YzhlYzhhMzE1MDNkMzlmMjBkYTBmMzE4";
        // 消息列表，可以在此列表添加历史对话记录
        String predefinedInformation = "你是一名资深内容重构专家、语言风格顾问、视频内容分析专家。\n" +
                "需要对用户提供的链接(链接有文章、视频等)进行深度重写,确保关键细节准确呈现的同时,调整语言风格以更贴近日常生活,减少华丽辞藻的运用;\n" +
                "设计一个详细流程,帮助用户将现有文章重构为高质量、原创性强、语言风格朴素且细节准确的内容;\n" +
                "仔细阅读原文,列出所有关键细节,确保在重写过程中一一准确呈现;\n" +
                "完整的文章草稿,包含吸引人的标题、引人入胜的引言、信息丰富的正文和深刻的结语;\n" +
                "必须遵守版权法,确保重写后的文章细节准确无误,语言风格符合用户期望的朴素要求;\n" +
                "接下来我会按照以下固定格式给你提供内容:\n" +
                "原始数据：\n" +
                "{网页链接的原始数据(链接有文章、视频等)}\n" +
                "请根据这部分内容，严格按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）同时不要使用这个符号 '】'\n" +
                "'【【【【【'\n" +
                "{文章标题}\n" +
                "{文章的具体大纲,包括各个章节或段落的主题和内容,最多不超过5个,不要生成任何多余的内容}\n" +
                "'【【【【【'";
        List<SparkMessage> messages = new ArrayList<>();
        //messages.add(SparkMessage.systemContent("请你扮演我的语文老师李老师，问我讲解问题问题，希望你可以保证知识准确，逻辑严谨。"));
        //messages.add(SparkMessage.userContent("鲁迅和周树人小时候打过架吗？"));
        messages.add(SparkMessage.systemContent(predefinedInformation));
        messages.add(SparkMessage.userContent("https://www.toutiao.com/trending/7438500511910739977/?category_name=topic_innerflow&event_type=hot_board&log_pb=%7B%22category_name%22%3A%22topic_innerflow%22%2C%22cluster_type%22%3A%222%22%2C%22enter_from%22%3A%22click_category%22%2C%22entrance_hotspot%22%3A%22outside%22%2C%22event_type%22%3A%22hot_board%22%2C%22hot_board_cluster_id%22%3A%227438500511910739977%22%2C%22hot_board_impr_id%22%3A%2220241120201309306B5377C02918D2B16E%22%2C%22jump_page%22%3A%22hot_board_page%22%2C%22location%22%3A%22news_hot_card%22%2C%22page_location%22%3A%22hot_board_page%22%2C%22rank%22%3A%221%22%2C%22source%22%3A%22trending_tab%22%2C%22style_id%22%3A%2240132%22%2C%22title%22%3A%22%E8%B6%855.5%E4%B8%87%E6%AF%9B%E5%88%A9%E4%BA%BA%E5%86%8D%E8%B7%B3%E6%88%98%E8%88%9E%E6%8A%97%E8%AE%AE%E6%96%B0%E6%B3%95%E6%A1%88%22%7D&rank=1&style_id=40132&topic_id=7438500511910739977"));
// 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
// 消息列表
                .messages(messages)
// 模型回答的tokens的最大长度,非必传，默认为2048。
// V1.5取值为[1,4096]
// V2.0取值为[1,8192]
// V3.0取值为[1,8192]
                .maxTokens(2048)
// 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
// 指定请求版本，默认使用最新3.0版本
                .apiVersion(SparkApiVersion.V4_0)
                .build();

        try {
            // 同步调用
            SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
            SparkTextUsage textUsage = chatResponse.getTextUsage();

            System.out.println("回答：\n" + chatResponse.getContent());
            System.out.println("\n提问tokens：" + textUsage.getPromptTokens()
                    + "，回答tokens：" + textUsage.getCompletionTokens()
                    + "，总消耗tokens：" + textUsage.getTotalTokens());
        } catch (SparkException e) {
            System.out.println("发生异常了：" + e.getMessage());
        }

        //String predefinedInformation = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
        //        "分析需求：\n" +
        //        "{数据分析的需求或者目标}\n" +
        //        "原始数据：\n" +
        //        "{csv格式的原始数据，用,作为分隔符}\n" +
        //        "请根据这两部分内容，严格按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）同时不要使用这个符号 '】'\n" +
        //        "'【【【【【'\n" +
        //        "{前端 Echarts V5 的 option 配置对象 JSON 代码, 不要生成任何多余的内容，比如注释和代码块标记}\n" +
        //        "'【【【【【'\n" +
        //        "{明确的数据分析结论、越详细越好，不要生成多余的注释} \n"
        //        + "下面是一个具体的例子的模板："
        //        + "'【【【【【'\n"
        //        + "JSON格式代码"
        //        + "'【【【【【'\n" +
        //        "结论：";
    }

    @Test
    void d() {
        ThirdPartyAccountVO thirdPartyAccountVO = new ThirdPartyAccountVO();
        thirdPartyAccountVO.setAccount("1815611467541508");
        thirdPartyAccountVO.setUserName("限量版逗比");
        thirdPartyAccountVO.setPlatForm("头条号");
        thirdPartyAccountVO.setIsDisabled(true);
        redisUtils.redisSetThirdPartyAccount(
                String.format(REDIS_THIRD_PARTY_ACCOUNT, AccountPlatformEnum.TOUTIAO.getPlatform(), "1858497270280724482")
                , thirdPartyAccountVO
        );
    }


    @Test
    void e() throws IOException {
        //"id": "7440751537036496411",
        //"biId": null,
        //"title": "王楚钦决赛夺冠用时不到30分钟",
        //"hotURL": "https://www.toutiao.com/trending/7440751537036496411/",
        //"imageURL": "https://p3-sign.toutiaoimg.com/tos-cn-i-qvj2lq49k0/5d3887fcd72442099843a9e89453d72b~tplv-tt-shrink:960:540.jpeg?_iz=30575&from=sign_default&lk3s=8d617dac&x-expires=1734998400&x-signature=PIvOprsg8gzVCc2e%2F7kpVdeSyoI%3D",
        //"hotDesc": "高热事件"

        //System.out.println(HttpUtil.createGet("https://www.toutiao.com/trending/7440751537036496411/"));
        System.setProperty("webdriver.edge.driver", "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedgedriver.exe");
        //System.setProperty("webdriver.chrome.whitelistedIps", "");
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("user-data-dir=E:\\user-test-data\\baijia");
        options.setHeadless(true);

        EdgeDriver driver = new EdgeDriver(options);
        //try {
        driver.navigate().to("https://www.toutiao.com/trending/7440751537036496411/");
        //driver.navigate().to("https://mp.toutiao.com/profile_v4/graphic/publish");
        String pageSource = driver.getPageSource();
        System.out.println(pageSource);
        // 使用Jsoup解析HTML内容
        Document doc = Jsoup.parse(pageSource);

        // 打印文档标题
        Elements elementsByClass = doc.getElementsByClass("feed-card-cover");
        for (Element byClass : elementsByClass) {
            for (Element element : byClass.getElementsByTag("a")) {
                System.out.println(element.attr("href"));
            }
        }
        driver.quit();

    }
    //<div class="article-content">
    // <h1>找回统治力，王楚钦30分钟横扫张本智和，他是如何做到的？</h1>
    // <div class="article-meta">
    //  <span>2024-11-24 15:47</span><span class="dot">·</span><span class="name"><a href="/c/user/token/MS4wLjABAAAAG6j_8o-TdLywYX48YpoVSomUJCCd_zW4vDzyeoIKnRs/?source=tuwen_detail" target="_blank" rel="noopener nofollow">小哥聊体育</a></span>
    // </div>
    // <article class="syl-article-base tt-article-content syl-page-article syl-device-pc">
    //  <p data-track="1">WTT福冈总决赛男单决赛，王楚钦4-0横扫张本智和，夺得冠军。全场仅仅耗时30分钟不到，王楚钦火力全开，打出了非常强势的攻防，统治了比赛！</p>
    //  <div class="pgc-img">
    //   <img src="https://p3-sign.toutiaoimg.com/tos-cn-i-axegupay5k/0b7c3dd80a7a4cdd90622212ae1d7d50~tplv-tt-origin-web:gif.jpeg?_iz=58558&amp;from=article.pc_detail&amp;lk3s=953192f4&amp;x-expires=1733067582&amp;x-signature=dZmafHhsSHk98ZEP2tD2DwX%2Bt30%3D" img_width="1600" img_height="973" image_type="1" mime_type="image/jpeg" web_uri="tos-cn-i-6w9my0ksvp/eb0f6acd9da944dd87144bf4c1902615" class="syl-page-img" style="height: auto;">
    //   <p class="pgc-img-caption"></p>
    //  </div>
    //  <p data-track="2" class="syl-page-br syl-page-br-hide"><br></p>
    //  <p data-track="4">张本智和是主场作战，半决赛又逆转了林诗栋，自信心爆棚！王楚钦身处职业生涯低谷，这次总决赛刚刚复苏。本以为是一场激烈的比赛，王楚钦依靠着自己的超强爆发，直接将比赛打成了一边倒！</p>
    //  <p data-track="5">首局，王楚钦10-0开局，11-2先胜。</p>
    //  <p data-track="6">第二局，双方比分紧咬，7-8关键分，王楚钦11-8再胜。</p>
    //  <p data-track="7">第三局，王楚钦4-1开局，11-7拿下。</p>
    //  <p data-track="8">第四局，王楚钦没有浪费机会，11-5收下了比赛的胜利。</p>
    //  <p data-track="9">一，王楚钦打得非常释放</p>
    //  <p data-track="10">新周期，王楚钦经历了低谷。连输外战，经历了职业生涯的黑暗时刻。本次总决赛，王楚钦成功复仇莫雷加德，获得了宝贵的自信，状态在慢慢复苏。决赛之战，王楚钦没有包袱，就去全力去冲击张本智和，力争打出最好的状态。</p>
    //  <p data-track="11">打得释放，打得坚决，王楚钦的单板质量也就打出来了。这场比赛，王楚钦反手的，发挥尤其突出，质量好，速度快，变化多，直接是打懵了张本智和。</p>
    //  <div class="pgc-img">
    //   <img src="https://p3-sign.toutiaoimg.com/tos-cn-i-6w9my0ksvp/d06e0965179f475bb546b959110d9eb6~tplv-tt-origin-web:gif.jpeg?_iz=58558&amp;from=article.pc_detail&amp;lk3s=953192f4&amp;x-expires=1733067582&amp;x-signature=Lz66csdlMGU0Zpovt614D2yli%2B0%3D" img_width="1600" img_height="999" image_type="1" mime_type="image/jpeg" web_uri="tos-cn-i-6w9my0ksvp/d06e0965179f475bb546b959110d9eb6" class="syl-page-img" style="height: auto;">
    //   <p class="pgc-img-caption"></p>
    //  </div>
    //  <p data-track="12" class="syl-page-br syl-page-br-hide"><br></p>
    //  <p data-track="14">二，王楚钦备战更充分，综合实力更强</p>
    //  <p data-track="15">王楚钦这场的状态非常好，综合实力和场上发挥明显强于张本智和，赢球基本是水到渠成的。王楚钦前三板抢得非常凶，上手主动。进入到相持阶段，王楚钦的单板质量也更高，稳定性也更好。</p>
    //  <p data-track="16">张本智和的优势是速度和变化，这场比赛被限制得很难受，王楚钦在备战上做得非常充分，牢牢地控制着比赛的节奏。反观张本智和，全场没有摸透王楚钦的发球，自身的节奏出不来，心气慢慢也被打没了。</p>
    //  <p data-track="17">三，关键分，王楚钦更稳</p>
    //  <p data-track="18">开场，王楚钦火力全开，直接打了张本智和一个10-0。这让王楚钦打出了自信，压制住了张本智和的搏杀。</p>
    //  <p data-track="19">如果说首局是因为张本智和进入状态较慢的话，那第二局是本场比赛的关键局，决定了比赛的走势。王楚钦依靠着这一局的发挥，彻底掌控住了比赛的节奏。张本智和一度取得了8-7的领先，王楚钦依靠着出色的发球，连拿4分，完成了逆转。大比分2-0领先之后，场上的形势也就清晰了，王楚钦越打越自信，越战越勇。张本智和想搏杀，始终没有找到节奏，后面失误多，节奏全无！</p>
    //  <div class="pgc-img">
    //   <img src="https://p3-sign.toutiaoimg.com/tos-cn-i-6w9my0ksvp/19dc25b627c74b4085354759ee202374~tplv-tt-origin-web:gif.jpeg?_iz=58558&amp;from=article.pc_detail&amp;lk3s=953192f4&amp;x-expires=1733067582&amp;x-signature=6%2F2YU54spNFI0ocd%2Bla11yfFj34%3D" img_width="1600" img_height="974" image_type="1" mime_type="image/jpeg" web_uri="tos-cn-i-6w9my0ksvp/19dc25b627c74b4085354759ee202374" class="syl-page-img" style="height: auto;">
    //   <p class="pgc-img-caption"></p>
    //  </div>
    //  <p data-track="20" class="syl-page-br syl-page-br-hide"><br></p>
    //  <p data-track="22">刚刚经历了职业生涯的低谷，王楚钦在关键节点拿到了总决赛的男单冠军，复仇了莫雷加德，击败了强敌张本智和。不仅稳住了世界第一，也收获了自信，有助于接下来的进一步蜕变！</p>
    //  <p data-track="23">比赛打得非常精彩，祝福王楚钦！</p>
    // </article>
    //</div>

    @Test
    void


    f() {
        System.setProperty("webdriver.edge.driver", "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedgedriver.exe");
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("user-data-dir=E:\\user-test-data\\baijia");
        options.addArguments("--headless");

        EdgeDriver driver = new EdgeDriver(options);
        //try {
        driver.navigate().to("https://www.toutiao.com/article/7440748766673699343/");
        String pageSource = driver.getPageSource();
        //System.out.println(pageSource);
        // 使用Jsoup解析HTML内容
        Document doc = Jsoup.parse(pageSource);

        // 打印文档标题
        Elements elementsByClass = doc.getElementsByClass("article-content");
        String text = elementsByClass.text();
        System.err.println(text);
        String title = text.substring(0, text.indexOf(" "));
        System.out.println(title);

        List<String> collect = Arrays.stream(text.substring(text.indexOf(" ") + 1).split(" ")).collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < collect.size(); i++) {
            if (i > 1) {
                stringBuilder.append(collect.get(i)).append("\n");
            }
        }
        System.out.println(stringBuilder);
        driver.quit();
    }

    @Test
    void h() {
        int a = 0;
        for (int i = 0; i < 3; i++) {
            System.out.println(a += 1);
        }
    }
}
