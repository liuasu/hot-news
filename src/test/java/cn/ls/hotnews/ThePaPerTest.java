package cn.ls.hotnews;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.ai.AICommon;
import cn.ls.hotnews.model.vo.HotNewsVO;
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

    public static void main(String[] args) {
        String str = "{\"code\":0,\"data\":1869229938484047874,\"message\":\"ok\",\"currentDateTime\":1734494122241,\"updateDateTime\":1734494122242}";
        System.out.println(JSONUtil.parseObj(str).get("data"));
    }

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

    public final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    @Resource
    AICommon aiCommon;

    @Test
    void i() {
        Map<String,HotNewsVO> mapVO =new HashMap<>();
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
                            if(!mapVO.containsKey(docId)){
                                HotNewsVO hotNewsVO = new HotNewsVO();
                                hotNewsVO.setBiId(docId);
                                hotNewsVO.setTitle(title);
                                hotNewsVO.setHotURL(docurl);
                                hotNewsVO.setImageURL(imgurl);
                                System.out.println("10分钟内发布的\n");
                                System.out.printf("%s:%s%n", title, docurl);
                                mapVO.put(docId,hotNewsVO);
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
    void j(){
        //System.out.println(Thread.currentThread().getName());
        aiCommon.MonitorTheLatestInformation();
        //while (!aiCommon.executorService.isShutdown()){
        //
        //}
    }

    @Test
    void k(){
        aiCommon.clear();
    }
}
