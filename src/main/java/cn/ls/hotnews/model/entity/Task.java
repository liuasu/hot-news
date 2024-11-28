package cn.ls.hotnews.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务中心表
 *
 * @TableName task
 */
@TableName(value = "task")
@Data
public class Task implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 第三方账号
     */
    private String platFormAccount;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 热点标题
     */
    private String hotNewTitle;
    /**
     * 热点url
     */
    private String hotUrl;
    /**
     * 热点平台
     */
    private String hotPlatForm;
    /**
     * 任务状态 0-已配置 1-已生产
     */
    private Integer taskStatus;
    /**
     * 任务类型 0-手动配置 1-托管
     */
    private Integer taskType;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}