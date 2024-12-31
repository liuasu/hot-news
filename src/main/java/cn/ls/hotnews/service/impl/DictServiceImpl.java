package cn.ls.hotnews.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.ls.hotnews.mapper.DictMapper;
import cn.ls.hotnews.model.dto.dict.DictAddReq;
import cn.ls.hotnews.model.dto.dict.DictEditReq;
import cn.ls.hotnews.model.entity.Dict;
import cn.ls.hotnews.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
*
* @description dict(字典表)的Service实现
* @createDate 2024-12-31 11:00:38
* @author ls
*/
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    /**
    * 查询字典列表
    */
    @Override
    public List<Dict> findDictList(Dict dict){
        return  lambdaQuery()
                .like(ObjectUtil.isNotNull(dict.getDictName()),Dict::getDictName,dict.getDictName())
                .eq(ObjectUtil.isNotNull(dict.getStatus()),Dict::getStatus,dict.getStatus())
                .list();
    }

    /**
    *  添加字典
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean addDict(DictAddReq dictAddReq){
        if(dictAddReq == null){
            System.out.println("请求参数为空!");
        }
        Dict dict = new Dict();
        BeanUtils.copyProperties(dict, dictAddReq);
        dict.setCreateTime(new Date());
        return this.save(dict);
    }

    /**
    *  修改字典
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean editDict(DictEditReq dictEditReq){
        if(dictEditReq == null){
            System.out.println("请求参数为空!");
        }
        return  lambdaUpdate()
                .set(ObjectUtil.isNotNull(dictEditReq.getDictName()),Dict::getDictName,dictEditReq.getDictName())
                .set(ObjectUtil.isNotNull(dictEditReq.getDictLabelKey()),Dict::getDictLabelKey,dictEditReq.getDictLabelKey())
                .set(ObjectUtil.isNotNull(dictEditReq.getDictLabelValues()),Dict::getDictLabelValues,dictEditReq.getDictLabelValues())
                .set(ObjectUtil.isNotNull(dictEditReq.getDictChildId()),Dict::getDictChildId,dictEditReq.getDictChildId())
                .set(ObjectUtil.isNotNull(dictEditReq.getStatus()),Dict::getStatus,dictEditReq.getStatus())
                .set(ObjectUtil.isNotNull(dictEditReq.getUserId()),Dict::getUserId,dictEditReq.getUserId())
                .set(Dict::getUpdateTime,new Date())
                .eq(Dict::getId,dictEditReq.getId())
                .update();
    }

    /**
    *  删除字典
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean delById(Long id){
        if(id == null || id <= 0){
            System.out.println("请求参数为空!");
        }
        return this.removeById(id);
    }
}




