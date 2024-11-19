package cn.ls.hotnews.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.enums.HotPlatformEnum;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.vo.HotNewsVO;
import cn.ls.hotnews.service.HotApiService;
import cn.ls.hotnews.service.HotNewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * title: DouYinHotNewsServiceImpl
 * author: liaoshuo
 * date: 2024/11/19 21:11
 * description:
 */
@Slf4j
@Component("douyin")
public class DouYinHotNewsServiceImpl implements HotNewsService {
    @Resource
    private HotApiService hotApiService;

    /**
     * 热点新闻列表
     *
     * @return
     */
    @Override
    public List<HotNewsVO> hotNewsList() {
        List<HotNewsVO> douYinList = new ArrayList<>();
        HotApi platformAPI = hotApiService.getPlatformAPI(HotPlatformEnum.DOUYIN.getValues());
        ThrowUtils.throwIf(platformAPI== null, ErrorCode.NOT_FOUND_ERROR);
        String body = HttpUtil.createGet(platformAPI.getApiURL())
                .header("Cookie", String.format("passport_csrf_token=%s", getDyTemporaryCookie()))
                .execute().body();
        Object data = JSONUtil.parseObj(body).get("data");
        JSONArray wordList = JSONUtil.parseObj(data).getJSONArray("word_list");
        for (Object obj : wordList) {
            Map<String, Object> map = (Map<String, Object>) obj;
            JSONArray imageUrlList = JSONUtil.parseObj(map.get("word_cover")).getJSONArray("url_list");
            HotNewsVO hotNewsVO = new HotNewsVO();
            String sentence_id = (String) map.get("sentence_id");
            long sentenceId = Long.parseLong(sentence_id);
            hotNewsVO.setId(sentenceId);
            hotNewsVO.setTitle((String) map.get("word"));
            hotNewsVO.setHotURL(String.format("https://www.douyin.com/hot/%s",sentence_id));
            hotNewsVO.setImageURL((String) imageUrlList.get(0));
            douYinList.add(hotNewsVO);
        }
        return douYinList;
    }

    private String getDyTemporaryCookie(){
        String cookisUrl = "https://www.douyin.com/passport/general/login_guiding_strategy/?aid=6383";
        HttpResponse response = HttpUtil.createGet(cookisUrl).execute();
        if(response.getStatus()!= 200){
            log.error("临时cookie获取失败。响应码:\t",response.getStatus());
        }
        List<String> setCookieHeaderList = response.headerList("Set-Cookie");
        return setCookieHeaderList.get(2);
    }
}
