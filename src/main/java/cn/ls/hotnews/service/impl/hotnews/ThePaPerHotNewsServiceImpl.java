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
    private final String thePaPerUrl = "https://www.thepaper.cn/newsDetail_forward_%s";
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private HotApiService hotApiService;

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
    public Map<String, String> getHotUrlGainNew(HotNewsAddReq req) {
        return null;
    }
}
