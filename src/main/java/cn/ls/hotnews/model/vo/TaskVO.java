package cn.ls.hotnews.model.vo;

import lombok.Data;

/**
 * title: TaskVO
 * author: liaoshuo
 * date: 2024/11/23 17:33
 * description:
 */
@Data
public class TaskVO {

    /**
     * id
     */
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
     * 用户
     */
    private UserVO user;

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
     * 任务状态
     */
    private Integer taskStatus;
}
