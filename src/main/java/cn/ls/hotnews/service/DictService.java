package cn.ls.hotnews.service;

import cn.ls.hotnews.model.dto.dict.DictAddReq;
import cn.ls.hotnews.model.dto.dict.DictEditReq;
import cn.ls.hotnews.model.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
*
* 字典Service
* @createDate 2024-12-31 11:00:38
* @author ls
*/
public interface DictService extends IService<Dict> {

    /**
    * 查询字典列表
    */
    List<Dict> findDictList(Dict dict);

    /**
    *  添加字典
    */
    Boolean addDict(DictAddReq dictAddReq);

    /**
    *  修改字典
    */
    Boolean editDict(DictEditReq dictEditReq);

    /**
    *  删除字典
    */
    Boolean delById(Long id);

}
