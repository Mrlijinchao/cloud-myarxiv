package com.lijinchao.myarxivProjectFacade.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用于验证访问document模块的请求是否合法
 *
 */
public interface AuthFacade {

    /**
     * 请求是否合法的标志是用户是否在project模块实现登录
     * @param userId
     * @param token
     * @return
     */
    @GetMapping("/auth")
    Boolean authRequest(@RequestParam(required = false) Long userId, @RequestParam(required = false) String token);

    @GetMapping("/permission")
    Boolean authPermission(@RequestParam(required = false) Long userId, @RequestParam(required = false) String token,
                           @RequestParam(required = false) List<String> permissions);

}
