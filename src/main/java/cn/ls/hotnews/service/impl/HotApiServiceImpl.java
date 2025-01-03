package cn.ls.hotnews.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.mapper.HotApiMapper;
import cn.ls.hotnews.model.dto.hotapi.HotApiAddReq;
import cn.ls.hotnews.model.dto.hotapi.HotApiEditReq;
import cn.ls.hotnews.model.dto.hotapi.HotApiQueryReq;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.vo.HotApiVO;
import cn.ls.hotnews.service.HotApiService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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
    public Page<HotApiVO> findHotApiList(HotApiQueryReq queryReq) {
        String platform = queryReq.getPlatform();
        String apiName = queryReq.getApiName();
        int current = queryReq.getCurrent();
        int pageSize = queryReq.getPageSize();

        Page<HotApi> hotApiPage = lambdaQuery()
                .like(StringUtils.isNotBlank(platform), HotApi::getPlatform, platform)
                .like(StringUtils.isNotBlank(apiName), HotApi::getApiName, apiName)
                .page(new Page<>(current, pageSize));
        List<HotApiVO> hotApiVOList = hotApiPage.getRecords().stream().map(this::toHotAPIVO).toList();
        Page<HotApiVO> hotApiVOPage = new Page<>(hotApiPage.getCurrent(), hotApiPage.getSize(), hotApiPage.getTotal());
        hotApiVOPage.setRecords(hotApiVOList);
        return hotApiVOPage;
    }

    /**
     * 按类型查找 HOT API 列表
     *
     * @param hotType 热型
     * @return {@link List }<{@link HotApi }>
     */
    @Override
    public List<HotApi> findHotApiByTypeList(String hotType) {
        return lambdaQuery()
                .eq(HotApi::getCodeType,hotType)
                .list();
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
        ThrowUtils.throwIf(platform == null, ErrorCode.PARAMS_ERROR);
        return lambdaQuery().eq(HotApi::getPlatform, platform).one();
    }

    /**
     * @param platform
     * @return
     */
    @Override
    public List<HotApiVO> getPlatFormByLikeRightAPI(String platform) {
        return lambdaQuery()
                .select(HotApi::getApiName, HotApi::getPlatform)
                .likeRight(HotApi::getPlatform, platform)
                .list().stream().map(this::toHotAPIVO)
                .map(hotApiVO -> {
                    String apiName = hotApiVO.getApiName().substring(2);
                    hotApiVO.setApiName(apiName);
                    return hotApiVO;
                }).collect(Collectors.toList());
    }
}




