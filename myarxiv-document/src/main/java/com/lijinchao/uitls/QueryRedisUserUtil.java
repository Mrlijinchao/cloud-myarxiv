package com.lijinchao.uitls;

import com.lijinchao.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Component
public class QueryRedisUserUtil {

    @Resource
    RedisUtil redisUtil;

    public User queryUser(Long userId, String token){
        if(ObjectUtils.isEmpty(userId) && !StringUtils.hasText(token)){
            return null;
        }

        if(!ObjectUtils.isEmpty(userId)){
            User user = (User)redisUtil.getById(String.valueOf(userId));
            if(!ObjectUtils.isEmpty(user)){
                return user;
            }
        }

        if(StringUtils.hasText(token)){
            User user = (User)redisUtil.getByToken(token);
            if(!ObjectUtils.isEmpty(user)){
                return user;
            }
        }

        return null;
    }

}
