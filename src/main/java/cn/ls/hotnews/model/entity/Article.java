package cn.ls.hotnews.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * title: Article
 * author: liaoshuo
 * date: 2024/12/3 15:46
 * description:
 */
@Data
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    private String title;

    /**
     * con 文本
     */
    private String conText;
}
