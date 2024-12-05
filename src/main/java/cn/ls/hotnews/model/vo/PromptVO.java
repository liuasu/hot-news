package cn.ls.hotnews.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ai提示词表
 *
 * @TableName prompt
 */
@Data
public class PromptVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 模板名称
     */
    private String promptName;
    /**
     * 提示词模板
     */
    private String promptTemplate;
}