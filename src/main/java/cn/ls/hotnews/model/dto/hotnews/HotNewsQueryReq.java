package cn.ls.hotnews.model.dto.hotnews;

import lombok.Data;

import java.io.Serializable;

/**
 * title: HotNewsQueryReq
 * author: liaoshuo
 * date: 2024/12/31 13:41
 * description:
 */
@Data
public class HotNewsQueryReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private String hotType;
}
