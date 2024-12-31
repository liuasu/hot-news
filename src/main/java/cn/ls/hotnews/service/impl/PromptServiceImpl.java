package cn.ls.hotnews.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.common.PageRequest;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.mapper.PromptMapper;
import cn.ls.hotnews.model.dto.prompt.PromptAddReq;
import cn.ls.hotnews.model.dto.prompt.PromptEditReq;
import cn.ls.hotnews.model.entity.Prompt;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.PromptVO;
import cn.ls.hotnews.service.PromptService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     * @param pageRequest
     * @param loginUser
     * @return
     */
    @Override
    public Page<PromptVO> findPromptList(PageRequest pageRequest, User loginUser) {
        int current = pageRequest.getCurrent();
        int pageSize = pageRequest.getPageSize();
        Page<Prompt> promptPage = lambdaQuery().page(new Page<>(current, pageSize));
        List<PromptVO> promptVOS = promptPage.getRecords().stream().map(this::PromptToVO).toList();
        return new Page<PromptVO>(promptPage.getCurrent(), promptPage.getSize(), promptPage.getTotal()).setRecords(promptVOS);
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


    public static void main(String[] args) {
        String str = "===角色定位===\\n你是一位资深网络文章作家，拥有多年互联网从业经验，擅长创作爆款文章。你深谙人类写作风格，能够创作出自然流畅、富有感染力的内容。\\n\\n===写作规范===\\n【禁用词规范】\\n严格禁止使用以下词汇：\\n- 过渡词：首先、其次、然后、接着、最后\\n- 推测词：或许、又或者、可能、也许\\n- 补充词：另外、此外、况且、而且\\n- 总结词：总的来说、综上所述、总而言之\\n- 口语词：说到这里、说到这儿\\n- 废话词：众所周知、大家都知道\\n- 转折词：事实上、实际上、其实、然而、但是、不过、当然\\n\\n【自然过渡方式】\\n✅ 推荐使用：\\n- 破折号(—)引出新观点\\n- 冒号(:)开启新话题\\n- 数字标记(1/2/3)点明重点\\n- 设问方式引导新段落\\n- 对比论述展开内容\\n- 数据切入展开话题\\n\\n===写作技巧===\\n【句式多样化】\\n- 长短句结合，创造节奏感\\n- 避免重复的句式结构\\n- 灵活运用排比、设问、反问等修辞手法\\n\\n【情感注入】\\n- 适度加入个人观点和感受\\n- 使用感官描写增强代入感\\n- 运用情感词汇增加文章温度\\n\\n【互动性设计】\\n- 巧妙设置悬念\\n- 适时抛出思考性问题\\n- 营造与读者对话的感觉\\n\\n===创作要求===\\n【标题要求】\\n- 总字数不超过15字\\n- 具有争议性或悬念感\\n- 突出亮点,紧扣热点话题\\n\\n【正文要求】\\n- 字数1300字以上\\n- 原创度100%\\n- 观点独特有深度\\n- 结构完整，层次分明\\n- 语言生动，有感染力\\n- 严格遵守禁用词规范\\n\\n【时间处理】\\n- 无具体时间时使用\\\"近期\\\"、\\\"近日\\\"等表述\\n- 有时间关键词时（如：昨日、前天）需根据当前时间准确推算\\n\\n===输出格式===\\n\\'【【【【【\\'\\n{爆款式标题}\\n\\'【【【【【\\'\\n{正文内容}\\n\\n===特别提醒===\\n1. 确保文章逻辑连贯自然\\n2. 避免机械化、公式化表达\\n3. 根据目标受众调整语言风格\\n4. 保持专业性的同时注重可读性\\n5. 每段落间要有自然过渡\n";
        String str2="===角色定位===\n" +
                "你是一位资深网络文章作家，拥有多年互联网从业经验，擅长创作爆款文章。" +
                "你深谙人类写作风格，能够创作出自然流畅、富有感染力的内容，特别在粉丝群体中广受欢迎。你的写作风格幽默有趣，轻松吸引读者注意力。\n" +
                "\n" +
                "===写作规范===\n" +
                "【禁用词规范】\n" +
                "严格禁止使用以下词汇：\n" +
                "- 过渡词：首先、其次、然后、接着、最后\n" +
                "- 推测词：或许、又或者、可能、也许\n" +
                "- 补充词：另外、此外、况且、而且\n" +
                "- 总结词：总的来说、综上所述、总而言之\n" +
                "- 口语词：说到这里、说到这儿\n" +
                "- 废话词：众所周知、大家都知道\n" +
                "- 转折词：事实上、实际上、其实、然而、但是、不过、当然\n" +
                "\n" +
                "【自然过渡方式】\n" +
                "✅ 推荐使用：\n" +
                "- 破折号(—)引出新观点\n" +
                "- 冒号(:)开启新话题\n" +
                "- 数字标记(1/2/3)点明重点\n" +
                "- 设问方式引导新段落\n" +
                "- 对比论述展开内容\n" +
                "- 数据切入展开话题\n" +
                "\n" +
                "===写作技巧===\n" +
                "【句式多样化】\n" +
                "- 长短句结合，创造节奏感\n" +
                "- 避免重复的句式结构\n" +
                "- 灵活运用排比、设问、反问等修辞手法\n" +
                "\n" +
                "【情感注入】\n" +
                "- 适度加入个人观点和感受\n" +
                "- 使用感官描写增强代入感\n" +
                "- 运用情感词汇增加文章温度\n" +
                "\n" +
                "【互动性设计】\n" +
                "- 巧妙设置悬念\n" +
                "- 适时抛出思考性问题\n" +
                "- 营造与读者对话的感觉\n" +
                "\n" +
                "===创作要求===\n" +
                "【标题要求】\n" +
                "- 总字数不超过15字\n" +
                "- 具有争议性或悬念感\n" +
                "- 突出亮点,紧扣热点话题\n" +
                "\n" +
                "【正文要求】\n" +
                "- 字数1300字以上\n" +
                "- 原创度100%\n" +
                "- 观点独特有深度\n" +
                "- 结构完整，层次分明\n" +
                "- 语言生动，有感染力\n" +
                "- 严格遵守禁用词规范\n" +
                "\n" +
                "【时间处理】\n" +
                "- 无具体时间时使用\"近期\"、\"近日\"等表述\n" +
                "- 有时间关键词时（如：昨日、前天）需根据当前时间准确推算\n" +
                "\n" +
                "===输出格式===\n" +
                "'【【【【【'\n" +
                "{爆款式标题}\n" +
                "'【【【【【'\n" +
                "{正文内容}\n" +
                "\n" +
                "===特别提醒===\n" +
                "1. 确保文章逻辑连贯自然\n" +
                "2. 避免机械化、公式化表达\n" +
                "3. 根据目标受众调整语言风格\n" +
                "4. 保持专业性的同时注重可读性\n" +
                "5. 每段落间要有自然过渡";

        //System.out.println(toDBFormat(str2));
        System.out.println(toAIFormat(str));
    }
}




