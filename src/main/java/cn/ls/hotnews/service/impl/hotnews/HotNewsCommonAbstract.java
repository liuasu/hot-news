package cn.ls.hotnews.service.impl.hotnews;

import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.BusinessException;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.manager.ChromeProcessCleaner;
import cn.ls.hotnews.model.dto.hotnews.HotNewsAddReq;
import cn.ls.hotnews.model.vo.ArticleVO;
import cn.ls.hotnews.utils.ChromeDriverUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * title: HotNewsAbstract
 * author: liaoshuo
 * date: 2025/1/3 19:52
 * description: 热点新闻
 */
@Component
public abstract class HotNewsCommonAbstract {

    @Resource
    private ChromeProcessCleaner chromeProcessCleaner;


    /**
     * 获取文章
     *
     * @return {@link Map }<{@link String }, {@link Object }>
     */
    public Map<String, Object> extractHotURLGainNewInfo(HotNewsAddReq req) {
        ThrowUtils.throwIf(req == null, ErrorCode.PARAMS_ERROR);
        String title = req.getTitle();
        String hotURL = req.getHotURL();
        return getMapCompletableFuture(hotURL, title);
    }

    /**
     * 获取文章
     *
     * @param hotURL 热门网址
     * @param title  标题
     * @return {@link CompletableFuture }<{@link Map }<{@link String }, {@link Object }>>
     */
    public Map<String, Object> getMapCompletableFuture(String hotURL, String title) {
        ChromeDriver driver = ChromeDriverUtils.initHeadlessChromeDriver("Default");
        Map<String, Object> editingMap = new HashMap<>();
        try {
            //操作浏览器访问热点获取相关文章
            driver.get(hotURL);
            String pageSource = driver.getPageSource();
            ThrowUtils.throwIf(pageSource == null, ErrorCode.SYSTEM_ERROR);
            Document doc = Jsoup.parse(pageSource);
            //根据热点相关的范文
            editingMap.put("hotNewsTitle", title);
            editingMap.put("editing_1", getEditingByDoc(doc));
        } catch (Exception e) {
            chromeProcessCleaner.cleanupNow();
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "浏览器操作异常");
        } finally {
            //关闭浏览器操作
            driver.quit();
        }
        return editingMap;
    }


    public abstract ArticleVO getEditingByDoc(Document doc);
}
