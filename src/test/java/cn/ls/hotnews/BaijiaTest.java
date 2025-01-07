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
        // 初始化浏览器并访问百家号发文页面
        ChromeDriver driver = ChromeDriverUtils.initChromeDriver("baijia2");
        driver.get("https://baijiahao.baidu.com/builder/rc/edit?type=news&is_from_cms=1");
        Thread.sleep(3000);
        
        // 输入文章标题
        WebElement titleInput = driver.findElement(By.cssSelector("textarea[placeholder='请输入标题（8 - 30字）']"));
        titleInput.click();
        titleInput.sendKeys("乐山大佛叫哦阿拉基哦就麻辣！");
        titleInput.sendKeys(Keys.TAB);

        // 输入文章内容并插入图片
        //WebElement contentIframe = driver.findElement(By.cssSelector("#ueditor_0"));
        WebElement contentIframe = ChromeDriverUtils.driverFindElementByCssSelector(driver,"#ueditor_0");
        contentIframe.sendKeys("asldjiljlkdjoiwejfljdflaksjdfoiejflkflkajdflajflkaj");

        // 打开新标签页复制图片
        driver.executeScript("window.open()");
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());

        //driver.get("https://i-blog.csdnimg.cn/blog_migrate/92c411f7ed26be8b3b1f2e203c9182b2.png");
        ////driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "c");
        //ChromeDriverUtils.driverFindElementByCssSelector(driver,"body")
        //        .sendKeys(Keys.CONTROL + "c");
        String[] str= {
                "https://i-blog.csdnimg.cn/direct/7349e50b285540659f45fd4192bc161c.png",
                "https://img-blog.csdnimg.cn/img_convert/cafbd2b843418757819e4df5190e033e.png",
                "https://i-blog.csdnimg.cn/direct/60413fcb1f994c8e8665657e7a78c04b.png"
        };
        // 切回文章编辑页粘贴图片
        driver.switchTo().window(tabs.get(0));
        for (int i = 0; i < 3; i++) {
            driver.switchTo().window(tabs.get(1));
            driver.get(str[i]);
            //driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "c");
            ChromeDriverUtils.driverFindElementByCssSelector(driver,"body")
                    .sendKeys(Keys.CONTROL + "c");
            driver.switchTo().window(tabs.get(0));
            contentIframe.sendKeys(Keys.CONTROL + "v");
            Thread.sleep(1500);
        }

        // 设置文章封面
        contentIframe.sendKeys(Keys.PAGE_DOWN);
        driver.findElement(By.cssSelector("input[value='three']")).click();

        // 选择封面图片
        WebElement coverImageBtn = driver.findElement(By.cssSelector("html > body > div:nth-of-type(1) > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div > div:nth-of-type(1) > div > form > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div:nth-of-type(1) > div > div"));
        coverImageBtn.click();
        
        WebElement imageSelect = driver.findElement(By.cssSelector("html > body > div:nth-of-type(6) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div > div > div:nth-of-type(2) > div > div > div > div:nth-of-type(1) > div > div"));
        imageSelect.click();
        // 确认封面图片
        driver.findElement(By.cssSelector("button[class='cheetah-btn css-11mocfm cheetah-btn-primary cheetah-btn-solid cheetah-public acss-t8dvyk acss-1kjo6pu acss-1g9lkh4 acss-18ub98p acss-uv0qn4 acss-58e25w acss-1grxnxm acss-1izrri0 cheetah-btn-L cheetah-btn-text-primary']")).click();

        coverImageBtn=driver.findElement(By.cssSelector("html > body > div:nth-of-type(1) > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div > div:nth-of-type(1) > div > form > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div:nth-of-type(1) > div > div"));
        coverImageBtn.click();
        imageSelect=ChromeDriverUtils.driverFindElementByCssSelector(driver,"html > body > div:nth-of-type(6) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div > div > div:nth-of-type(2) > div > div > div > div:nth-of-type(2) > div > div");
        imageSelect.click();
        driver.findElement(By.cssSelector("button[class='cheetah-btn css-11mocfm cheetah-btn-primary cheetah-btn-solid cheetah-public acss-t8dvyk acss-1kjo6pu acss-1g9lkh4 acss-18ub98p acss-uv0qn4 acss-58e25w acss-1grxnxm acss-1izrri0 cheetah-btn-L cheetah-btn-text-primary']")).click();

        coverImageBtn=driver.findElement(By.cssSelector("html > body > div:nth-of-type(1) > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div > div:nth-of-type(1) > div > form > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(3) > div:nth-of-type(1) > div > div"));
        coverImageBtn.click();
        imageSelect=ChromeDriverUtils.driverFindElementByCssSelector(driver,"html > body > div:nth-of-type(6) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div > div > div:nth-of-type(2) > div > div > div > div:nth-of-type(3) > div > div");
        imageSelect.click();
        driver.findElement(By.cssSelector("button[class='cheetah-btn css-11mocfm cheetah-btn-primary cheetah-btn-solid cheetah-public acss-t8dvyk acss-1kjo6pu acss-1g9lkh4 acss-18ub98p acss-uv0qn4 acss-58e25w acss-1grxnxm acss-1izrri0 cheetah-btn-L cheetah-btn-text-primary']")).click();
        // 发布文章
        //driver.findElement(By.cssSelector("button[class*='always-blue']")).click();
    }
}
