package com.zq.service.impl;

import com.zq.commons.base.BaseServiceImpl;
import com.zq.commons.base.IBaseDao;
import com.zq.entity.ClassEntity;
import com.zq.mapper.ClassMapper;
import com.zq.service.IClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZQ
 * @Date 2020/4/16
 */
@Service
public class ClassServiceImpl extends BaseServiceImpl<ClassEntity> implements IClassService {

    @Autowired
    private ClassMapper classMapper;

    @Override
    public IBaseDao<ClassEntity> getBaseDao() {
        return classMapper;
    }

    @Override
    public List<ClassEntity> getClassListByUserId(Long userId) {
        return classMapper.getClassListByUserId(userId);
    }

    @Override
    public void setRelation(List<Long> list, Long classId, Long userId) {
        classMapper.setRelation(list, classId, userId);
    }
}
