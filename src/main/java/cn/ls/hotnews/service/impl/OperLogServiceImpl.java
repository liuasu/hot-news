package cn.ls.hotnews.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.ls.hotnews.mapper.OperLogMapper;
import cn.ls.hotnews.model.entity.OperLog;
import cn.ls.hotnews.service.OperLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
*
* @description oper_log(操作日志记录)的Service实现
* @createDate 2024-12-11 15:04:25
* @author ls
*/
@Slf4j
@Service
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, OperLog> implements OperLogService {
    /**
    * 查询操作日志记列表
    */
    @Override
    public List<OperLog> findOperLogList(OperLog operLog){
        return  lambdaQuery()
                .eq(ObjectUtil.isNotNull(operLog.getStatus()),OperLog::getStatus,operLog.getStatus())
                .list();
    }

    /**
    *  添加操作日志记
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean addOperLog(OperLog operLog){
        return this.save(operLog);
    }
}




