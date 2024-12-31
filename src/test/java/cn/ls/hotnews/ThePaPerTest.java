package cn.ls.hotnews;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.ai.AICommon;
import cn.ls.hotnews.ai.JieYueAIServiceImpl;
import cn.ls.hotnews.model.vo.HotNewsVO;
import cn.ls.hotnews.service.impl.hotnews.WangYiHotNewsServiceImpl;
import cn.ls.hotnews.utils.ChromeDriverUtils;
import cn.ls.hotnews.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * title: ThePaPerTest
 * author: liaoshuo
 * date: 2024/12/8 13:21
 * description:
 */
@Slf4j
@SpringBootTest
public class ThePaPerTest {

    public final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    @Resource
    AICommon aiCommon;
    @Resource
    WangYiHotNewsServiceImpl wangYiHotNewsService;
    @Resource
    JieYueAIServiceImpl jieYueAIService;

    @Test
    void a() {
        ChromeDriver driver = ChromeDriverUtils.initHeadlessChromeDriver("Default");
        driver.get("https://www.thepaper.cn/newsDetail_forward_29573347");
        String pageSource = driver.getPageSource();
        Document doc = Jsoup.parse(pageSource);
        Elements elementsByClass = doc.getElementsByClass("index_wrapper__L_zqV");
        System.out.println("标题：" + doc.getElementsByClass("index_title__B8mhI").text());
        System.out.println("文章：" + doc.getElementsByClass("index_cententWrap__Jv8jK").text());
        for (Element byClass : elementsByClass) {
            System.out.println(byClass.getElementsByTag("img").attr("src"));
        }
        driver.quit();
    }

    @Test
    void b() {
        ChromeDriver driver = ChromeDriverUtils.initHeadlessChromeDriver("Default");
        driver.get("https://36kr.com/p/3068390606926727");
        String pageSource = driver.getPageSource();
        Document doc = Jsoup.parse(pageSource);
        Elements elementsByClass = doc.getElementsByClass("article-wrapper common-width");
        elementsByClass.select(".article-title-icon").remove();
        elementsByClass.select(".img-desc").remove();
        elementsByClass.select(".article-footer-txt").remove();
        String text = elementsByClass.text();
        System.out.println(text.substring(0, text.indexOf(" ")));
        String context = text.substring(text.indexOf(" "));
        String[] strings = context.split(" ");
        //for (String string : strings) {
        System.out.println(CommonUtils.cleanText(context).trim());
        //}
        driver.quit();
    }

    @Test
    void c() {
        String str = HttpUtil.get("https://m.163.com/fe/api/hot/news/flow");
        Object JsonData = JSONUtil.parseObj(str).get("data");
        List<Object> objList = (List<Object>) JSONUtil.parseObj(JsonData).get("list");
        Map<String, Object> map = (Map<String, Object>) objList.get(0);
        System.out.println(map.get("docid"));
        System.out.println(map.get("title"));
        System.out.println(map.get("url"));
        System.out.println(map.get("imgsrc"));
    }

    @Test
    void d() {
        String str = HttpUtil.get("https://r.inews.qq.com/gw/event/hot_ranking_list?page_size=20");
        List<Object> idlist = (List<Object>) JSONUtil.parseObj(str).get("idlist");
        List<Object> newslist = (List<Object>) JSONUtil.parseObj(idlist.get(0)).get("newslist");
        newslist.remove(0);
        System.out.println(newslist);
        Map<String, Object> map = (Map<String, Object>) newslist.get(0);
        System.out.println(map.get("id"));
        System.out.println(map.get("title"));
        System.out.println(map.get("url"));
        System.out.println(map.get("abstract"));
    }

    /**
     * 网易文章获取
     */
    @Test
    void e() {
        ChromeDriver driver = ChromeDriverUtils.initHeadlessChromeDriver("Default");
        driver.get("https://www.163.com/dy/article/JJ1P7LF4055619ZB.html");
        String pageSource = driver.getPageSource();
        Document doc = Jsoup.parse(pageSource);
        Elements select = doc.select(".f_center");
        for (Element element : select) {
            System.out.println(element.getElementsByTag("img").attr("src"));
        }
        driver.quit();
    }

    @Test
    void f() {
        ChromeDriver driver = ChromeDriverUtils.initHeadlessChromeDriver("Default");
        driver.get("https://view.inews.qq.com/a/20241210A00JAS00");
        String pageSource = driver.getPageSource();
        Document doc = Jsoup.parse(pageSource);
        Elements elementsByClass = doc.getElementsByClass("rich_media_content");
        elementsByClass.select("strong").remove();
        System.out.println(doc.select("h1").text());
        System.out.println(CommonUtils.cleanText(elementsByClass.text()));
        for (Element byClass : elementsByClass) {
            System.out.println(byClass.getElementsByTag("img").attr("src"));
        }
        driver.quit();
    }

    @Test
    void g() {
        ChromeDriver driver = ChromeDriverUtils.initHeadlessChromeDriver("Default");
        driver.get("https://www.36kr.com/p/3071816389587847");
        String pageSource = driver.getPageSource();
        Document doc = Jsoup.parse(pageSource);
        String title = doc.getElementsByClass("article-title margin-bottom-20 common-width").text();
        Elements elementsByClass = doc.getElementsByClass("common-width content articleDetailContent kr-rich-text-wrapper");
        elementsByClass.select("strong").remove();
        elementsByClass.select("a").remove();
        System.out.println(title);
        System.out.println(CommonUtils.cleanText(elementsByClass.text()));
        for (Element byClass : elementsByClass) {
            byClass.getElementsByTag("img").attr("src");
        }
        driver.quit();
    }

    @Test
    void h() {
        //        String s = HttpUtil.get("https://news.163.com/special/cm_yaowen20200213/?callback=data_callback");
        String s = HttpUtil.get("https://ent.163.com/special/000381Q1/newsdata_movieidx.js?callback=data_callback");
        //String s = HttpUtil.get("https://edu.163.com/special/002987KB/newsdata_edu_hot.js?callback=data_callback");
        String str = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        for (Object o : JSONUtil.parseArray(str)) {
            //System.out.println(o);
            Map<String, Object> map = (Map<String, Object>) o;
            String timeStr = (String) map.get("time");
            if (StringUtils.isNotBlank(timeStr)) {
                String[] timeArry = timeStr.split(" ");
                String[] dateArry = timeArry[0].split("/");
                String[] newsTimeArry = timeArry[1].split(":");

                LocalDateTime targetTime = LocalDateTime.of(Integer.parseInt(dateArry[2]),
                        Integer.parseInt(dateArry[0]),
                        Integer.parseInt(dateArry[1]),
                        Integer.parseInt(newsTimeArry[0]),
                        Integer.parseInt(newsTimeArry[1]),
                        Integer.parseInt(newsTimeArry[2])
                ); // 示例时间

                // 判断目标时间是否在当前时间的一小时之内
                boolean isWithinOneHour = targetTime.isAfter(currentTime.minusMinutes(10)) && targetTime.isBefore(currentTime.plusMinutes(10));

                // 输出结果
                if (isWithinOneHour) {
                    System.out.println("3小时之内。");
                    String title = (String) map.get("title");
                    String docurl = map.get("docurl").toString();
                    System.out.printf("%s:%s%n", title, docurl);
                }
            }
            //String title = (String) map.get("title");
            //String docurl = map.get("docurl").toString();
            //String docId = docurl.substring(docurl.lastIndexOf("/") + 1, docurl.indexOf(".html"));
            //String imgurl = (String) map.get("imgurl");
            //
            //HotNewsVO hotNewsVO = new HotNewsVO();
            //hotNewsVO.setBiId(docId);
            //hotNewsVO.setTitle(title);
            //hotNewsVO.setHotURL(docurl);
            //hotNewsVO.setImageURL(imgurl);
        }
    }

    public static void main(String[] args) {
        // 创建一个 Consumer，接受一个字符串并打印它
        Consumer<String> printConsumer = (s) -> System.out.println(s);

        // 使用 Consumer
        printConsumer.accept("Hello, World!"); // 输出: Hello, World!

        // 另一个示例，使用 Consumer 处理一个整数列表
        processNumbers(new int[]{1, 2, 3, 4, 5}, (n) -> System.out.println(n * 2));
    }

    // 方法接受一个整数数组和一个 Consumer
    public static void processNumbers(int[] numbers, Consumer<Integer> consumer) {
        for (int number : numbers) {
            consumer.accept(number); // 对每个数字应用 Consumer
        }
    }

    @Test
    void i() {
        Map<String, HotNewsVO> mapVO = new HashMap<>();
        while (true) {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + TimeUnit.MINUTES.toMillis(5); // 5分钟的结束时间
            // 在5分钟内持续执行代码
            while (System.currentTimeMillis() < endTime) {
                log.info("监控中****");
                String s = HttpUtil.get("https://ent.163.com/special/000381Q1/newsdata_movieidx.js?callback=data_callback");
                //String s = HttpUtil.get("https://edu.163.com/special/002987KB/newsdata_edu_hot.js?callback=data_callback");
                String str = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
                // 获取当前时间
                LocalDateTime currentTime = LocalDateTime.now();
                for (Object o : JSONUtil.parseArray(str)) {
                    //System.out.println(o);
                    Map<String, Object> map = (Map<String, Object>) o;
                    String timeStr = (String) map.get("time");
                    if (StringUtils.isNotBlank(timeStr)) {
                        String[] timeArry = timeStr.split(" ");
                        String[] dateArry = timeArry[0].split("/");
                        String[] newsTimeArry = timeArry[1].split(":");

                        LocalDateTime targetTime = LocalDateTime.of(Integer.parseInt(dateArry[2]),
                                Integer.parseInt(dateArry[0]),
                                Integer.parseInt(dateArry[1]),
                                Integer.parseInt(newsTimeArry[0]),
                                Integer.parseInt(newsTimeArry[1]),
                                Integer.parseInt(newsTimeArry[2])
                        ); // 示例时间

                        // 判断目标时间是否在当前时间的一小时之内
                        boolean isWithinOneHour = targetTime.isAfter(currentTime.minusMinutes(10)) && targetTime.isBefore(currentTime.plusMinutes(10));

                        // 输出结果
                        if (isWithinOneHour) {
                            String title = (String) map.get("title");
                            String docurl = map.get("docurl").toString();
                            String docId = docurl.substring(docurl.lastIndexOf("/") + 1, docurl.indexOf(".html"));
                            String imgurl = (String) map.get("imgurl");
                            if (!mapVO.containsKey(docId)) {
                                HotNewsVO hotNewsVO = new HotNewsVO();
                                hotNewsVO.setBiId(docId);
                                hotNewsVO.setTitle(title);
                                hotNewsVO.setHotURL(docurl);
                                hotNewsVO.setImageURL(imgurl);
                                System.out.println("10分钟内发布的\n");
                                System.out.printf("%s:%s%n", title, docurl);
                                mapVO.put(docId, hotNewsVO);
                            }
                        }
                    }
                }

                // 这里可以添加适当的休眠，避免过于频繁的输出
                try {
                    Thread.sleep(1000); // 每秒执行一次
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // 休息1分钟
            log.info("休息1分钟...");
            try {
                //if(CollectionUtil.isNotEmpty(mapVO)){
                //    mapVO.clear();
                //}
                Thread.sleep(TimeUnit.MINUTES.toMillis(1)); // 休息1分钟
                log.info("休息结束...");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    void j() {
        Map<String, HotNewsVO> mapVO = new HashMap<>();
        while (true) {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + TimeUnit.MINUTES.toMillis(5); // 5分钟的结束时间
            // 在5分钟内持续执行代码
            while (System.currentTimeMillis() < endTime) {
                log.info("监控中****");
                String s = HttpUtil.get("https://ent.163.com/special/000381Q1/newsdata_movieidx.js?callback=data_callback");
                //String s = HttpUtil.get("https://edu.163.com/special/002987KB/newsdata_edu_hot.js?callback=data_callback");
                String str = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
                // 获取当前时间
                LocalDateTime currentTime = LocalDateTime.now();


                //for (Object o : JSONUtil.parseArray(str)) {
                //    //System.out.println(o);
                //    Map<String, Object> map = (Map<String, Object>) o;
                //    String timeStr = (String) map.get("time");
                //    if (StringUtils.isNotBlank(timeStr)) {
                //        String[] timeArry = timeStr.split(" ");
                //        String[] dateArry = timeArry[0].split("/");
                //        String[] newsTimeArry = timeArry[1].split(":");
                //
                //        LocalDateTime targetTime = LocalDateTime.of(Integer.parseInt(dateArry[2]),
                //                Integer.parseInt(dateArry[0]),
                //                Integer.parseInt(dateArry[1]),
                //                Integer.parseInt(newsTimeArry[0]),
                //                Integer.parseInt(newsTimeArry[1]),
                //                Integer.parseInt(newsTimeArry[2])
                //        ); // 示例时间
                //
                //        // 判断目标时间是否在当前时间的一小时之内
                //        boolean isWithinOneHour = targetTime.isAfter(currentTime.minusMinutes(10)) && targetTime.isBefore(currentTime.plusMinutes(10));
                //
                //        // 输出结果
                //        if (isWithinOneHour) {
                //            String title = (String) map.get("title");
                //            String docurl = map.get("docurl").toString();
                //            String docId = docurl.substring(docurl.lastIndexOf("/") + 1, docurl.indexOf(".html"));
                //            String imgurl = (String) map.get("imgurl");
                //            if(!mapVO.containsKey(docId)){
                //                HotNewsVO hotNewsVO = new HotNewsVO();
                //                hotNewsVO.setBiId(docId);
                //                hotNewsVO.setTitle(title);
                //                hotNewsVO.setHotURL(docurl);
                //                hotNewsVO.setImageURL(imgurl);
                //                System.out.println("10分钟内发布的\n");
                //                System.out.printf("%s:%s%n", title, docurl);
                //                mapVO.put(docId,hotNewsVO);
                //            }
                //        }
                //    }
                //}

                // 这里可以添加适当的休眠，避免过于频繁的输出
                try {
                    Thread.sleep(1000); // 每秒执行一次
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // 休息1分钟
            log.info("休息1分钟...");
            try {
                //if(CollectionUtil.isNotEmpty(mapVO)){
                //    mapVO.clear();
                //}
                Thread.sleep(TimeUnit.MINUTES.toMillis(1)); // 休息1分钟
                log.info("休息结束...");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    void k() {
        Map<String, HotNewsVO> mapVO = new HashMap<>();
        while (true) {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + TimeUnit.MINUTES.toMillis(5); // 5分钟的结束时间
            // 在5分钟内持续执行代码
            while (System.currentTimeMillis() < endTime) {
                log.info("监控中****");
                String s = HttpUtil.get("https://ent.163.com/special/000381Q1/newsdata_movieidx.js?callback=data_callback");
                //String s = HttpUtil.get("https://edu.163.com/special/002987KB/newsdata_edu_hot.js?callback=data_callback");
                String str = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
                // 获取当前时间
                LocalDateTime currentTime = LocalDateTime.now();
                for (Object o : JSONUtil.parseArray(str)) {
                    //System.out.println(o);
                    Map<String, Object> map = (Map<String, Object>) o;
                    String timeStr = (String) map.get("time");
                    if (StringUtils.isNotBlank(timeStr)) {
                        String[] timeArry = timeStr.split(" ");
                        String[] dateArry = timeArry[0].split("/");
                        String[] newsTimeArry = timeArry[1].split(":");

                        LocalDateTime targetTime = LocalDateTime.of(Integer.parseInt(dateArry[2]),
                                Integer.parseInt(dateArry[0]),
                                Integer.parseInt(dateArry[1]),
                                Integer.parseInt(newsTimeArry[0]),
                                Integer.parseInt(newsTimeArry[1]),
                                Integer.parseInt(newsTimeArry[2])
                        ); // 示例时间

                        // 判断目标时间是否在当前时间的一小时之内
                        boolean isWithinOneHour = targetTime.isAfter(currentTime.minusMinutes(10)) && targetTime.isBefore(currentTime.plusMinutes(10));

                        // 输出结果
                        if (isWithinOneHour) {
                            String title = (String) map.get("title");
                            String docurl = map.get("docurl").toString();
                            String docId = docurl.substring(docurl.lastIndexOf("/") + 1, docurl.indexOf(".html"));
                            String imgurl = (String) map.get("imgurl");
                            if (!mapVO.containsKey(docId)) {
                                HotNewsVO hotNewsVO = new HotNewsVO();
                                hotNewsVO.setBiId(docId);
                                hotNewsVO.setTitle(title);
                                hotNewsVO.setHotURL(docurl);
                                hotNewsVO.setImageURL(imgurl);
                                System.out.println("10分钟内发布的\n");
                                System.out.printf("%s:%s%n", title, docurl);
                                mapVO.put(docId, hotNewsVO);

                                Map<String, Object> mapCompletableFuture = wangYiHotNewsService.getMapCompletableFuture(docurl, title);

                            }
                        }
                    }
                }

                // 这里可以添加适当的休眠，避免过于频繁的输出
                try {
                    Thread.sleep(1000); // 每秒执行一次
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // 休息1分钟
            log.info("休息1分钟...");
            try {
                //if(CollectionUtil.isNotEmpty(mapVO)){
                //    mapVO.clear();
                //}
                Thread.sleep(TimeUnit.MINUTES.toMillis(1)); // 休息1分钟
                log.info("休息结束...");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }


    }

    @Test
    void l(){
        //String body = HttpUtil.createPost("https://odin.sohu.com/odin/api/blockdata")
        //        .body("{\n" +
        //                "  \"pvId\": \"1735309564330_1UsRBwO\",\n" +
        //                "  \"pageId\": \"1735309564519_1735218745177odi_AHC\",\n" +
        //                "  \"mainContent\": {\n" +
        //                "    \"productType\": \"13\",\n" +
        //                "    \"productId\": \"1348\",\n" +
        //                "    \"secureScore\": \"50\",\n" +
        //                "    \"categoryId\": \"27\",\n" +
        //                "    \"adTags\": \"20000084\",\n" +
        //                "    \"authorId\": 121135924\n" +
        //                "  },\n" +
        //                "  \"resourceList\": [\n" +
        //                "    {\n" +
        //                "      \"tplCompKey\": \"TPLFeedMul_2_9_feedData\",\n" +
        //                "      \"isServerRender\": false,\n" +
        //                "      \"isSingleAd\": false,\n" +
        //                "      \"configSource\": \"mp\",\n" +
        //                "      \"content\": {\n" +
        //                "        \"productId\": \"53710\",\n" +
        //                "        \"productType\": \"15\",\n" +
        //                "        \"size\": 20,\n" +
        //                "        \"pro\": \"0,1\",\n" +
        //                "        \"feedType\": \"XTOPIC_SYNTHETICAL\",\n" +
        //                "        \"view\": \"feedMode\",\n" +
        //                "        \"innerTag\": \"news-slice\",\n" +
        //                "        \"spm\": \"smpc.channel_114.block3_77_O0F7zf_1_fd\",\n" +
        //                "        \"page\": 1,\n" +
        //                "        \"requestId\": \"1735309564275WdjhEuX_1348\"\n" +
        //                "      }\n" +
        //                "    }\n" +
        //                "  ]\n" +
        //                "}").execute().body();
        //System.out.println(body);
        JSONArray objects = new JSONArray();
        objects.add(new JSONObject()
                        .set("pvId", "1735309564330_1UsRBwO")
                        .set("pageId", "1735309564519_1735218745177odi_AHC")
                        .set("mainContent", new JSONObject()
                                .set("productType", "13")
                                .set("productId", "1348")
                                .set("secureScore", "50")
                                .set("categoryId", "27")
                                .set("adTags", "20000084")
                                .set("authorId", 121135924)
                        )
                        .set("resourceList", new JSONObject()
                                .set("tplCompKey", "TPLFeedMul_2_9_feedData")
                                .set("isServerRender", "false")
                                .set("isSingleAd", "false")
                                .set("configSource", "mp")
                                .set("content", new JSONObject()
                                        .set("productId", "53710")
                                        .set("productType", "15")
                                        .set("size", "20")
                                        .set("pro", "0,1")
                                        .set("feedType", "XTOPIC_SYNTHETICAL")
                                        .set("view", "feedMode")
                                        .set("innerTag", "news")
                                        .set("spm", "smpc.channel_114.block3_77_O0F7zf_1_fd")
                                        .set("page", "1")
                                        .set("requestId", "1735309564275WdjhEuX_1348")))
        );
        String body = HttpUtil.createPost("https://odin.sohu.com/odin/api/blockdata").body(objects.toString()).execute().body();
        System.out.println(body);
    }

}
