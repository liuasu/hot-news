package cn.ls.hotnews.service.impl;

import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.enums.AIPlatFormEnum;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.mapper.AiConfigMapper;
import cn.ls.hotnews.model.dto.aiconfig.AiConfigAddReq;
import cn.ls.hotnews.model.dto.aiconfig.AiConfigEditReq;
import cn.ls.hotnews.model.entity.AiConfig;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.AiConfigVO;
import cn.ls.hotnews.service.AiConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ls
 * @description ai_config(ai 秘钥配置)的Service实现
 * @createDate 2024-12-05 11:16:10
 */
@Slf4j
@Service
public class AiConfigServiceImpl extends ServiceImpl<AiConfigMapper, AiConfig> implements AiConfigService {
    /**
     * 查询ai 秘钥配列表
     */
    @Override
    public List<AiConfigVO> findAiConfigList(User loginUser) {
        Long userId = loginUser.getId();
        return lambdaQuery()
                .eq(AiConfig::getUserId, userId)
                .list().stream().map(this::AIConfigToVO).collect(Collectors.toList());
    }

    /**
     * 添加ai 秘钥配
     */
    @Override
    public Boolean addAiConfig(AiConfigAddReq aiConfigAddReq, User loginUser) {
        String appId = aiConfigAddReq.getAppId();
        String apiKey = aiConfigAddReq.getApiKey();
        String apiSecret = aiConfigAddReq.getApiSecret();
        String aiPlatForm = aiConfigAddReq.getAiPlatForm();
        Long userId = loginUser.getId();
        Integer values = Objects.requireNonNull(AIPlatFormEnum.getValuesByName(aiPlatForm)).getValues();
        AiConfig config = lambdaQuery()
                .eq(AiConfig::getUserId, userId)
                .eq(StringUtils.isNotBlank(appId), AiConfig::getAppId, appId)
                .eq(StringUtils.isNotBlank(apiKey), AiConfig::getApiKey, apiKey)
                .eq(StringUtils.isNotBlank(apiSecret), AiConfig::getApiSecret, apiSecret)
                .eq(StringUtils.isNotBlank(aiPlatForm), AiConfig::getAiPlatForm, values).one();
        ThrowUtils.throwIf(config != null, ErrorCode.OPERATION_ERROR, "请勿重复添加");
        AiConfig aiConfig = new AiConfig();
        BeanUtils.copyProperties(aiConfigAddReq, aiConfig);
        aiConfig.setAiPlatForm(values);
        aiConfig.setUserId(userId);
        aiConfig.setCreateTime(new Date());
        return this.save(aiConfig);
    }

    /**
     * 修改ai 秘钥配
     */
    @Override
    public Boolean editAiConfig(AiConfigEditReq aiConfigEditReq, User loginUser) {
        Long id = aiConfigEditReq.getId();
        String appId = aiConfigEditReq.getAppId();
        String apiKey = aiConfigEditReq.getApiKey();
        String apiSecret = aiConfigEditReq.getApiSecret();
        String aiPlatForm = aiConfigEditReq.getAiPlatForm();
        Long userId = loginUser.getId();

        Integer values = Objects.requireNonNull(AIPlatFormEnum.getValuesByName(aiPlatForm)).getValues();
        AiConfig config = lambdaQuery()
                .eq(AiConfig::getId, id)
                .eq(AiConfig::getUserId, userId)
                .eq(StringUtils.isNotBlank(appId), AiConfig::getAppId, appId)
                .eq(StringUtils.isNotBlank(apiKey), AiConfig::getApiKey, apiKey)
                .eq(StringUtils.isNotBlank(apiSecret), AiConfig::getApiSecret, apiSecret)
                .eq(StringUtils.isNotBlank(aiPlatForm), AiConfig::getAiPlatForm, values).one();
        ThrowUtils.throwIf(config == null, ErrorCode.NOT_FOUND_ERROR);

        return lambdaUpdate()
                .set(StringUtils.isNotBlank(appId), AiConfig::getAppId, appId)
                .set(StringUtils.isNotBlank(apiKey), AiConfig::getApiKey, apiKey)
                .set(StringUtils.isNotBlank(apiSecret), AiConfig::getApiSecret, apiSecret)
                .set(AiConfig::getUpdateTime, new Date())
                .eq(AiConfig::getId, aiConfigEditReq.getId())
                .eq(AiConfig::getUserId, userId)
                .update();
    }

    /**
     * 删除ai 秘钥配
     */
    @Override
    public Boolean delById(Long id, User loginUser) {
        Long userId = loginUser.getId();
        AiConfig config = lambdaQuery()
                .eq(AiConfig::getId, id)
                .eq(AiConfig::getUserId, userId)
                .one();
        ThrowUtils.throwIf(config == null, ErrorCode.NOT_FOUND_ERROR);
        return this.removeById(id);
    }

    /**
     * @param aiConfig
     * @return
     */
    @Override
    public AiConfigVO AIConfigToVO(AiConfig aiConfig) {
        AiConfigVO aiConfigVO = new AiConfigVO();
        BeanUtils.copyProperties(aiConfig, aiConfigVO);
        String naem = Objects.requireNonNull(AIPlatFormEnum.getNameByValues(aiConfig.getAiPlatForm())).getName();
        aiConfigVO.setAiPlatForm(naem);
        return aiConfigVO;
    }

    /**
     * 通过userid 和 平台 来查询
     *
     * @param userId
     * @param PlatForm
     * @return {@link AiConfig }
     */
    @Override
    public AiConfig getAiConfigByUserIdInPlatForm(Long userId, Integer PlatForm) {
        return lambdaQuery()
                .eq(AiConfig::getUserId, userId)
                .eq(AiConfig::getAiPlatForm, PlatForm)
                .one();
    }
}




