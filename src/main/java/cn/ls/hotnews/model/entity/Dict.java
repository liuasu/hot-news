package cn.ls.hotnews.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 字典表
 * @TableName dict
 */
@TableName(value ="dict")
@Data
public class Dict implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 字典名称
     */
    private String dictName;
    /**
     * 字典标签(key)
     */
    private String dictLabelKey;
    /**
     * 字典标签(values)
     */
    private String dictLabelValues;
    /**
     * 字典类型
     */
    private Long dictChildId;
    /**
     * 状态（0正常 1停用）
     */
    private Integer status;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}