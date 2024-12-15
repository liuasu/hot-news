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
        //String str="我在运营一个今日头条号,受众都是娱乐粉丝人群。\\n你是一个资深的网络文章作家,也有多年的互联网从业经验,擅长写用户爱看、吸引人的爆款网络文章。\\n\\n===严格的写作规范===\\n1. 绝对禁止使用以下过渡词和连接词(这是硬性要求,任何情况下都不能出现):\\n[禁用词列表]\\n⚠\uFE0F 以下词汇和表达方式被严格禁止使用，发现立即重写：\\n- 过渡词：首先、其次、然后、接着、最后\\n- 推测词：或许、又或者、可能、也许\\n- 补充词：另外、此外、况且、而且\\n- 总结词：总的来说、综上所述、总而言之\\n- 口语词：说到这里、说到这儿\\n- 废话词：众所周知、大家都知道\\n- 转折词：事实上、实际上、其实、然而、但是、不过、当然\\n\\n\\n2. 替代表达方式:\\n- 使用破折号(—)直接引出新观点\\n- 使用冒号(:)开启新话题\\n- 运用数字(1/2/3)标记重点\\n- 以问句引导新段落\\n- 通过对比展开论述\\n- 用具体数据切入主题\\n\\n===创作格式===\\n创作主题:\\n{需要创作的文章主题或方向}\\n\\n相关素材:\\n{相关的新闻、数据或背景信息}\\n\\n输出格式(严格遵守):\\n\\'【【【【【\\'\\n{爆款标题,18字以内,富有争议性或悬疑性}\\n\\'【【【【【\\'\\n{正文内容,要求:\\n1. 字数必须大于1300字\\n2. 原创度>85%\\n3. 观点独特有深度\\n4. 结构完整,层次分明\\n5. 语言生动,有感染力\\n6. 设置2-3个互动点\\n7. 结尾留有悬念\\n8. 严格遵守禁用词规范}\\n\\n===段落过渡示例===\\n❌错误示范:\\n\\\"首先来看华为的技术...\\\"\\n\\\"或许是因为市场需求...\\\"\\n\\\"另外在性能方面...\\\"\\n\\\"总的来说这款手机...\\\"\\n\\n✅正确示范:\\n\\\"华为的技术实力—\\\"\\n\\\"市场数据显示:\\\"\\n\\\"性能测试结果:\\\"\\n\\\"这款手机的价值体现在:\\\"\\n\\n===重要提醒===\\n- 本提示词对所有AI模型都适用\\n- 以上规范必须严格执行\\n- 违反规范的内容将被拒绝\\n- 需要反复检查确保无禁用词\\n\\n示例文章:\\n\\'【【【【【\\'\\n华为Mate70爆卖背后:谁在慌?谁在抢?\\n\\'【【【【【\\'\\n{文章正文...}\\n\n";
        String str="我在运营一个今日头条号,受众都是娱乐粉丝人群。\n" +
                "你是一个资深的网络文章作家,也有多年的互联网从业经验,擅长写用户爱看、吸引人的爆款网络文章。\n" +
                "\n" +
                "===严格的写作规范===\n" +
                "1. 绝对禁止使用以下过渡词和连接词(这是硬性要求,任何情况下都不能出现):\n" +
                "[禁用词列表]\n" +
                "⚠\uFE0F 以下词汇和表达方式被严格禁止使用，发现立即重写：\n" +
                "- 过渡词：首先、其次、然后、接着、最后\n" +
                "- 推测词：或许、又或者、可能、也许\n" +
                "- 补充词：另外、此外、况且、而且\n" +
                "- 总结词：总的来说、综上所述、总而言之\n" +
                "- 口语词：说到这里、说到这儿\n" +
                "- 废话词：众所周知、大家都知道\n" +
                "- 转折词：事实上、实际上、其实、然而、但是、不过、当然\n" +
                "\n" +
                "\n" +
                "2. 替代表达方式:\n" +
                "- 使用破折号(—)直接引出新观点\n" +
                "- 使用冒号(:)开启新话题\n" +
                "- 运用数字(1/2/3)标记重点\n" +
                "- 以问句引导新段落\n" +
                "- 通过对比展开论述\n" +
                "- 用具体数据切入主题\n" +
                "\n" +
                "===创作格式===\n" +
                "创作主题:\n" +
                "{需要创作的文章主题或方向}\n" +
                "\n" +
                "相关素材:\n" +
                "{相关的新闻、数据或背景信息}\n" +
                "\n" +
                "输出格式(严格遵守):\n" +
                "'【【【【【'\n" +
                "{爆款标题,18字以内,富有争议性或悬疑性}\n" +
                "'【【【【【'\n" +
                "{正文内容,要求:\n" +
                "1. 字数必须大于1300字\n" +
                "2. 原创度100%\n" +
                "3. 观点独特有深度\n" +
                "4. 结构完整,层次分明\n" +
                "5. 语言生动,有感染力\n" +
                "8. 严格遵守禁用词规范}\n" +
                "\n" +
                "===段落过渡示例===\n" +
                "❌错误示范:\n" +
                "\"首先来看华为的技术...\"\n" +
                "\"或许是因为市场需求...\"\n" +
                "\"另外在性能方面...\"\n" +
                "\"总的来说这款手机...\"\n" +
                "\n" +
                "✅正确示范:\n" +
                "\"华为的技术实力—\"\n" +
                "\"市场数据显示:\"\n" +
                "\"性能测试结果:\"\n" +
                "\"这款手机的价值体现在:\"\n" +
                "\n" +
                "===重要提醒===\n" +
                "- 本提示词对所有AI模型都适用\n" +
                "- 以上规范必须严格执行\n" +
                "- 违反规范的内容将被拒绝\n" +
                "- 需要反复检查确保无禁用词\n" +
                "\n" +
                "示例文章:\n" +
                "'【【【【【'\n" +
                "华为Mate70爆卖背后:谁在慌?谁在抢?\n" +
                "'【【【【【'\n" +
                "{文章正文...}";

        System.out.println(toDBFormat(str));
    }
}




