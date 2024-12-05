package cn.ls.hotnews.service;

import cn.ls.hotnews.model.dto.prompt.PromptAddReq;
import cn.ls.hotnews.model.dto.prompt.PromptEditReq;
import cn.ls.hotnews.model.entity.Prompt;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.PromptVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * ai提示词Service
 *
 * @author ls
 * @createDate 2024-12-05 11:16:10
 */
public interface PromptService extends IService<Prompt> {

    /**
     * 查询ai提示词列表
     */
    List<PromptVO> findPromptList(User loginUser);

    /**
     * 添加ai提示词
     */
    Boolean addPrompt(PromptAddReq promptAddReq, User loginUser);

    /**
     * 修改ai提示词
     */
    Boolean editPrompt(PromptEditReq promptEditReq,User loginUser);

    /**
     * 删除ai提示词
     */
    Boolean delById(Long id,User loginUser);

    PromptVO PromptToVO(Prompt prompt);

    /**
     * 按提示名称查询（获取用的指定的提示词）
     *
     * @param promptName 提示名称
     * @param loginUser  登录用户
     * @return {@link Prompt }
     */
    Prompt queryByPromptName(String promptName,User loginUser);

    /**
     * 默认查询提示词
     *
     * @return {@link Prompt }
     */
    Prompt queryByDefault();

}
