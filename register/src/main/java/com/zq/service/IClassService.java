package com.zq.service;

import com.zq.commons.base.IBaseService;
import com.zq.entity.ClassEntity;

import java.util.List;

/**
 * @author ZQ
 * @Date 2020/4/16
 */
public interface IClassService extends IBaseService<ClassEntity> {

    /**
     * 根据userId查询对应的班级
     * @param userId
     * @return
     */
    List<ClassEntity> getClassListByUserId(Long userId);

    /**
     * 添加user和班级的关系
     * @param list
     * @param classId
     * @param userId
     */
    void setRelation(List<Long> list, Long classId, Long userId);
}
