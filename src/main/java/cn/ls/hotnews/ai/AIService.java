package cn.ls.hotnews.ai;

import cn.ls.hotnews.model.dto.productionarticle.ProductionTrusteeshipAddReq;
import cn.ls.hotnews.model.entity.Article;
import cn.ls.hotnews.model.entity.User;

import java.util.Map;

/**
 * title: AIService
 * author: liaoshuo
 * date: 2024/12/5 15:33
 * description:
 */
public interface AIService {

    /**
     * 生成文章
     *
     * @param hotUrlGainNew 热门 URL 增益 新
     * @param loginUser     登录用户
     */
    void productionArticle(Map<String, Object> hotUrlGainNew, User loginUser);
    /**
     * 生成文章
     *
     * @param params 生成参数
     * @return 生成的文章
     */
    Article generateArticle(Map<String, Object> params);

    /**
     * 托管服务
     *
     * @param trusteeshipAddReq 托管请求
     * @param loginUser 登录用户
     */
    void Trusteeship(ProductionTrusteeshipAddReq trusteeshipAddReq, User loginUser);



}
