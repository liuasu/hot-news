package cn.ls.hotnews.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.mapper.TaskMapper;
import cn.ls.hotnews.model.dto.task.TaskAddReq;
import cn.ls.hotnews.model.dto.task.TaskEditReq;
import cn.ls.hotnews.model.dto.task.TaskQueryReq;
import cn.ls.hotnews.model.entity.Task;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.TaskVO;
import cn.ls.hotnews.service.TaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.ls.hotnews.constant.CommonConstant.ZERO;
import static cn.ls.hotnews.constant.UserConstant.ADMIN_ROLE;

/**
 * @author ls
 * @description task(任务中心表)的Service实现
 * @createDate 2024-11-23 17:27:51
 */
@Slf4j
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    /**
     * 查询任务中心列表
     */
    @Override
    public List<TaskVO> findTaskList(TaskQueryReq taskQueryReq,User loginUser) {
        return lambdaQuery()
                .eq(StringUtils.isNotBlank(taskQueryReq.getPlatFormAccount()),
                        Task::getPlatFormAccount,
                        taskQueryReq.getPlatFormAccount()
                ).eq(!Objects.equals(loginUser.getUserRole(), ADMIN_ROLE),Task::getUserId,loginUser.getId())
                .orderByDesc(Task::getCreateTime)
                .list()
                .stream()
                .map(this::taskToVO)
                .collect(Collectors.toList());
    }

    /**
     * 添加任务中心
     */
    @Override
    public Boolean addTask(TaskAddReq taskAddReq, User loginUser) {
        ThrowUtils.throwIf(taskAddReq == null, ErrorCode.PARAMS_ERROR);
        Task task = new Task();
        BeanUtils.copyProperties(taskAddReq, task);
        task.setUserId(loginUser.getId());
        task.setTaskStatus(ZERO);
        task.setTaskType(ZERO);
        task.setCreateTime(new Date());
        return this.save(task);
    }

    /**
     * 修改任务中心
     */
    @Override
    public Boolean editTask(TaskEditReq taskEditReq) {
        return lambdaUpdate()
                .set(ObjectUtil.isNotNull(taskEditReq.getTaskStatus()), Task::getTaskStatus, taskEditReq.getTaskStatus())
                .set(Task::getUpdateTime, new Date())
                .eq(Task::getId, taskEditReq.getId())
                .update();
    }

    /**
     * 删除任务中心
     */
    @Override
    public Boolean delById(Long id) {
        ThrowUtils.throwIf(id == null || id < 0, ErrorCode.PARAMS_ERROR);
        Task task = this.getById(id);
        ThrowUtils.throwIf(task == null, ErrorCode.NOT_FOUND_ERROR);
        return this.removeById(id);
    }

    /**
     * @param task
     * @return
     */
    @Override
    public TaskVO taskToVO(Task task) {
        TaskVO taskVO = new TaskVO();
        BeanUtil.copyProperties(task, taskVO);
        return taskVO;
    }
}



