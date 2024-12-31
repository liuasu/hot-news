package cn.ls.hotnews;

import cn.ls.hotnews.utils.ChromeDriverUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

/**
 * title: BaijiaTest
 * author: liaoshuo
 * date: 2024/12/30 19:25
 * description:
 */
@SpringBootTest
public class BaijiaTest {

    @Test
    void a_publish() throws InterruptedException {
        ChromeDriver driver = ChromeDriverUtils.initChromeDriver("baijia2");
        driver.get("https://baijiahao.baidu.com/builder/rc/edit?type=news&is_from_cms=1");
        Thread.sleep(3000);
        WebElement inputBox = driver.findElement(By.cssSelector("textarea[placeholder='请输入标题（8 - 30字）']"));
        inputBox.click();
        inputBox.sendKeys("乐山大佛叫哦阿拉基哦就麻辣！");
        inputBox.sendKeys(Keys.TAB);

        WebElement iframe = driver.findElement(By.cssSelector("#ueditor_0"));
        iframe.sendKeys("asldjiljlkdjoiwejfljdflaksjdfoiejflkflkajdflajflkaj");
        driver.executeScript("window.open()");
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        //打开新的标签页用于复制图片
        driver.switchTo().window(tabs.get(1));
        driver.get("https://i-blog.csdnimg.cn/blog_migrate/92c411f7ed26be8b3b1f2e203c9182b2.png");
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "c");
        driver.switchTo().window(tabs.get(0));
        for (int i = 0; i < 3; i++) {
            iframe.sendKeys(Keys.CONTROL + "v");
            Thread.sleep(1500);
        }



        driver.findElement(By.cssSelector("html > body > div:nth-of-type(1) > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div > div:nth-of-type(1) > div > form > div:nth-of-type(2) > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div > div > div:nth-of-type(1) > label:nth-of-type(1)")).click();


    }
}
