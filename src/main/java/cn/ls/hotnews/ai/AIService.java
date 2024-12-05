package cn.ls.hotnews.ai;

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
     * ai 文章创作
     *
     * @param hotUrlGainNew 热门 URL 增益 新
     * @param loginUser     登录用户
     */
    void productionArticle(Map<String, Object> hotUrlGainNew, User loginUser);

}
