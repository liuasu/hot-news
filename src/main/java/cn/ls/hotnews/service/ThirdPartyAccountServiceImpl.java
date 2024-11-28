package cn.ls.hotnews.service;

import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.AccountCentreVO;
import cn.ls.hotnews.model.vo.ThirdPartyAccountVO;
import cn.ls.hotnews.utils.RedisUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.ls.hotnews.constant.CommonConstant.REDIS_THIRDPARTY_ACCOUNT;

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
        String key = String.format(REDIS_THIRDPARTY_ACCOUNT, loginUser.getId());
        Map<String, List<ThirdPartyAccountVO>> map = redisUtils.redisGetThirdPartyAccountByMap(key);
        for (String keyName : map.keySet()) {
            list.addAll(map.get(keyName));
        }

        return list;
    }

    /**
     * 按账户中心获取第三方账户列表
     *
     * @param loginUser 登录用户
     * @return {@link List }<{@link Map }<{@link String }, {@link List }<{@link ThirdPartyAccountVO }>>>
     */
    @Override
    public List<AccountCentreVO> getThirdPartyAccountListByAccountCentre(User loginUser) {
        List<AccountCentreVO> li=new ArrayList<>();
        String key = String.format(REDIS_THIRDPARTY_ACCOUNT, loginUser.getId());
        Map<String, List<ThirdPartyAccountVO>> map = redisUtils.redisGetThirdPartyAccountByMap(key);
        for (String keyNmae : map.keySet()) {
            List<ThirdPartyAccountVO> list = map.get(keyNmae);
            AccountCentreVO accountCentreVO = new AccountCentreVO();
            accountCentreVO.setName(keyNmae);
            accountCentreVO.setCount(list.size());
            accountCentreVO.setThirdPartyAccountVOList(list);
            li.add(accountCentreVO);
        }
        return li;
    }
}
