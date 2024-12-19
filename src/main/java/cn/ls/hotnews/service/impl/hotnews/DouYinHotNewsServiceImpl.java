package cn.ls.hotnews.service.impl.hotnews;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpResponse;
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
import cn.ls.hotnews.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.ls.hotnews.constant.CommonConstant.REDIS_DY;
import static cn.ls.hotnews.constant.CommonConstant.REDIS_DY_DTATETIME;

/**
 * title: DouYinHotNewsServiceImpl
 * author: liaoshuo
 * date: 2024/11/19 21:11
 * description: 现不使用
 */
@Slf4j
@Service("douyin")
public class DouYinHotNewsServiceImpl implements HotNewsService {
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
        List<HotNewsVO> douYinList = redisUtils.redisGet(REDIS_DY);
        if (douYinList != null) {
            return douYinList;
        }
        douYinList = new ArrayList<>();
        HotApi platformAPI = hotApiService.getPlatformAPI(HotPlatformEnum.DOUYIN.getValues());
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        try {
            String body = HttpUtil.createGet(platformAPI.getApiURL())
                    .header("Cookie", String.format("passport_csrf_token=%s", getDyTemporaryCookie()))
                    .execute().body();
            Object data = JSONUtil.parseObj(body).get("data");
            JSONArray wordList = JSONUtil.parseObj(data).getJSONArray("word_list");
            for (int i = 0; i < 20; i++) {
                Map<String, Object> map = (Map<String, Object>) wordList.get(i);
                JSONArray imageUrlList = JSONUtil.parseObj(map.get("word_cover")).getJSONArray("url_list");
                HotNewsVO hotNewsVO = new HotNewsVO();
                String sentence_id = (String) map.get("sentence_id");
                long sentenceId = Long.parseLong(sentence_id);
                hotNewsVO.setId(sentenceId);
                hotNewsVO.setTitle((String) map.get("word"));
                hotNewsVO.setHotURL(String.format("https://www.douyin.com/hot/%s", sentence_id));
                hotNewsVO.setImageURL((String) imageUrlList.get(0));
                douYinList.add(hotNewsVO);
            }
            redisUtils.redisSetInOneHour(REDIS_DY, douYinList);
            redisUtils.redisSetInOneHour(REDIS_DY_DTATETIME, new DateTime());
        } catch (Exception e) {
            log.error("dy hot news error:\t", e);
            throw new RuntimeException(e);
        }
        return douYinList;
    }

    /**
     * 根据热点链接获取相关文章
     *
     * @param req 要求
     * @return {@link Map }<{@link String }, {@link String }>
     */
    @Override
    public Map<String, Object> getHotUrlGainNew(HotNewsAddReq req) {
        return null;
    }

    private String getDyTemporaryCookie() {
        String cookisUrl = "https://www.douyin.com/passport/general/login_guiding_strategy/?aid=6383";
        HttpResponse response = HttpUtil.createGet(cookisUrl).execute();
        if (response.getStatus() != 200) {
            log.error("临时cookie获取失败。响应码:\t", response.getStatus());
        }
        List<String> setCookieHeaderList = response.headerList("Set-Cookie");
        return setCookieHeaderList.get(2);
    }
}
