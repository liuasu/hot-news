package cn.ls.hotnews.service;

import cn.ls.hotnews.enums.AccountPlatformEnum;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.ThirdPartyAccountVO;
import cn.ls.hotnews.utils.RedisUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static cn.ls.hotnews.constant.UserConstant.REDIS_THIRD_PARTY_ACCOUNT;

/**
 * title: ThirdPartyAccountServiceImpl
 * author: liaoshuo
 * date: 2024/11/23 20:47
 * description:
 */
@Service
public class ThirdPartyAccountServiceImpl implements ThirdPartyAccountService {

    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取第三方账户列表
     *
     * @param loginUser 登录用户
     * @return {@link List }<{@link ThirdPartyAccountVO }>
     */
    @Override
    public List<ThirdPartyAccountVO> getThirdPartyAccountList(User loginUser) {
        List<ThirdPartyAccountVO> list=new ArrayList<>();
        String key = String.format(REDIS_THIRD_PARTY_ACCOUNT, AccountPlatformEnum.TOUTIAO.getPlatform(),loginUser.getId());
        ThirdPartyAccountVO touTiao = redisUtils.redisGetThirdPartyAccount(key);
        list.add(touTiao);
        return list;
    }
}
