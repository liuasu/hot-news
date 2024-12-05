package cn.ls.hotnews.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.mapper.PromptMapper;
import cn.ls.hotnews.model.dto.prompt.PromptAddReq;
import cn.ls.hotnews.model.dto.prompt.PromptEditReq;
import cn.ls.hotnews.model.entity.Prompt;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.PromptVO;
import cn.ls.hotnews.service.PromptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ls
 * @description prompt(ai提示词表)的Service实现
 * @createDate 2024-12-05 11:16:10
 */
@Slf4j
@Service
public class PromptServiceImpl extends ServiceImpl<PromptMapper, Prompt> implements PromptService {
    /**
     * 将提示词转换为数据库存储格式
     * 主要处理换行符和特殊字符
     */
    public static String toDBFormat(String prompt) {
        return prompt.replace("\n", "\\n")
                .replace("\"", "\\\"")
                .replace("'", "\\'");
    }

    /**
     * 将数据库格式转换回AI使用格式
     */
    public static String toAIFormat(String dbPrompt) {
        return dbPrompt.replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\'", "'");
    }

    /**
     * 查询ai提示词列表
     */
    @Override
    public List<PromptVO> findPromptList(User loginUser) {
        Long userId = loginUser.getId();
        return lambdaQuery()
                .eq(Prompt::getUserId, userId)
                .or()
                .like(Prompt::getPromptName, "default")
                .list().stream().map(this::PromptToVO).collect(Collectors.toList());
    }

    /**
     * 添加ai提示词
     */
    @Override
    public Boolean addPrompt(PromptAddReq promptAddReq, User loginUser) {
        String promptName = promptAddReq.getPromptName();
        String promptTemplate = promptAddReq.getPromptTemplate();
        ThrowUtils.throwIf(promptName == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(promptTemplate == null, ErrorCode.PARAMS_ERROR);
        Long userId = loginUser.getId();
        Long count = lambdaQuery().eq(Prompt::getUserId, userId).count();
        ThrowUtils.throwIf(count >= 5, ErrorCode.OPERATION_ERROR, "最多可添加5份提示词");

        Prompt prompt = new Prompt();
        prompt.setPromptName(promptName);
        prompt.setPromptTemplate(toDBFormat(promptTemplate));
        prompt.setUserId(userId);
        prompt.setCreateTime(new Date());
        return this.save(prompt);
    }

    /**
     * 修改ai提示词
     */
    @Override
    public Boolean editPrompt(PromptEditReq promptEditReq, User loginUser) {
        Long id = promptEditReq.getId();
        String promptName = promptEditReq.getPromptName();
        String promptTemplate = promptEditReq.getPromptTemplate();
        Long userId = loginUser.getId();
        Prompt prompt = lambdaQuery().eq(Prompt::getId, id).eq(Prompt::getUserId, userId).one();
        ThrowUtils.throwIf(prompt == null, ErrorCode.NOT_FOUND_ERROR);

        return lambdaUpdate()
                .set(ObjectUtil.isNotNull(promptName), Prompt::getPromptName, promptName)
                .set(ObjectUtil.isNotNull(promptTemplate), Prompt::getPromptTemplate, promptTemplate)
                .set(Prompt::getUpdateTime, new Date())
                .eq(Prompt::getId, promptEditReq.getId())
                .set(Prompt::getUserId, userId)
                .update();
    }

    /**
     * 删除ai提示词
     */
    @Override
    public Boolean delById(Long id, User loginUser) {
        Long userId = loginUser.getId();
        Prompt prompt = lambdaQuery().eq(Prompt::getId, id).eq(Prompt::getUserId, userId).one();
        ThrowUtils.throwIf(prompt == null, ErrorCode.NOT_FOUND_ERROR);
        return this.removeById(prompt.getId());
    }

    /**
     * @param prompt
     * @return
     */
    @Override
    public PromptVO PromptToVO(Prompt prompt) {
        PromptVO promptVO = new PromptVO();
        promptVO.setId(prompt.getId());
        promptVO.setPromptName(prompt.getPromptName());
        promptVO.setPromptTemplate(toAIFormat(prompt.getPromptTemplate()));
        return promptVO;
    }

    /**
     * 按提示名称查询
     *
     * @param promptName
     * @param loginUser  登录用户
     * @return
     */
    @Override
    public Prompt queryByPromptName(String promptName, User loginUser) {
        Prompt prompt = lambdaQuery()
                .eq(StringUtils.isNotBlank(promptName), Prompt::getPromptName, promptName)
                .eq(Prompt::getUserId, loginUser.getId())
                .one();
        prompt.setPromptTemplate(toAIFormat(prompt.getPromptTemplate()));
        return prompt;
    }

    /**
     * 默认查询提示词
     *
     * @return {@link Prompt }
     */
    @Override
    public Prompt queryByDefault() {
        Prompt prompt = lambdaQuery().eq(Prompt::getPromptName, "default").one();
        prompt.setPromptTemplate(toAIFormat(prompt.getPromptTemplate()));
        return prompt;
    }
}




