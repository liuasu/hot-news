package cn.ls.hotnews;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.model.vo.ThirdPartyAccountVO;
import cn.ls.hotnews.service.impl.chromedriver.TouTiaoChromeDriverServiceImpl;
import cn.ls.hotnews.strategy.HotNewsStrategy;
import cn.ls.hotnews.utils.ChromeDriverUtils;
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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.ls.hotnews.constant.CommonConstant.REDIS_THIRDPARTY_ACCOUNT;
import static cn.ls.hotnews.constant.CommonConstant.TOUTIAO;

/**
 * 主类测试
 */
@SpringBootTest
class MainApplicationTests {
    private static final String[] ALGORITHMS = {
            "AES/CBC/PKCS5Padding",
            "AES/ECB/PKCS5Padding",
            "DES/CBC/PKCS5Padding",
            "DES/ECB/PKCS5Padding"
    };
    private final int[] MIXIN_KEY_ENC_TAB = {
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49, 33, 9, 42, 19, 29, 28,
            14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40, 61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54,
            21, 56, 59, 6, 63, 57, 62, 11, 36, 20, 34, 44, 52
    };
    @Resource
    HotNewsStrategy hotNewsStrategy;
    @Resource
    RedisUtils redisUtils;

    //public static void main(String[] args) {
    //    String text = "上海浦东新区书记朱芝松被查 14:27·经济观察报 经济观察网 记者 李微敖 2024年11月27日10时，中央纪委国家监委官方网站发布消息：上海市委常委、浦东新区区委书记朱芝松涉嫌严重违纪违法，目前正接受中央纪委国家监委纪律审查和监察调查。 2024年11月26日晚间，关于朱芝松被带走调查的消息已经在上海政界广为流布。 彼时，有当地政界人士对经济观察网记者称，朱芝松案发与2023年以来航天军品系统反腐事项相关，朱芝松此前曾长时间在这一领域工作。除朱芝松之外，亦有数位与他关系密切者一并“失联”。这包括其家人、身边工作人员及浦东新区政府某部门的一位负责人。此前一日，即2024年11月25日，朱芝松还在浦东新区大团镇，调研美丽乡村建设进展情况。 公开简历显示：朱芝松，1969年2月出生，籍贯在江苏赣榆（今连云港市赣榆区）。1989年，朱芝松从哈尔滨工业大学机械制造专业毕业后，进入上海航天局，长期从事军品工作。 上海航天局，又称上海航天技术研究院、中国航天科技集团公司第八研究院，曾名上海市第二机电工业局、航天工业部上海航天局，创建于1961年，一度实行以航天工业部为主的部、市双重领导。其主要业务领域覆盖防务装备、运载火箭、应用卫星、空间科学、航天技术应用产业和航天服务业，是中国航天科技集团有限公司三大总体院之一，也是中国航天唯一的综合性总体院。 朱芝松先后在上海航天局第八OO研究所（又名：上海航天精密机械研究所）担任技术员、研究室副主任、研究室主任、副所长、总体所所长等职务。2000年6月，朱芝松任上海航天局局长助理，主管全局武器型号的研制生产工作；2002年2月，出任上海航天局副局长；2008年11月，升任上海航天局局长。 2014年5月，朱芝松离开航天军品系统，转任上海市委宣传部副部长；一年多之后，即2015年12月，出任上海市闵行区委副书记、代区长；2016年1月，任闵行区区长。2017年6月，任闵行区委书记。 2019年8月，朱芝松转任上海市人民政府副秘书长，中国（上海）自由贸易试验区临港新片区管理委员会常务副主任、洋山保税港区管理委员会主任（兼）。 2021年7月，时任上海市委常委、浦东新区区委书记翁祖亮，调任中国五矿集团有限公司董事长。朱芝松随即接任了翁祖亮的职位，出任上海市委常委，浦东新区区委书记，官至副省部级。";
    //    String formattedText = formatText(text);
    //    System.out.println(formattedText);
    //}
    //
    //public static String formatText(String text) {
    //    // Split the text into paragraphs based on specific markers (e.g., dates, titles)
    //    String[] paragraphs = text.split("·|\\d{4}年\\d{1,2}月\\d{1,2}日");
    //
    //    // Trim and format each paragraph
    //    StringBuilder formattedText = new StringBuilder();
    //    for (String paragraph : paragraphs) {
    //        String trimmedParagraph = paragraph.trim();
    //        if (!trimmedParagraph.isEmpty()) {
    //            formattedText.append("\n\n").append(trimmedParagraph);
    //        }
    //    }
    //
    //    return formattedText.toString();
    //}
    @Resource
    TouTiaoChromeDriverServiceImpl touTiaoChromeDriverServiceImpl;

    public static void main(String[] args) {
        String cookieStr = "tt_webid=7441204681650603559;\n" +
                " _ga=GA1.1.1263119088.1732540499;\n" +
                " _ga_QEHZPBE5HH=GS1.1.1732759839.4.1.1732762748.0.0.0;\n" +
                " gfkadpd=1231,25897; passport_csrf_token=b96841df8a235815bd6695ec85618f00; passport_csrf_token_default=b96841df8a235815bd6695ec85618f00; ttcid=a6c50491e280483d8527f4b819f99ce111; s_v_web_id=verify_m41brdrq_5pqnn7Js_VUxn_43NK_BkJv_Ur3sMwOV8Qa1; ttwid=1%7CzxYeyy3fqLGhMxXAfxDS_vO6ztavvPQL9hBWR2g-MXc%7C1732798985%7C26962462e4cf7c08461361e2cbb30ad2e57ea4af4984c1cc70d30e56b0d655fe; n_mh=KQ-p6faDP5EkFlkMjaqicI6RZzvZ01nR1dSjk560S14; sso_uid_tt=50d1cca11b81a0eb5ebb1601792eec45; sso_uid_tt_ss=50d1cca11b81a0eb5ebb1601792eec45; toutiao_sso_user=f80384e01c5b73cf86d34369dcb40a60; toutiao_sso_user_ss=f80384e01c5b73cf86d34369dcb40a60; sid_ucp_sso_v1=1.0.0-KDcwNWM1MjMzNWM4MzhjMmMwYTRlM2E4N2ViM2RkODk3ODQ5N2Q0M2YKHwiUqLG108ySBRCN1KG6BhjPCSAMMNnUzaoGOAZA9AcaAmhsIiBmODAzODRlMDFjNWI3M2NmODZkMzQzNjlkY2I0MGE2MA; ssid_ucp_sso_v1=1.0.0-KDcwNWM1MjMzNWM4MzhjMmMwYTRlM2E4N2ViM2RkODk3ODQ5N2Q0M2YKHwiUqLG108ySBRCN1KG6BhjPCSAMMNnUzaoGOAZA9AcaAmhsIiBmODAzODRlMDFjNWI3M2NmODZkMzQzNjlkY2I0MGE2MA; passport_auth_status=d88d6318a46018ac55c699e534b58820%2C; passport_auth_status_ss=d88d6318a46018ac55c699e534b58820%2C; sid_guard=429e738bebe4cb06bbda30e1171b2e6c%7C1732798991%7C5184000%7CMon%2C+27-Jan-2025+13%3A03%3A11+GMT; uid_tt=5c604c43c64e860b0e8034fd4602b125; uid_tt_ss=5c604c43c64e860b0e8034fd4602b125; sid_tt=429e738bebe4cb06bbda30e1171b2e6c; sessionid=429e738bebe4cb06bbda30e1171b2e6c; sessionid_ss=429e738bebe4cb06bbda30e1171b2e6c; is_staff_user=false; sid_ucp_v1=1.0.0-KGRhOGYxOTcwYTI0OGVmNDBiZDc0MWVjM2JlMTM4MGIxZjU3ZjdjN2QKGQiUqLG108ySBRCP1KG6BhjPCSAMOAZA9AcaAmxmIiA0MjllNzM4YmViZTRjYjA2YmJkYTMwZTExNzFiMmU2Yw; ssid_ucp_v1=1.0.0-KGRhOGYxOTcwYTI0OGVmNDBiZDc0MWVjM2JlMTM4MGIxZjU3ZjdjN2QKGQiUqLG108ySBRCP1KG6BhjPCSAMOAZA9AcaAmxmIiA0MjllNzM4YmViZTRjYjA2YmJkYTMwZTExNzFiMmU2Yw; store-region=cn-hn; store-region-src=uid; odin_tt=aecfa40349d659bc4944997e532a83ba644ca513d649571feea8fb863a9e14b999ffc959866772c71434b20bc4a26676; csrf_session_id=ff5018b439bcb8592ab483ec31d4566f; tt_scid=12bjUVS5WeA1ItNdceDF6nOUfeCn.cTg-YDyUK-B3TlctVkZ1Wl3yg6RWLyxsLQJf0eb";
        String str = "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";

        //try {
        //    // 尝试Base64解码
        //    byte[] decodedBytes = Base64.getDecoder().decode(str);
        //    String decodedString = new String(decodedBytes);
        //    System.out.println("Base64解码结果：");
        //    System.out.println(decodedString);
        //} catch (Exception e) {
        //    System.out.println("Base64解码失败：" + e.getMessage());
        //}

        byte[] data = Base64.getDecoder().decode(str);

        for (String algorithm : ALGORITHMS) {
            try {
                // 使用一些常见的密钥尝试解密
                String[] commonKeys = {"1234567890123456", "0000000000000000", "abcdefghijklmnop"};
                for (String key : commonKeys) {
                    try {
                        byte[] decrypted = decrypt(data, key, algorithm);
                        String result = new String(decrypted);
                        if (isReadable(result)) {
                            System.out.println("可能的解密结果（算法=" + algorithm + ", 密钥=" + key + "）：");
                            System.out.println(result);
                        }
                    } catch (Exception ignored) {
                        // 忽略失败的尝试
                    }
                }
            } catch (Exception ignored) {
                // 忽略不支持的算法
            }
        }
    }

    private static byte[] decrypt(byte[] data, String key, String algorithm) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm.split("/")[0]);
        Cipher cipher = Cipher.getInstance(algorithm);

        if (algorithm.contains("CBC")) {
            // CBC模式需要IV
            byte[] iv = new byte[16];
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        }

        return cipher.doFinal(data);
    }

    private static boolean isReadable(String text) {
        return text.matches("^[\\x20-\\x7E\\n\\r\\t]+$");
    }

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
        //ThirdPartyAccountVO thirdPartyAccountVO = new ThirdPartyAccountVO();
        //thirdPartyAccountVO.setAccount("1815611467541508");
        //thirdPartyAccountVO.setUserName("限量版逗比");
        //thirdPartyAccountVO.setPlatForm("头条号");
        //thirdPartyAccountVO.setIsDisabled(true);
        Map<String, List<ThirdPartyAccountVO>> map = new HashMap<>();
        List<ThirdPartyAccountVO> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            ThirdPartyAccountVO thirdPartyAccountVO = new ThirdPartyAccountVO();
            thirdPartyAccountVO.setAccount("181561146754150" + i);
            thirdPartyAccountVO.setUserName("限量版逗比" + i);
            thirdPartyAccountVO.setPlatForm("头条号");
            thirdPartyAccountVO.setIsDisabled(true);
            list.add(thirdPartyAccountVO);
        }
        map.put(TOUTIAO, list);
        //redisUtils.redisSetInMap(
        //        String.format(REDIS_THIRD_PARTY_ACCOUNT, AccountPlatformEnum.TOUTIAO.getPlatform(), "1858497270280724482")
        //        , thirdPartyAccountVO
        //);
        //REDIS_THIRDPARTY_ACCOUNT
        String key = String.format(REDIS_THIRDPARTY_ACCOUNT, "1858497270280724482");
        redisUtils.redisSetInMap(key, map);
        Map<String, List<ThirdPartyAccountVO>> objMap = redisUtils.redisGetThirdPartyAccountByMap(key);
        list = new ArrayList<>();
        ThirdPartyAccountVO thirdPartyAccountVO = new ThirdPartyAccountVO();
        thirdPartyAccountVO.setAccount("1815611467541599");
        thirdPartyAccountVO.setUserName("六啊朔");
        thirdPartyAccountVO.setPlatForm("百家号");
        thirdPartyAccountVO.setIsDisabled(true);
        list.add(thirdPartyAccountVO);
        objMap.put(TOUTIAO, list);
        redisUtils.redisSetInMap(key, objMap);
        System.out.println(redisUtils.redisGetThirdPartyAccountByMap(key).get(TOUTIAO));
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
        System.setProperty("webdriver.edge.driver", "C:\\Program Files (x86)\\Microsoft\\Chrome\\Application\\msedgedriver.exe");
        //System.setProperty("webdriver.chrome.whitelistedIps", "");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("user-data-dir=E:\\user-test-data\\baijia");
        options.setHeadless(true);

        ChromeDriver driver = new ChromeDriver(options);
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

    @Test
    void f() {
        System.setProperty("webdriver.edge.driver", "C:\\Program Files (x86)\\Microsoft\\Chrome\\Application\\msedgedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("user-data-dir=E:\\user-test-data\\baijia");
        options.addArguments("--headless");

        ChromeDriver driver = new ChromeDriver(options);
        //try {
        driver.navigate().to("https://www.toutiao.com/article/7441120424341127680/");
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

        List<String> collect = Arrays.stream(text.substring(text.indexOf(" ") + 1).split(" ")).toList();
        StringBuilder stringBuilder = new StringBuilder();
        // 定义正则表达式
        String regex = "(版权归原作者所有|侵犯您的合法权益|邮箱|联系|未经授权|不得转载)";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 创建匹配器对象
        Matcher matcher = pattern.matcher(text);
        for (int i = 0; i < collect.size(); i++) {
            if (i > 1 && !matcher.find()) {
                stringBuilder.append(collect.get(i)).append("\n");
            }
        }
        System.out.println(stringBuilder);
        driver.quit();
    }

    @Test
    void h() {
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", "s_v_web_id=verify_m42rd19n_ydqNGz6x_rwGu_4lrR_80f6_FE8S8nd4g7hK; ttwid=1%7C3qoxEnW7wyL0fgpcU8D60v9C4RrcHSe_wXGCdne-dgM%7C1732885630%7C6d73e2a3319757b8bb6813a92872e915532b03a7541740d9089a2bbba45f998f; passport_csrf_token=92853b7b669ff13e17d5ada2e962880a; passport_csrf_token_default=92853b7b669ff13e17d5ada2e962880a; n_mh=KQ-p6faDP5EkFlkMjaqicI6RZzvZ01nR1dSjk560S14; sso_uid_tt=6cb4741ff3fc76c0270451689a2ef64d; sso_uid_tt_ss=6cb4741ff3fc76c0270451689a2ef64d; toutiao_sso_user=d2bbfca45575bdeb734ae95e4f0f7965; toutiao_sso_user_ss=d2bbfca45575bdeb734ae95e4f0f7965; sid_ucp_sso_v1=1.0.0-KGVhNDU2MTQ2YmViNTg1YWEwNWNjOWQyZDQ4ZGRmMGUzYjRlZTNiYmUKHwiUqLG108ySBRCC-aa6BhjPCSAMMNnUzaoGOAZA9AcaAmhsIiBkMmJiZmNhNDU1NzViZGViNzM0YWU5NWU0ZjBmNzk2NQ; ssid_ucp_sso_v1=1.0.0-KGVhNDU2MTQ2YmViNTg1YWEwNWNjOWQyZDQ4ZGRmMGUzYjRlZTNiYmUKHwiUqLG108ySBRCC-aa6BhjPCSAMMNnUzaoGOAZA9AcaAmhsIiBkMmJiZmNhNDU1NzViZGViNzM0YWU5NWU0ZjBmNzk2NQ; passport_auth_status=ed161a9070b11174fe5be033d9af3f10%2C; passport_auth_status_ss=ed161a9070b11174fe5be033d9af3f10%2C; sid_guard=e851b672b667a5725d9d18c2337f2a8b%7C1732885636%7C5184000%7CTue%2C+28-Jan-2025+13%3A07%3A16+GMT; uid_tt=d4d1c7915aa105f712b11c9de855a942; uid_tt_ss=d4d1c7915aa105f712b11c9de855a942; sid_tt=e851b672b667a5725d9d18c2337f2a8b; sessionid=e851b672b667a5725d9d18c2337f2a8b; sessionid_ss=e851b672b667a5725d9d18c2337f2a8b; is_staff_user=false; sid_ucp_v1=1.0.0-KDNhZWYzN2Y2ZWNkYmQ3M2QyMjE5ZGIzMTVjYjliYjNjMzJjYzhlNDIKGQiUqLG108ySBRCE-aa6BhjPCSAMOAZA9AcaAmxmIiBlODUxYjY3MmI2NjdhNTcyNWQ5ZDE4YzIzMzdmMmE4Yg; ssid_ucp_v1=1.0.0-KDNhZWYzN2Y2ZWNkYmQ3M2QyMjE5ZGIzMTVjYjliYjNjMzJjYzhlNDIKGQiUqLG108ySBRCE-aa6BhjPCSAMOAZA9AcaAmxmIiBlODUxYjY3MmI2NjdhNTcyNWQ5ZDE4YzIzMzdmMmE4Yg; store-region=cn-hn; store-region-src=uid; gfkadpd=1231,25897; ttcid=49ce5007e2a84854879e27058b018ea730; odin_tt=886db5db9dce89fbbbbaa49cf585ba86bd7fbab099d48a2c981b60b3781c237ce05cc0efc3bc285912c0276abc49d94a; tt_scid=Mt5agi-79mSoBRiXXeaYbe38251TRG1pBaL2QlxEOv0nNapTX6BAmzi3u2gIwAlc5385; csrf_session_id=2620053670038787dc8a0fbd1c44a44e");
        HttpResponse cookie = HttpUtil.createGet("https://mp.toutiao.com/mp/agw/creator_center/user_info?app_id=1231")
                .cookie("s_v_web_id=verify_m42rd19n_ydqNGz6x_rwGu_4lrR_80f6_FE8S8nd4g7hK; " +
                        "ttwid=1%7C3qoxEnW7wyL0fgpcU8D60v9C4RrcHSe_wXGCdne-dgM%7C1732885630%7C6d73e2a3319757b8bb6813a92872e915532b03a7541740d9089a2bbba45f998f; " +
                        "passport_csrf_token=92853b7b669ff13e17d5ada2e962880a; " +
                        "passport_csrf_token_default=92853b7b669ff13e17d5ada2e962880a; " +
                        "n_mh=KQ-p6faDP5EkFlkMjaqicI6RZzvZ01nR1dSjk560S14; " +
                        "sso_uid_tt=6cb4741ff3fc76c0270451689a2ef64d; " +
                        "sso_uid_tt_ss=6cb4741ff3fc76c0270451689a2ef64d; " +
                        "toutiao_sso_user=d2bbfca45575bdeb734ae95e4f0f7965; " +
                        "toutiao_sso_user_ss=d2bbfca45575bdeb734ae95e4f0f7965; " +
                        "sid_ucp_sso_v1=1.0.0-KGVhNDU2MTQ2YmViNTg1YWEwNWNjOWQyZDQ4ZGRmMGUzYjRlZTNiYmUKHwiUqLG108ySBRCC-aa6BhjPCSAMMNnUzaoGOAZA9AcaAmhsIiBkMmJiZmNhNDU1NzViZGViNzM0YWU5NWU0ZjBmNzk2NQ; " +
                        "ssid_ucp_sso_v1=1.0.0-KGVhNDU2MTQ2YmViNTg1YWEwNWNjOWQyZDQ4ZGRmMGUzYjRlZTNiYmUKHwiUqLG108ySBRCC-aa6BhjPCSAMMNnUzaoGOAZA9AcaAmhsIiBkMmJiZmNhNDU1NzViZGViNzM0YWU5NWU0ZjBmNzk2NQ; " +
                        "passport_auth_status=ed161a9070b11174fe5be033d9af3f10%2C; passport_auth_status_ss=ed161a9070b11174fe5be033d9af3f10%2C; sid_guard=e851b672b667a5725d9d18c2337f2a8b%7C1732885636%7C5184000%7CTue%2C+28-Jan-2025+13%3A07%3A16+GMT; " +
                        "uid_tt=d4d1c7915aa105f712b11c9de855a942; " +
                        "uid_tt_ss=d4d1c7915aa105f712b11c9de855a942; " +
                        "sid_tt=e851b672b667a5725d9d18c2337f2a8b; " +
                        "sessionid=e851b672b667a5725d9d18c2337f2a8b; " +
                        "sessionid_ss=e851b672b667a5725d9d18c2337f2a8b; " +
                        "is_staff_user=false; " +
                        "sid_ucp_v1=1.0.0-KDNhZWYzN2Y2ZWNkYmQ3M2QyMjE5ZGIzMTVjYjliYjNjMzJjYzhlNDIKGQiUqLG108ySBRCE-aa6BhjPCSAMOAZA9AcaAmxmIiBlODUxYjY3MmI2NjdhNTcyNWQ5ZDE4YzIzMzdmMmE4Yg; " +
                        "ssid_ucp_v1=1.0.0-KDNhZWYzN2Y2ZWNkYmQ3M2QyMjE5ZGIzMTVjYjliYjNjMzJjYzhlNDIKGQiUqLG108ySBRCE-aa6BhjPCSAMOAZA9AcaAmxmIiBlODUxYjY3MmI2NjdhNTcyNWQ5ZDE4YzIzMzdmMmE4Yg; " +
                        "store-region=cn-hn; " +
                        "store-region-src=uid; " +
                        "gfkadpd=1231,25897; " +
                        "ttcid=49ce5007e2a84854879e27058b018ea730; " +
                        "odin_tt=886db5db9dce89fbbbbaa49cf585ba86bd7fbab099d48a2c981b60b3781c237ce05cc0efc3bc285912c0276abc49d94a; " +
                        "tt_scid=Mt5agi-79mSoBRiXXeaYbe38251TRG1pBaL2QlxEOv0nNapTX6BAmzi3u2gIwAlc5385; " +
                        "csrf_session_id=2620053670038787dc8a0fbd1c44a44e").execute();
        System.out.println(cookie);
    }

    //{
    //  "gfkadpd":"1231,25897",
    //  "ssid_ucp_v1":"1.0.0-KGMxNWI2MjMyZDNlMjgyNWQ3MTljZjZkMWFjNTlhN2Q1NzJjMGQ3NWQKGQjZtND6_82BARCTh6e6BhjPCSAMOAZA9AcaAmxmIiA0NTMxMDZiMDE2YjJiZWQ4M2UwOTQ0ODMwODkzNTdkNA",
    //  "toutiao_sso_user_ss":"3678b1a5d2c7921f4f432369aabd5761",
    //  "is_staff_user":"false",
    //  "sid_ucp_v1":"1.0.0-KGMxNWI2MjMyZDNlMjgyNWQ3MTljZjZkMWFjNTlhN2Q1NzJjMGQ3NWQKGQjZtND6_82BARCTh6e6BhjPCSAMOAZA9AcaAmxmIiA0NTMxMDZiMDE2YjJiZWQ4M2UwOTQ0ODMwODkzNTdkNA",
    //  "sessionid":"453106b016b2bed83e094483089357d4",
    //  "ttwid":"1%7C3qoxEnW7wyL0fgpcU8D60v9C4RrcHSe_wXGCdne-dgM%7C1732887423%7C3ba499b7d220d0305caf57f56c61d98b56fc73cffb98ac2662cde37e09819c64",
    //  "uid_tt":"7d82dd191e8ce2f5b967386f5c219eb0",
    //  "gfgarrcache_650f993b":"1",
    //  "sso_uid_tt":"518e748eacf52b96f8f25ac15dcbf7a2",
    //  "toutiao_sso_user":"3678b1a5d2c7921f4f432369aabd5761",
    //  "uid_tt_ss":"7d82dd191e8ce2f5b967386f5c219eb0",
    //  "passport_auth_status":"ba2ab372fe65a793d31018bb91dabc50%2Ced161a9070b11174fe5be033d9af3f10",
    //  "odin_tt":"44fc6457be9fc2d79554ae21e7c2de780268769783c20cd4accc5577bb6facaa2874bc16c0367cdfce846177cf22194fe16bd818aaccf02a2b44a44311028f2e",
    //  "passport_csrf_token_default":"92853b7b669ff13e17d5ada2e962880a",
    //  "sid_ucp_sso_v1":"1.0.0-KDg2MTQyZjkyNDM5YWM5OWEwMGNiODBkYmVmMmM3ZGU2MDA2ZGQ3ZjIKHwjZtND6_82BARCRh6e6BhjPCSAMMKvq_7cGOAZA9AcaAmhsIiAzNjc4YjFhNWQyYzc5MjFmNGY0MzIzNjlhYWJkNTc2MQ",
    //  "ssid_ucp_sso_v1":"1.0.0-KDg2MTQyZjkyNDM5YWM5OWEwMGNiODBkYmVmMmM3ZGU2MDA2ZGQ3ZjIKHwjZtND6_82BARCRh6e6BhjPCSAMMKvq_7cGOAZA9AcaAmhsIiAzNjc4YjFhNWQyYzc5MjFmNGY0MzIzNjlhYWJkNTc2MQ",
    //  "sid_tt":"453106b016b2bed83e094483089357d4",
    //  "ttcid":"49ce5007e2a84854879e27058b018ea730",
    //  "gfgarrcache_ea32c849":"1",
    //  "passport_auth_status_ss":"ba2ab372fe65a793d31018bb91dabc50%2Ced161a9070b11174fe5be033d9af3f10",
    //  "sessionid_ss":"453106b016b2bed83e094483089357d4",
    //  "s_v_web_id":"verify_m42rd19n_ydqNGz6x_rwGu_4lrR_80f6_FE8S8nd4g7hK",
    //  "sid_guard":"453106b016b2bed83e094483089357d4%7C1732887443%7C5184001%7CTue%2C+28-Jan-2025+13%3A37%3A24+GMT",
    //  "passport_csrf_token":"92853b7b669ff13e17d5ada2e962880a",
    //  "store-region":"cn-hn",
    //  "sso_uid_tt_ss":"518e748eacf52b96f8f25ac15dcbf7a2",
    //  "store-region-src":"uid",
    //  "tt_scid":"wOhYHo3bVHPQZoE72VXgqFBSucU7GGauQgIEMevcjY8uR3t1PM0Y9fPRJ4cHUbpXe55c",
    //  "n_mh":"1IFsTbSLKq1EnKvzi2LpJgAjwq1HCzzLNJeKfaJtSRQ"
    //}


    //{
    //  "auth_type":-1,
    //  "avatar_url":"https://sf6-cdn-tos.toutiaostatic.com/img/user-avatar/70f44a5ab18cdf0bc91be1468fefff6b~300x300.image",
    //  "code":0,
    //  "extra":{
    //    "claim_origin_permission":"1",
    //    "impr_boost_pkg_permission":"0",
    //    "medium_video_cnt":"0",
    //    "show_authentication_entrance":"1"
    //  },
    //  "is_creator":false,
    //  "media_id":1815611467541508,
    //  "message":"success",
    //  "name":"限量版逗比",
    //  "show_data_assistant":false,
    //  "show_impression":false,
    //  "tie_fans":{
    //    "banner_image_url":"https://p9.toutiaoimg.com/obj/toutiao-activity-bucket/activity/pgc_media/task4/f8722a4963abfc81ebc014b84bae74af",
    //    "banner_schema":"sslocal://webview?hide_search=1&status_bar_color=white&bounce_disable=1&hide_more=1&hide_back_close=1&hide_bar=1&hide_status_bar=1&should_append_common_param=1&url=https%3A%2F%2Fi.snssdk.com%2Ffeoffline%2Ftt_data_assistant%2Fincrease-fans.html",
    //    "tie_fan_authority":false
    //  },
    //  "total_fans_count":0,
    //  "user_id":570028048259673,
    //  "user_id_str":"570028048259673",
    //  "welcome_msg":"在头条创作的第 16 天"
    //}

    @Test
    void i() {
        //ChromeDriver driver = ChromeDriverUtils.initChromeDriver();
        //driver.get(AccountPlatformEnum.TOUTIAO_LOGIN.getPlatformURL());
        //Map<String, String> map = driver.manage().getCookies().stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
        //StringBuilder stringBuilder = new StringBuilder();
        //for (String key : TOUTIAO_COOKIE_SORT_LIST) {
        //    stringBuilder.append(String.format("%s=%s; ",key,map.get(key)));
        //}
        //String body = HttpUtil.createGet("https://mp.toutiao.com/mp/agw/creator_center/user_info?app_id=1231")
        //        .cookie(stringBuilder.toString()).execute().body();
        //System.out.println(body);
    }

    @Test
    void j() {
        ChromeDriver driver1 = null;
        ChromeDriver driver2 = null;

        try {
            // 创建第一个浏览器实例

            driver1 = new ChromeDriver(test("toutiao1"));
            driver1.get("https://mp.toutiao.com/profile_v4/graphic/publish");

            // 创建第二个浏览器实例
            driver2 = new ChromeDriver(test("toutiao2"));
            driver2.get("https://mp.toutiao.com/profile_v4/graphic/publish");

            // 等待操作完成
            Thread.sleep(20000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 确保浏览器正确关闭
            if (driver1 != null) {
                driver1.quit();
            }
            if (driver2 != null) {
                driver2.quit();
            }
        }
    }

    public ChromeOptions test(String str) {
        System.setProperty("webdriver.chrome.driver", "D:\\桌面\\chrome-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments(String.format("user-data-dir=D:\\桌面\\chrome-win64\\selenium\\%s", str));
        options.addArguments("profile-directory=" + str);
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return options;
    }

    @Test
    void l() throws IOException {
        //String x = HttpUtil.get("https://www.toutiao.com/article/7443609472766968347");
        //Document doc = Jsoup.parse(x);
        //Elements elementsByClass = doc.getElementsByClass("article-content");
        //System.out.println(elementsByClass.text());
        Document doc = Jsoup.connect("https://www.toutiao.com/article/7443609472766968347").get();
        Elements elementsByClass = doc.getElementsByClass("article-content");
        System.out.println(doc);
    }

    @Test
    void k() {
        String thePaPerStr = HttpUtil.get("https://cache.thepaper.cn/contentapi/wwwIndex/rightSidebar");
        //System.out.println(thePaPerStr);
        Object entries = JSONUtil.parseObj(JSONUtil.parseObj(thePaPerStr)).get("data");
        //System.out.println(entries);
        List<Object> thePaPerList = (List<Object>) JSONUtil.parseObj(entries).get("hotNews");
        System.out.println(thePaPerList.size());
        //for (Object o : thePaPerList) {
        Map<String, Object> map = (Map<String, Object>) thePaPerList.get(0);
        System.out.println(map.get("contId"));
        System.out.println(map.get("name"));
        System.out.println(map.get("pic"));
        //}
    }

    //body: {
    //      partner_id: "wap",
    //      param: {
    //        siteId: 1,
    //        platformId: 2,
    //      },
    //      timestamp: new Date().getTime(),
    //    },
    @Test
    void m() {
        JSONObject paramObject = new JSONObject();
        paramObject.set("siteId", 1);
        paramObject.set("platformId", 2);

        JSONObject bodyObject = new JSONObject();
        bodyObject.set("partner_id", "wap");
        bodyObject.set("param", paramObject);
        bodyObject.set("timestamp", System.currentTimeMillis());
        String krStr = HttpUtil.createPost("https://gateway.36kr.com/api/mis/nav/home/nav/rank/hot")
                .header("Content-Type", "application/json; charset=utf-8")
                .body(JSONUtil.toJsonStr(bodyObject))
                .execute().body();
        Object dataJson = JSONUtil.parseObj(JSONUtil.parseObj(krStr)).get("data");
        List<Object> hotRankList = (List<Object>) JSONUtil.parseObj(dataJson).get("hotRankList");
        for (Object obj : hotRankList) {
            Map<String, Object> map = (Map<String, Object>) obj;
            Map<String, Object> templateMaterialMap = (Map<String, Object>) map.get("templateMaterial");
            System.out.println(templateMaterialMap.get("itemId"));
            System.out.println(templateMaterialMap.get("widgetTitle"));
            System.out.println(templateMaterialMap.get("widgetImage"));
        }

    }

    @Test
    void n() {
        ChromeDriver driver = ChromeDriverUtils.initChromeDriver("toutiao1");
        driver.get("https://mp.toutiao.com/profile_v4/graphic/publish");
        try {
            Thread.sleep(3000);
            driver.findElement(By.cssSelector("body")).click();
            //WebElement title = driver.findElement(By.cssSelector("textarea[placeholder='请输入文章标题（2～30个字）']"));
            //// 点击文本框
            //title.click();
            //// 输入文本
            //title.sendKeys("周杰伦演唱会彩排图曝光，歌迷惊叹：时间倒流二十年！");
            //WebElement proseMirror = driver.findElement(By.className("ProseMirror"));
            //proseMirror.click();
            //proseMirror.sendKeys("近日，周杰伦在台北大巨蛋的演唱会彩排图在网络上疯传，引发无数粉丝的热议。图中，周杰伦手持麦克风，头戴鸭舌帽，一身休闲装扮，仿佛回到了他出道初期的模样。这一幕让不少歌迷感叹：“这不就是二十年前的周杰伦吗？”更有粉丝调侃道：“是不是该唱《反方向的钟》了？”\n"
            //
            //);
            //Thread.sleep(3000);
            //((JavascriptExecutor) driver).executeScript("window.open()");
            //ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            //// 切换到新标签页
            //driver.switchTo().window(tabs.get(1));
            //driver.get("https://p3-sign.toutiaoimg.com/tos-cn-i-axegupay5k/1c229c8dfd8f45fea62c48451271869c~tplv-tt-origin-web:gif.jpeg?_iz=58558&from=article.pc_detail&lk3s=953192f4&x-expires=1734062204&x-signature=kqCbGgPwV7sVUTeZk9tvgajCwgc%3D");
            //driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "c");
            //driver.switchTo().window(tabs.get(0));
            //proseMirror.sendKeys(Keys.CONTROL + "v");
            //proseMirror.sendKeys("周杰伦作为华语乐坛的天王级人物，自2000年出道以来，凭借其独特的音乐风格和创作才华，赢得了无数歌迷的喜爱。从《简单爱》到《青花瓷》，再到《告白气球》，每一首歌曲都成为了经典之作。而他的演唱会更是场场爆满，门票往往一开售就被抢购一空。\n" +
            //        "此次台北大巨蛋的演唱会，是周杰伦时隔多年再次回到台湾举办大型个唱。据悉，这场演唱会不仅将带来众多经典歌曲的现场演绎，还会有全新的舞台设计和视觉效果，力求给观众带来一场视听盛宴。\n");
            ////Thread.sleep(3000);
            //proseMirror.sendKeys(Keys.CONTROL, Keys.ARROW_DOWN);
            //driver.switchTo().window(tabs.get(1));
            //driver.get("https://p3-sign.toutiaoimg.com/tos-cn-i-6w9my0ksvp/2430c74114b9464a9e941d294297ed10~tplv-tt-origin-web:gif.jpeg?_iz=58558&from=article.pc_detail&lk3s=953192f4&x-expires=1734062204&x-signature=5vN7gJGJgr9FmNjRrKQDv7zvmmU%3D");
            //driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "c");
            //driver.switchTo().window(tabs.get(0));
            //proseMirror.sendKeys(Keys.CONTROL + "v");
            //proseMirror.sendKeys(Keys.CONTROL, Keys.ARROW_DOWN);
            //proseMirror.sendKeys("对于周杰伦来说，每一次的演唱会都是一次与歌迷们的深度交流。他表示：“我希望我的音乐会让大家感受到我对音乐的热爱和执着，也希望每个人都能在我的歌声中找到属于自己的故事。\n" +
            //        "那么，当周杰伦再次唱起那些熟悉的旋律时，你会想起哪一段青春的记忆呢？让我们一起期待这场穿越时空的音乐之旅吧！");

            //作品声明ai创作点击
            WebElement element = driver.findElement(By.cssSelector("html > body > div:nth-of-type(1) > div > div:nth-of-type(3) > section > main > div:nth-of-type(2) > div > div > div:nth-of-type(2) > div > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div:nth-of-type(2) > div:nth-of-type(9) > div > div:nth-of-type(2) > div > div > span > span:nth-of-type(1)"));
            driver.executeScript("arguments[0].scrollIntoView(true);", element);
            element.findElement(By.tagName("label")).click();

            //点击预览发布按钮
            driver.findElement(
                    By.cssSelector("button.byte-btn.byte-btn-primary.byte-btn-size-large.byte-btn-shape-square.publish-btn.publish-btn-last")
            ).click();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void o() {
        ChromeDriver driver = ChromeDriverUtils.initHeadlessChromeDriver("Default");
        driver.get("https://www.toutiao.com/article/7445093634006942235/?log_from=3145a1f701f3f_1733457405677");
        String page_source = driver.getPageSource();
        Document doc = Jsoup.parse(page_source);
        Elements elementsByClass = doc.getElementsByClass("article-content");
        for (Element byClass : elementsByClass) {
            for (Element element : byClass.getElementsByTag("img")) {
                String articleHref = element.attr("src");
                System.out.println(articleHref);
            }
        }
    }


}
