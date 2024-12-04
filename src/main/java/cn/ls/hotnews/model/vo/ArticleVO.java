package cn.ls.hotnews.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * title: ArticleVO
 * author: liaoshuo
 * date: 2024/12/3 15:46
 * description:
 */
@Data
public class ArticleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String conText;
}
