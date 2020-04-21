package com.zq.service.impl;

import com.zq.commons.base.BaseServiceImpl;
import com.zq.commons.base.IBaseDao;
import com.zq.commons.constant.ResultBeanConstant;
import com.zq.commons.pojo.ResultBean;
import com.zq.entity.User;
import com.zq.mapper.UserMapper;
import com.zq.service.IRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZQ
 * @Date 2020/3/19
 */
@Service
public class RegisterServiceImpl extends BaseServiceImpl<User> implements IRegisterService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public ResultBean checkUsername(String username) {
        Long id = userMapper.selIdByUsername(username);
        if (id!=null){
            return new ResultBean(ResultBeanConstant.OK,null);
        }
        return new ResultBean(ResultBeanConstant.ERROR,null);
    }

    @Override
    public Long insertUser(User user) {
        //密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        insert(user);
        return user.getId();
    }

    @Override
    public ResultBean batInsert(Long start, Long end, String password, Long userId) {
        int capacity = (int) (end.longValue() - start.longValue() + 1L);
        List<Long> list = new ArrayList(capacity);
        for (Long i=start; i<=end; i++){
            list.add(i);
        }
        //密码加密
        password = passwordEncoder.encode(password);
        //Integer count = null;
        try {
            userMapper.batInsert(list, password, userId);
        }catch (Exception e){
            return new ResultBean(ResultBeanConstant.ERROR,e.getMessage());
        }

//        if (count.intValue() == capacity){
            return new ResultBean(ResultBeanConstant.OK,list);
//        }
//        return new ResultBean(ResultBeanConstant.ERROR,"部分用户数据丢失！");
    }

    @Override
    public IBaseDao<User> getBaseDao() {
        return userMapper;
    }
}
