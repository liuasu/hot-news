package cn.ls.hotnews.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.ls.hotnews.mapper.AiArticleCreationLogMapper;
import cn.ls.hotnews.model.dto.aiarticlecreationlog.AiArticleCreationLogQueryRes;
import cn.ls.hotnews.model.entity.AiArticleCreationLog;
import cn.ls.hotnews.model.vo.AiArticleCreationLogVO;
import cn.ls.hotnews.service.AiArticleCreationLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author ls
 * @description ai_article_creation_log(ai文章生成记录)的Service实现
 * @createDate 2024-12-11 13:11:18
 */
@Slf4j
@Service
public class AiArticleCreationLogServiceImpl extends ServiceImpl<AiArticleCreationLogMapper, AiArticleCreationLog> implements AiArticleCreationLogService {
    /**
     * 查询ai文章生成记列表
     */
    @Override
    public Page<AiArticleCreationLogVO> findAiArticleCreationLogList(AiArticleCreationLogQueryRes aiArticleCreationLog) {
        String aiPlatForm = aiArticleCreationLog.getAiPlatForm();
        String hotTitle = aiArticleCreationLog.getHotTitle();
        String userId = aiArticleCreationLog.getUserId();
        Date startTime = aiArticleCreationLog.getStartTime();
        Date endTime = aiArticleCreationLog.getEndTime();
        int current = aiArticleCreationLog.getCurrent();
        int pageSize = aiArticleCreationLog.getPageSize();
        Page<AiArticleCreationLog> page = lambdaQuery()
                .eq(StringUtils.isNotBlank(aiPlatForm), AiArticleCreationLog::getAiPlatForm, aiPlatForm)
                .eq(StringUtils.isNotBlank(hotTitle), AiArticleCreationLog::getHotTitle, hotTitle)
                .eq(StringUtils.isNotBlank(userId), AiArticleCreationLog::getUserId, userId)
                .between(!ObjectUtil.isAllNotEmpty(startTime, endTime), AiArticleCreationLog::getCreateTime, startTime, endTime)
                .page(new Page<>(pageSize, current));
        List<AiArticleCreationLogVO> voList = page.getRecords().stream().map(this::thisToVO).toList();
        return new Page<AiArticleCreationLogVO>(page.getCurrent(), page.getSize(), page.getTotal()).setRecords(voList);
    }

    /**
     * 添加ai文章生成记
     */
    @Override
    public Boolean addAiArticleCreationLog(AiArticleCreationLog aiArticleCreationLog) {
        aiArticleCreationLog.setCreateTime(new Date());
        return this.save(aiArticleCreationLog);
    }


    /**
     * @param articleCreationLog
     * @return
     */
    @Override
    public AiArticleCreationLogVO thisToVO(AiArticleCreationLog articleCreationLog) {
        AiArticleCreationLogVO aiArticleCreationLogVO = new AiArticleCreationLogVO();
        BeanUtils.copyProperties(articleCreationLog, aiArticleCreationLogVO);
        return aiArticleCreationLogVO;
    }
}




