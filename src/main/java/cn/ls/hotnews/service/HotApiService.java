package cn.ls.hotnews.service;

import cn.ls.hotnews.model.dto.hotapi.HotApiQueryReq;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.dto.hotapi.HotApiAddReq;
import cn.ls.hotnews.model.dto.hotapi.HotApiEditReq;
import cn.ls.hotnews.model.vo.HotApiVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
*
* 热点信息接口地Service
* @createDate 2024-11-18 21:08:27
* @author ls
*/
public interface HotApiService extends IService<HotApi> {

    /**
    * 查询热点信息接口地列表
    */
    List<HotApiVO> findHotApiList(HotApiQueryReq queryReq);

    /**
    *  添加热点信息接口地
    */
    Boolean addHotApi(HotApiAddReq hotApiAddReq);

    /**
    *  修改热点信息接口地
    */
    Boolean editHotApi(HotApiEditReq hotApiEditReq);

    /**
    *  删除热点信息接口地
    */
    Boolean delById(Long id);

    HotApiVO toHotAPIVO(HotApi hotApi);

    /**
     * 获取平台 API
     *
     * @param platform 平台
     * @return {@link HotApi }
     */
    HotApi getPlatformAPI(String platform);
}
