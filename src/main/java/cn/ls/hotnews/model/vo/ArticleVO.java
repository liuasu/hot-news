package cn.ls.hotnews.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * title: ArticleVO
 * author: liaoshuo
 * date: 2024/12/3 15:46
 * description:
 */
@Data
public class ArticleVO implements Serializable {

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
     * IMG 列表
     */
    private List<String> imgList;

    /**
     * 文章类型标签
     */
    private String labType;
}
