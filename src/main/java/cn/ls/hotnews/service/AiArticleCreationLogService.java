package cn.ls.hotnews.service;

import cn.ls.hotnews.model.dto.aiarticlecreationlog.AiArticleCreationLogQueryRes;
import cn.ls.hotnews.model.entity.AiArticleCreationLog;
import cn.ls.hotnews.model.vo.AiArticleCreationLogVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
*
* ai文章生成记Service
* @createDate 2024-12-11 13:11:18
* @author ls
*/
public interface AiArticleCreationLogService extends IService<AiArticleCreationLog> {

    /**
    * 查询ai文章生成记列表
    */
    Page<AiArticleCreationLogVO> findAiArticleCreationLogList(AiArticleCreationLogQueryRes aiArticleCreationLog);

    /**
    *  添加ai文章生成记
    */
    Boolean addAiArticleCreationLog(AiArticleCreationLog aiArticleCreationLog);

    AiArticleCreationLogVO thisToVO(AiArticleCreationLog articleCreationLog);
}
