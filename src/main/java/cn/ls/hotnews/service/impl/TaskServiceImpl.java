package cn.ls.hotnews.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.ls.hotnews.common.ErrorCode;
import cn.ls.hotnews.enums.HotPlatformEnum;
import cn.ls.hotnews.exception.ThrowUtils;
import cn.ls.hotnews.mapper.TaskMapper;
import cn.ls.hotnews.model.dto.task.TaskAddReq;
import cn.ls.hotnews.model.dto.task.TaskEditReq;
import cn.ls.hotnews.model.dto.task.TaskQueryReq;
import cn.ls.hotnews.model.entity.Task;
import cn.ls.hotnews.model.entity.User;
import cn.ls.hotnews.model.vo.TaskVO;
import cn.ls.hotnews.service.TaskService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static cn.ls.hotnews.constant.CommonConstant.ONE;
import static cn.ls.hotnews.constant.CommonConstant.ZERO;

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
    public Page<TaskVO> findTaskList(TaskQueryReq taskQueryReq, User loginUser) {
        Page<Task> page = lambdaQuery()
                .eq(StringUtils.isNotBlank(taskQueryReq.getPlatForm()), Task::getPlatFormAccount, taskQueryReq.getPlatForm())
                .eq(Task::getUserId, loginUser.getId())
                .eq(ObjectUtil.isNotNull(taskQueryReq.getTaskStatus()), Task::getTaskStatus, taskQueryReq.getTaskStatus())
                .eq(StringUtils.isNotBlank(taskQueryReq.getPlatForm()), Task::getPlatForm, taskQueryReq.getPlatForm())
                .orderByDesc(Task::getCreateTime)
                .page(new Page<>(taskQueryReq.getCurrent(), taskQueryReq.getPageSize()));

        List<TaskVO> collect = page.getRecords().stream().map(this::taskToVO).collect(Collectors.toList());
        Page<TaskVO> taskVOPage = new Page<>(page.getCurrent(), page.getSize());
        taskVOPage.setRecords(collect);
        taskVOPage.setTotal(page.getTotal());
        return taskVOPage;
    }

    /**
     * 添加任务中心
     */
    @Override
    public Boolean addTask(TaskAddReq taskAddReq, User loginUser) {
        ThrowUtils.throwIf(taskAddReq == null, ErrorCode.PARAMS_ERROR);

        Task one = lambdaQuery().eq(Task::getHotUrl, taskAddReq.getHotUrl()).eq(Task::getUserId, loginUser.getId()).one();
        ThrowUtils.throwIf(one != null, ErrorCode.NOT_FOUND_ERROR, "该热点已配置，请先删除在进行配置");

        Task task = new Task();
        BeanUtils.copyProperties(taskAddReq, task);
        HotPlatformEnum valuesByName = HotPlatformEnum.getValuesByName(taskAddReq.getHotPlatForm());
        ThrowUtils.throwIf(valuesByName == null, ErrorCode.NOT_FOUND_ERROR);
        task.setHotPlatForm(valuesByName.getValues());
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
    public Boolean editTask(TaskEditReq taskEditReq, User loginUser) {
        Task task = lambdaQuery().eq(Task::getId, taskEditReq.getId())
                .eq(Task::getUserId, loginUser.getId())
                .one();
        ThrowUtils.throwIf(task == null, ErrorCode.NOT_FOUND_ERROR);
        return lambdaUpdate()
                .set(StringUtils.isNotBlank(taskEditReq.getPlatFormAccount()), Task::getPlatFormAccount, taskEditReq.getPlatFormAccount())
                .set(StringUtils.isNotBlank(taskEditReq.getPlatForm()), Task::getPlatForm, taskEditReq.getPlatForm())
                .set(Task::getUpdateTime, new Date())
                .eq(Task::getId, taskEditReq.getId())
                .eq(Task::getUserId, loginUser.getId())
                .update();
    }

    /**
     * @param task
     * @return
     */
    @Override
    public Boolean editTask(Task task) {
        Long id = task.getId();
        Long userId = task.getUserId();
        Task taskById = lambdaQuery().eq(Task::getId, id).eq(Task::getUserId, userId).eq(Task::getTaskStatus, ZERO).eq(Task::getTaskType, ZERO).one();
        ThrowUtils.throwIf(taskById == null, ErrorCode.NOT_FOUND_ERROR);
        return lambdaUpdate().set(Task::getTaskStatus, ONE).set(Task::getUpdateTime, new Date()).eq(Task::getId, id).eq(Task::getUserId, userId).update();
    }

    /**
     * 删除任务中心
     */
    @Override
    public Boolean delById(Long id, User loginUser) {
        ThrowUtils.throwIf(id == null || id < 0, ErrorCode.PARAMS_ERROR);
        Task task = lambdaQuery().eq(Task::getId, id)
                .eq(Task::getUserId, loginUser.getId())
                .one();
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




