package cn.ls.hotnews.service;

import cn.ls.hotnews.model.dto.task.TaskAddReq;
import cn.ls.hotnews.model.dto.task.TaskEditReq;
import cn.ls.hotnews.model.dto.task.TaskQueryReq;
import cn.ls.hotnews.model.entity.Task;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.TaskVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
*
* 任务中心Service
* @createDate 2024-11-23 17:27:51
* @author ls
*/
public interface TaskService extends IService<Task> {

    /**
    * 查询任务中心列表
    */
    Page<TaskVO> findTaskList(TaskQueryReq taskQueryReq, User loginUser);

    /**
    *  添加任务中心
    */
    Boolean addTask(TaskAddReq taskAddReq, User loginUser);

    /**
    *  修改任务中心
    */
    Boolean editTask(TaskEditReq taskEditReq);

    /**
    *  删除任务中心
    */
    Boolean delById(Long id);

    TaskVO taskToVO(Task task);

}
