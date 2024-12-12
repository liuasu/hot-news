package cn.ls.hotnews.model.dto.log;

import cn.ls.hotnews.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * title: LogQueryRes
 * author: liaoshuo
 * date: 2024/12/12 13:23
 * description:
 */
@Data
public class LogQueryRes extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userName;

    private String startTime;

    private String endTime;

}
