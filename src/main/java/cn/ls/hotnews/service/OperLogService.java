package cn.ls.hotnews.service;

import cn.ls.hotnews.model.entity.OperLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
    List<OperLog> findOperLogList(OperLog operLog);

    /**
    *  添加操作日志记
    */
    Boolean addOperLog(OperLog operLog);
}
