package cn.ls.hotnews.model.dto.hotnews;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * title: HotNewsAddReq
 * author: liaoshuo
 * date: 2024/11/19 20:21
 * description:
 */
@ApiModel(value = "HotNewsAddReq", description = "根据热点获取相关文章")
@Data
public class HotNewsAddReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     *  bilibili Id
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
