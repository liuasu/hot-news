package cn.ls.hotnews.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ai文章生成记录
 * @TableName ai_article_creation_log
 */
@TableName(value ="ai_article_creation_log")
@Data
public class AiArticleCreationLog implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * ai平台
     */
    private String aiPlatForm;
    /**
     * 热点标题
     */
    private String hotTitle;
    /**
     * 热点链接
     */
    private String hotUrl;
    /**
     * ai生成标题
     */
    private String aiCreationTitle;
    /**
     * ai生成内容
     */
    private String aiCreationContext;
    /**
     * userId
     */
    private String userId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}