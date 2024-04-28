package com.lijinchao.feign.facadeImpl.auth;

import com.lijinchao.entity.User;
import com.lijinchao.myarxivProjectFacade.auth.AuthFacade;
import com.lijinchao.service.UserService;
import com.lijinchao.utils.QueryRedisUserUtil;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/openapi/authFacade")
public class AuthFacadeImpl implements AuthFacade {

    @Resource
    UserService userService;
    @Resource
    QueryRedisUserUtil queryRedisUserUtil;

    @Override
    public Boolean authRequest(Long userId, String token) {
        User user = queryRedisUserUtil.queryUser(userId, token);

        if(ObjectUtils.isEmpty(user)){
            return false;
        }

        return true;
    }

    @Override
    public Boolean authPermission(Long userId, String token, List<String> permissions) {
        User user = queryRedisUserUtil.queryUser(userId, token);

        if(ObjectUtils.isEmpty(user)){
            return false;
        }

        return userService.checkPermissionForUser(user, permissions);
    }
}
