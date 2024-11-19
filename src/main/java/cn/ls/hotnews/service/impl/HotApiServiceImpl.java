package cn.ls.hotnews.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.enums.HotPlatformEnum;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.model.dto.hotapi.HotApiQueryReq;
import cn.ls.hotnews.model.vo.HotApiVO;
import cn.ls.hotnews.service.HotApiService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.dto.hotapi.HotApiAddReq;
import cn.ls.hotnews.model.dto.hotapi.HotApiEditReq;
import cn.ls.hotnews.mapper.HotApiMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ls
 * @description hot_api(热点信息接口地址)的Service实现
 * @createDate 2024-11-18 21:08:27
 */
@Slf4j
@Service
public class HotApiServiceImpl extends ServiceImpl<HotApiMapper, HotApi> implements HotApiService {
    /**
     * 查询热点信息接口地列表
     */
    @Override
    public List<HotApiVO> findHotApiList(HotApiQueryReq queryReq) {
        return lambdaQuery()
                .like(StringUtils.isNotBlank(queryReq.getApiName()),HotApi::getApiName, queryReq.getApiName())
                .list().stream().map(this::toHotAPIVO).collect(Collectors.toList());
    }

    /**
     * 添加热点信息接口地
     */
    @Override
    public Boolean addHotApi(HotApiAddReq hotApiAddReq) {
        ThrowUtils.throwIf(hotApiAddReq == null, ErrorCode.PARAMS_ERROR);
        HotApi hotApi = new HotApi();
        BeanUtils.copyProperties(hotApiAddReq, hotApi);
        hotApi.setCreateTime(new Date());
        return this.save(hotApi);
    }

    /**
     * 修改热点信息接口地
     */

    @Override
    public Boolean editHotApi(HotApiEditReq hotApiEditReq) {
        ThrowUtils.throwIf(hotApiEditReq == null, ErrorCode.PARAMS_ERROR);
        return lambdaUpdate()
                .set(ObjectUtil.isNotNull(hotApiEditReq.getApiName()), HotApi::getApiName, hotApiEditReq.getApiName())
                .set(ObjectUtil.isNotNull(hotApiEditReq.getApiURL()), HotApi::getApiURL, hotApiEditReq.getApiURL())
                .set(ObjectUtil.isNotNull(hotApiEditReq.getApiDescribe()), HotApi::getApiDescribe, hotApiEditReq.getApiDescribe())
                .set(HotApi::getUpdateTime, new Date())
                .eq(HotApi::getId, hotApiEditReq.getId())
                .update();
    }

    /**
     * 删除热点信息接口地
     */
    @Override
    public Boolean delById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return this.removeById(id);
    }

    /**
     * @param hotApi
     * @return
     */
    @Override
    public HotApiVO toHotAPIVO(HotApi hotApi) {
        HotApiVO hotApiVO = new HotApiVO();
        BeanUtils.copyProperties(hotApi, hotApiVO);
        return hotApiVO;
    }

    /**
     * 获取平台 API
     *
     * @param platform 平台
     * @return {@link HotApi }
     */
    @Override
    public HotApi getPlatformAPI(String platform) {
        ThrowUtils.throwIf(platform==null,ErrorCode.PARAMS_ERROR);
        return lambdaQuery().eq(HotApi::getPlatform,platform).one();
    }
}




