package cn.ls.hotnews.model.dto.aiarticlecreationlog;

import cn.ls.hotnews.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * title: AiArticleCreationLogQueryRes
 * author: liaoshuo
 * date: 2024/12/11 13:24
 * description:
 */
@Data
public class AiArticleCreationLogQueryRes extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ai平台
     */
    private String aiPlatForm;

    /**
     * 热点标题
     */
    private String hotTitle;
    /**
     * userId
     */
    private String userId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

}
