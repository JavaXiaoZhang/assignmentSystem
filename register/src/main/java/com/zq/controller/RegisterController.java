package com.zq.controller;

import com.zq.commons.constant.ResultBeanConstant;
import com.zq.commons.pojo.ResultBean;
import com.zq.entity.ClassEntity;
import com.zq.entity.User;
import com.zq.service.IClassService;
import com.zq.service.IRegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZQ
 */
@Controller
@RequestMapping("register")
public class RegisterController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IRegisterService registerService;

    @Autowired
    private IClassService classService;

    @GetMapping("register.html")
    public String registerHtml() {
        return "index";
    }

    @PostMapping("register")
    public String register(User user, ModelMap modelMap) {
        //检查用户名是否存在
        //ResultBean resultBean = registerService.checkUsername(user.getUsername());
        //如果用户名不存在则插入用户数据
        //if (!ResultBeanConstant.OK.equals(resultBean.getStatusCode())) {
            Long userId = registerService.insertUser(user);
            //注册成功跳转到登录界面
            logger.info("用户[{}]注册成功！", user.getUsername());
            //携带用户的id
            return "redirect:http://localhost:9081/sso/sso/index.html/" + userId;
        //}
        //logger.error("用户[{}]注册失败！", user.getUsername());
        //modelMap.put("isFail", "true");
        //return "index";
    }

    @PostMapping("bat")
    public String bat(Long start, Long end, String password, Long userId, Long classId) {
        if (classId == 0){
            try {
                return "redirect:http://localhost:9081/register-service/register/admin/"+userId+"/"+ Base64Utils.encodeToUrlSafeString("课程不能为空".getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        ResultBean resultBean = registerService.batInsert(start, end, password, userId);
        if (ResultBeanConstant.OK.equals(resultBean.getStatusCode())) {
            classService.setRelation((List<Long>)resultBean.getData(), classId, userId);
            try {
                logger.info("success!=={}"+Base64Utils.encodeToUrlSafeString("ok".getBytes("utf-8")));
                return "redirect:http://localhost:9081/register-service/register/admin/"+userId+"/"+Base64Utils.encodeToUrlSafeString("ok".getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            return "redirect:http://localhost:9081/register-service/register/admin/"+userId+"/"+Base64Utils.encodeToUrlSafeString(resultBean.getData().toString().getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping(value = {"admin/{userId}/{msg}", "admin/{userId}"})
    public String admin(@PathVariable("userId") Long userId, @PathVariable(required = false) String msg, ModelMap modelMap) {
        List<ClassEntity> classList = getClassListByUserId(null);
        modelMap.put("classList", classList);
        modelMap.put("userId", userId);
        if (msg!=null){
            logger.info("解密前：{}",msg);
            msg = Base64Utils.decodeFromUrlSafeString(msg).toString();
            logger.info("解密后：{}",msg);
            modelMap.put("msg","ok");
        }
        return "admin";
    }

    @GetMapping("getClassList/{userId}")
    @ResponseBody
    public List<ClassEntity> getClassListByUserId(@PathVariable(required = false) Long userId) {
        return classService.getClassListByUserId(userId);
    }
}
