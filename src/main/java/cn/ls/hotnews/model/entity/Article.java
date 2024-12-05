package cn.ls.hotnews.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

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

    /**
     * 备用标题列表
     */
    private List<String> alternateTitleList;
}
