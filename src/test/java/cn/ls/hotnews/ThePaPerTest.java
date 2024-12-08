package cn.ls.hotnews;

import cn.ls.hotnews.utils.ChromeDriverUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * title: ThePaPerTest
 * author: liaoshuo
 * date: 2024/12/8 13:21
 * description:
 */
@SpringBootTest
public class ThePaPerTest {

    @Test
    void a(){
        ChromeDriver driver = ChromeDriverUtils.initHeadlessChromeDriver("Default");
        driver.get("https://www.thepaper.cn/newsDetail_forward_29573347");
        String pageSource = driver.getPageSource();
        Document doc = Jsoup.parse(pageSource);
        Elements elementsByClass = doc.getElementsByClass("index_wrapper__L_zqV");
        System.out.println("标题："+doc.getElementsByClass("index_title__B8mhI").text());
        System.out.println("文章："+doc.getElementsByClass("index_cententWrap__Jv8jK").text());
        for (Element byClass : elementsByClass) {
            System.out.println(byClass.getElementsByTag("img").attr("src"));
        }
        driver.quit();
    }
}
