package cn.ls.hotnews.service.impl;

import cn.ls.hotnews.mapper.OperLogMapper;
import cn.ls.hotnews.model.dto.log.LogQueryRes;
import cn.ls.hotnews.model.entity.OperLog;
import cn.ls.hotnews.service.OperLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ls
 * @description oper_log(操作日志记录)的Service实现
 * @createDate 2024-12-11 15:04:25
 */
@Slf4j
@Service
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, OperLog> implements OperLogService {
    /**
     * 查询操作日志记列表
     */
    @Override
    public Page<OperLog> findOperLogList(LogQueryRes operLog) {
        String userName = operLog.getUserName();
        String startTime = operLog.getStartTime();
        String endTime = operLog.getEndTime();
        int current = operLog.getCurrent();
        int pageSize = operLog.getPageSize();
        return lambdaQuery()
                .like(StringUtils.isNotBlank(userName), OperLog::getOperUser, userName)
                .between(StringUtils.isNoneBlank(startTime, endTime), OperLog::getOperTime, startTime, endTime)
                .orderByDesc(OperLog::getOperTime)
                .page(new Page<>(current, pageSize));
    }

    /**
     * 添加操作日志记
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean addOperLog(OperLog operLog) {
        return this.save(operLog);
    }
}




