package cn.ls.hotnews.service;

import cn.ls.hotnews.model.dto.log.LogQueryRes;
import cn.ls.hotnews.model.entity.OperLog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
*
* 操作日志记Service
* @createDate 2024-12-11 15:04:25
* @author ls
*/
public interface OperLogService extends IService<OperLog> {

    /**
    * 查询操作日志记列表
    */
    Page<OperLog> findOperLogList(LogQueryRes operLog);

    /**
    *  添加操作日志记
    */
    Boolean addOperLog(OperLog operLog);
}
