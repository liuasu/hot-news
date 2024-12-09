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
            for (int i = 0; i < 20; i++) {
                Map<String,Object> map= (Map<String, Object>) objList.get(i);
                HotNewsVO hotNewsVO = new HotNewsVO();
                hotNewsVO.setBiId((String) map.get("docid"));
                hotNewsVO.setTitle((String) map.get("title"));
                hotNewsVO.setHotURL((String) map.get("url"));
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
        return null;
    }
}
