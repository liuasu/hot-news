package cn.ls.hotnews.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * title: HotApiVO
 * author: liaoshuo
 * date: 2024/11/18 21:11
 * description:
 */
@Data
public class HotApiVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口地址
     */
    private String apiURL;

    /**
     * 接口描述
     */
    private String apiDescribe;
}
