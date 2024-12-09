package cn.ls.hotnews.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * title: HotNewsVO
 * author: liaoshuo
 * date: 2024/11/19 20:21
 * description:
 */
@Data
public class HotNewsVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * bilibili wangyi Id
     */
    private String biId;

    /**
     * 标题
     */
    private String title;

    /**
     * 热门网址
     */
    private String hotURL;

    /*
      头条专有
     */
    //private String mobileUrl;


    /**
     * 图片 URL
     */
    private String imageURL;

    /**
     * 热描述
     */
    private String hotDesc;
}
