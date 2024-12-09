package cn.ls.hotnews.service.impl.hotnews;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.BusinessException;
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

import static cn.ls.hotnews.constant.CommonConstant.REDIS_QQNEWS;
import static cn.ls.hotnews.constant.CommonConstant.REDIS_QQNEWS_DTATETIME;

/**
 * title: QQNewsHotNewsServiceImpl
 * author: liaoshuo
 * date: 2024/12/9 14:17
 * description:
 */
@Slf4j
@Service("qq_news")
public class QQNewsHotNewsServiceImpl implements HotNewsService {

    @Resource
    private HotApiService hotApiService;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 热点新闻列表
     *
     * @return {@link List }<{@link HotNewsVO }>
     */
    @Override
    public List<HotNewsVO> hotNewsList() {
        String Key = REDIS_QQNEWS;
        List<HotNewsVO> hotNewsVOList = redisUtils.redisGet(Key);
        if (hotNewsVOList != null) {
            return hotNewsVOList;
        }
        HotApi platformAPI = hotApiService.getPlatformAPI("qq_news");
        ThrowUtils.throwIf(platformAPI == null, ErrorCode.NOT_FOUND_ERROR);
        try {
            String str = HttpUtil.get(platformAPI.getApiURL());
            List<Object> idlist = (List<Object>) JSONUtil.parseObj(str).get("idlist");
            List<Object> newslist =(List<Object>) JSONUtil.parseObj(idlist.get(0)).get("newslist");
            newslist.remove(0);
            hotNewsVOList = new ArrayList<>();
            for (Object o : newslist) {
                Map<String,Object> map= (Map<String, Object>) o;
                HotNewsVO hotNewsVO = new HotNewsVO();
                hotNewsVO.setBiId((String) map.get("id"));
                hotNewsVO.setTitle((String) map.get("title"));
                hotNewsVO.setHotURL((String) map.get("url"));
                hotNewsVOList.add(hotNewsVO);
            }
            redisUtils.redisSetInOneHour(Key, hotNewsVOList);
            redisUtils.redisSetInOneHour(REDIS_QQNEWS_DTATETIME, new DateTime());
        } catch (Exception e) {
            log.error("qq news news is error, message:{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "腾讯新闻热点获取失败");
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
        return null;
    }
}
