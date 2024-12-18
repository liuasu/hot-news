package cn.ls.hotnews.service;

import cn.ls.hotnews.model.dto.aiconfig.AiConfigAddReq;
import cn.ls.hotnews.model.dto.aiconfig.AiConfigEditReq;
import cn.ls.hotnews.model.entity.AiConfig;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.AiConfigVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * ai 秘钥配Service
 *
 * @author ls
 * @createDate 2024-12-05 11:16:10
 */
public interface AiConfigService extends IService<AiConfig> {

    /**
     * 查询ai 秘钥配列表
     */
    List<AiConfigVO> findAiConfigList(User loginUser);

    /**
     * 添加ai 秘钥配
     */
    Boolean addAiConfig(AiConfigAddReq aiConfigAddReq, User loginUser);
    Boolean addAiConfigList(List<AiConfig> aiConfigs);

    /**
     * 修改ai 秘钥配
     */
    Boolean editAiConfig(AiConfigEditReq aiConfigEditReq, User loginUser);

    /**
     * 删除ai 秘钥配
     */
    Boolean delById(Long id, User loginUser);

    AiConfigVO AIConfigToVO(AiConfig aiConfig);

    /**
     * 通过userid 和 平台 来查询
     *
     * @return {@link AiConfig }
     */
    AiConfig getAiConfigByUserIdInPlatForm(Long userId, Integer PlatForm);

}
