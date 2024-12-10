package cn.ls.hotnews;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.utils.ChromeDriverUtils;
import cn.ls.hotnews.utils.CommonUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * title: ThePaPerTest
 * author: liaoshuo
 * date: 2024/12/8 13:21
 * description:
 */
@SpringBootTest
public class ThePaPerTest {

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
        Map<String,Object> map= (Map<String, Object>) objList.get(0);
        System.out.println(map.get("docid"));
        System.out.println(map.get("title"));
        System.out.println(map.get("url"));
        System.out.println(map.get("imgsrc"));
    }

    @Test
    void d(){
        String str = HttpUtil.get("https://r.inews.qq.com/gw/event/hot_ranking_list?page_size=20");
        List<Object> idlist = (List<Object>) JSONUtil.parseObj(str).get("idlist");
        List<Object> newslist =(List<Object>) JSONUtil.parseObj(idlist.get(0)).get("newslist");
        newslist.remove(0);
        System.out.println(newslist);
        Map<String,Object> map= (Map<String, Object>)  newslist.get(0);
        System.out.println(map.get("id"));
        System.out.println(map.get("title"));
        System.out.println(map.get("url"));
        System.out.println(map.get("abstract"));
    }


    /**
     * 网易文章获取
     */
    @Test
    void e(){
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
    void f(){
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
    void g(){
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
}
