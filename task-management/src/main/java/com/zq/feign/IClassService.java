package com.zq.feign;

import com.zq.entity.ClassEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author ZQ
 * @Date 2020/4/20
 */

@FeignClient(value = "register-service", fallback = IClassService.IClassServiceFallBack.class)
public interface IClassService {

    @GetMapping("register/getClassList/{userId}")
    List<ClassEntity> getClassListByUserId(@PathVariable Long userId);

    class IClassServiceFallBack implements IClassService{
        @Override
        public List<ClassEntity> getClassListByUserId(Long userId) {
            return null;
        }
    }
}
