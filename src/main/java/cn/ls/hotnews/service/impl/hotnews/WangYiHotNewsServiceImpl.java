package cn.ls.hotnews.service.impl.hotnews;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.BusinessException;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.manager.ChromeProcessCleaner;
import cn.ls.hotnews.model.dto.hotnews.HotNewsAddReq;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.vo.ArticleVO;
import cn.ls.hotnews.model.vo.HotNewsVO;
import cn.ls.hotnews.service.HotApiService;
import cn.ls.hotnews.service.HotNewsService;
import cn.ls.hotnews.utils.ChromeDriverUtils;
import cn.ls.hotnews.utils.CommonUtils;
import cn.ls.hotnews.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.ls.hotnews.constant.CommonConstant.REDIS_WANGYI;
import static cn.ls.hotnews.constant.CommonConstant.REDIS_WANGYI_DTATETIME;

/**
 * title: WangYiHotNewsServiceImpl
 * author: liaoshuo
 * date: 2024/12/9 13:11
 * description: 网易新闻
 */
@Slf4j
@Service("wangyi")
public class WangYiHotNewsServiceImpl implements HotNewsService {
    @Resource
    private HotApiService hotApiService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private ChromeProcessCleaner chromeProcessCleaner;


    /**
     * 热点新闻列表
     *
     * @return {@link List }<{@link HotNewsVO }>
     */
    @Override
    public List<HotNewsVO> hotNewsList() {
        String Key = REDIS_WANGYI;
        List<HotNewsVO> hotNewsVOList = redisUtils.redisGet(Key);
        if (hotNewsVOList != null) {
            return hotNewsVOList;
        }
        HotApi platformAPI = hotApiService.getPlatformAPI("wangyi");
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        try {
            String str = HttpUtil.get(platformAPI.getApiURL());
            Object JsonData = JSONUtil.parseObj(str).get("data");
            List<Object> objList = (List<Object>) JSONUtil.parseObj(JsonData).get("list");
            hotNewsVOList = new ArrayList<>();
            String url="https://www.163.com/dy/article/%s.html";
            for (int i = 0; i < 30; i++) {
                Map<String,Object> map= (Map<String, Object>) objList.get(i);
                HotNewsVO hotNewsVO = new HotNewsVO();
                String docid = (String) map.get("docid");
                hotNewsVO.setBiId(docid);
                hotNewsVO.setTitle((String) map.get("title"));
                hotNewsVO.setHotURL(String.format(url,docid));
                hotNewsVO.setImageURL((String) map.get("imgsrc"));
                hotNewsVOList.add(hotNewsVO);
            }
            redisUtils.redisSetInOneHour(Key, hotNewsVOList);
            redisUtils.redisSetInOneHour(REDIS_WANGYI_DTATETIME, new DateTime());
        } catch (Exception e) {
            log.error("wangyi news is error, message:{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "网易新闻热点获取失败");
        }
        return hotNewsVOList;
    }

    /**
     * 根据热点链接获取相关文章
     *
     * @param req 要求
     * @return {@link Map }<{@link String }, {@link String }>
     */
    @Override
    public Map<String, Object> getHotUrlGainNew(HotNewsAddReq req) {
        ThrowUtils.throwIf(req == null, ErrorCode.PARAMS_ERROR);
        String title = req.getTitle();
        String hotURL = req.getHotURL();
        CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> {
            ChromeDriver driver;
            Map<String, Object> editingMap;
            try {
                //操作浏览器访问热点获取相关文章
                driver = ChromeDriverUtils.initHeadlessChromeDriver("Default");
                driver.get(hotURL);
                String pageSource = driver.getPageSource();
                ThrowUtils.throwIf(pageSource == null, ErrorCode.SYSTEM_ERROR);
                Document doc = Jsoup.parse(pageSource);
                //根据热点相关的范文
                editingMap = new HashMap<>();
                editingMap.put("hotNewsTitle", title);
                editingMap.put("editing_1", getEditingByDoc(doc));
            } catch (Exception e) {
                chromeProcessCleaner.cleanupNow();
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "浏览器操作异常");
            }
            //关闭浏览器操作
            driver.quit();
            return editingMap;
        }, threadPoolExecutor);

        try {
            return future.get();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }

    private ArticleVO getEditingByDoc(Document doc) {
        ArticleVO articleVO = new ArticleVO();
        List<String> imgList = new ArrayList<>();
        String postTitle = doc.getElementsByClass("post_title").text();
        String postBody = doc.getElementsByClass("post_body").text();
        Elements select = doc.select(".f_center");
        for (Element element : select) {
            imgList.add(element.getElementsByTag("img").attr("src"));
        }
        articleVO.setTitle(postTitle);
        articleVO.setConText(CommonUtils.cleanText(postBody));
        articleVO.setImgList(imgList);
        return articleVO;
    }
}
