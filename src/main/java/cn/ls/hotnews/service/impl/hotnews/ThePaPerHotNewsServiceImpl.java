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

import static cn.ls.hotnews.constant.CommonConstant.REDIS_THEPAPER;
import static cn.ls.hotnews.constant.CommonConstant.REDIS_THEPAPER_DTATETIME;

/**
 * title: ThePaPerHotNewsServiceImpl
 * author: liaoshuo
 * date: 2024/12/2 21:39
 * description: 澎湃新闻
 */
@Slf4j
@Service("thepaper")
public class ThePaPerHotNewsServiceImpl implements HotNewsService {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private HotApiService hotApiService;
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
        String thePaPerKey = REDIS_THEPAPER;
        List<HotNewsVO> hotNewsVOList = redisUtils.redisGet(thePaPerKey);
        if (hotNewsVOList != null) {
            return hotNewsVOList;
        }
        HotApi platformAPI = hotApiService.getPlatformAPI("thepaper");
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        try {
            String thePaPerStr = HttpUtil.get(platformAPI.getApiURL());
            if (thePaPerStr == null) {
                return new ArrayList<>();
            }
            Object entries = JSONUtil.parseObj(JSONUtil.parseObj(thePaPerStr)).get("data");
            List<Object> thePaPerList = (List<Object>) JSONUtil.parseObj(entries).get("hotNews");
            hotNewsVOList = new ArrayList<>();
            for (Object o : thePaPerList) {
                Map<String, Object> map = (Map<String, Object>) o;
                HotNewsVO hotNewsVO = new HotNewsVO();
                String contId = map.get("contId").toString();
                hotNewsVO.setId(Long.valueOf(contId));
                hotNewsVO.setTitle((String) map.get("name"));
                String thePaPerUrl = "https://www.thepaper.cn/newsDetail_forward_%s";
                hotNewsVO.setHotURL(String.format(thePaPerUrl, contId));
                hotNewsVO.setImageURL((String) map.get("pic"));
                hotNewsVOList.add(hotNewsVO);
            }
            redisUtils.redisSetInOneHour(thePaPerKey, hotNewsVOList);
            redisUtils.redisSetInOneHour(REDIS_THEPAPER_DTATETIME, new DateTime());
        } catch (Exception e) {
            log.error("thepaper news is error, message:{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "澎湃新闻获取失败");
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

    /**
     * 按 DOC 进行编辑
     *
     * @param doc 医生
     * @return {@link ArticleVO }
     */
    private ArticleVO getEditingByDoc(Document doc) {
        ArticleVO articleVO = new ArticleVO();
        List<String> imgList = new ArrayList<>();
        Elements elementsByClass = doc.getElementsByClass("index_wrapper__L_zqV");
        for (Element byClass : elementsByClass) {
            imgList.add(byClass.getElementsByTag("img").attr("src"));
        }
        articleVO.setTitle(doc.getElementsByClass("index_title__B8mhI").text());
        articleVO.setConText(doc.getElementsByClass("index_cententWrap__Jv8jK").text());
        articleVO.setImgList(imgList);
        return articleVO;
    }
}
