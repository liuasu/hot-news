package cn.ls.hotnews.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.enums.HotPlatformEnum;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.hotnews.HotNewsAddReq;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.vo.HotNewsVO;
import cn.ls.hotnews.service.HotApiService;
import cn.ls.hotnews.service.HotNewsService;
import cn.ls.hotnews.utils.ChromeDriverUtils;
import cn.ls.hotnews.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

import static cn.ls.hotnews.constant.CommonConstant.REDIS_TOUTIAO;
import static cn.ls.hotnews.constant.CommonConstant.REDIS_TOUTIAO_DTATETIME;

/**
 * title: TouTiaoHotNewsServiceImpl
 * author: liaoshuo
 * date: 2024/11/19 20:35
 * description:
 */
@Slf4j
@Component("toutiao")
public class TouTiaoHotNewsServiceImpl implements HotNewsService {

    private final String URL = "https://www.toutiao.com/trending/%s/";
    private final List<String> isArticle = List.of("article");
    @Resource
    private HotApiService hotApiService;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 热点新闻列表
     *
     * @return
     */
    @Override
    public List<HotNewsVO> hotNewsList() {
        List<HotNewsVO> touTiaoList = redisUtils.redisGet(REDIS_TOUTIAO);
        if (touTiaoList != null) {
            return touTiaoList;
        }
        touTiaoList = new ArrayList<>();
        HotPlatformEnum toutiao = HotPlatformEnum.TOUTIAO;
        HotApi platformAPI = hotApiService.getPlatformAPI(toutiao.getValues());
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        try {
            String touTiaoHotJsonStr = HttpUtil.get(platformAPI.getApiURL());
            if (StringUtils.isBlank(touTiaoHotJsonStr)) {
                return new ArrayList<>();
            }
            JSONArray data = JSONUtil.parseObj(touTiaoHotJsonStr).getJSONArray("data");
            for (int i = 0; i < 20; i++) {
                Map<String, Object> map = (Map<String, Object>) data.get(i);
                Map<String, Object> imageMap = (Map<String, Object>) map.get("Image");
                HotNewsVO hotNewsVO = new HotNewsVO();
                String str = (String) map.get("ClusterIdStr");
                Long clusterIdStr = Long.parseLong(str);
                String url = (String) map.get("Url");
                hotNewsVO.setId(clusterIdStr);
                hotNewsVO.setTitle((String) map.get("Title"));
                hotNewsVO.setHotURL(splitUrlIsContainsArticle(url, clusterIdStr));
                hotNewsVO.setImageURL((String) imageMap.get("url"));
                hotNewsVO.setHotDesc((String) map.get("LabelDesc"));
                touTiaoList.add(hotNewsVO);
            }
            redisUtils.redisSetInOneHour(REDIS_TOUTIAO, touTiaoList);
            redisUtils.redisSetInOneHour(REDIS_TOUTIAO_DTATETIME, new DateTime());
        } catch (Exception e) {
            log.error("toutiao hot news error:\t", e);
            throw new RuntimeException(e);
        }
        return touTiaoList;
    }

    /**
     * 根据热点链接获取相关文章
     *
     * @param req 要求
     * @return {@link Map }<{@link String }, {@link String }>
     */
    public Map<String, String> getHotUrlGainNew(HotNewsAddReq req) {
        ThrowUtils.throwIf(req == null, ErrorCode.PARAMS_ERROR);
        String title = req.getTitle();
        String hotURL = req.getHotURL();
        Boolean isArticle = splitUrlIsContainsArticle(hotURL);

        //操作浏览器访问热点获取相关文章
        ChromeDriver driver = ChromeDriverUtils.initChromeDriverNotHeadless();
        driver.get(hotURL);
        String pageSource = driver.getPageSource();

        //根据热点相关的范文
        Map<String, String> editingMap = new HashMap<>();
        editingMap.put("hotNewsTitle", title);
        Document doc = null;
        if (isArticle) {
            //当hotURL中包含 article
            //直接获取文章
            doc = Jsoup.parse(pageSource);
            editingMap.put("editing_1", getEditingByDoc(doc));
        } else {
            List<String> articleHrefList = new ArrayList<>();
            // 使用Jsoup解析HTML内容
            doc = Jsoup.parse(pageSource);
            // 打印文档标题
            Elements elementsByClass = doc.getElementsByClass("feed-card-cover");
            for (Element byClass : elementsByClass) {
                for (Element element : byClass.getElementsByTag("a")) {
                    String articleHref = element.attr("href");
                    if (splitUrlIsContainsArticle(articleHref)) {
                        articleHrefList.add(articleHref);
                    }
                }
            }
            ThrowUtils.throwIf(articleHrefList == null, ErrorCode.NOT_FOUND_ERROR);
            int count = 0;
            for (String url : articleHrefList) {
                if (count < 3) {
                    driver.navigate().to(url);
                    String page_source = driver.getPageSource();
                    doc = Jsoup.parse(page_source);
                    //将相关的范文添加到map中
                    editingMap.put("editing_" + (count += 1), getEditingByDoc(doc));
                }
            }
        }
        //关闭浏览器操作
        driver.quit();
        return editingMap;
    }

    /**
     * 通过操作html元素获取范文
     *
     * @param doc 医生
     * @return {@link String }
     */
    private String getEditingByDoc(Document doc) {
        StringBuilder stringBuilder = new StringBuilder();
        Elements elementsByClass = doc.getElementsByClass("article-content");
        String text = elementsByClass.text();
        String articleTitle = text.substring(0, text.indexOf(" "));
        stringBuilder.append(articleTitle).append("\n");
        List<String> collect = Arrays.stream(text.substring(text.indexOf(" ") + 1).split(" ")).toList();
        for (int i = 0; i < collect.size(); i++) {
            if (i > 1) {
                stringBuilder.append(collect.get(i)).append("\n");
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 拆分 URL 包含文章
     *
     * @param url 网址
     * @param id  身份证
     * @return {@link String }
     */
    private String splitUrlIsContainsArticle(String url, Long id) {
        ThrowUtils.throwIf(url == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(id == null || id < 0, ErrorCode.PARAMS_ERROR);
        String[] split = url.split("/");
        return isArticle.contains(split[3]) ? url : String.format(URL, id);
    }

    /**
     * 拆分 URL 包含文章
     *
     * @param url 网址
     * @return {@link Boolean }
     */
    private Boolean splitUrlIsContainsArticle(String url) {
        ThrowUtils.throwIf(url == null, ErrorCode.PARAMS_ERROR);
        String[] split = url.split("/");
        return isArticle.contains(split[3]);
    }
}
