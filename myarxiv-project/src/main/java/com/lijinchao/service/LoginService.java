package com.lijinchao.service;

import com.lijinchao.entity.User;
import com.lijinchao.utils.BaseApiResult;

public interface LoginService {
    /**
     * 登录
     * @param user
     * @return
     * @throws Exception
     */
    Object doLogin(User user) throws Exception;

    /**
     * 邮箱登录
     * @param email
     * @param code
     * @return
     */
    Object emailLogin(String email,String code) throws Exception;

    /**
     * 后台登录
     * @param user
     * @return
     */
    Object backgroundLogin(User user) throws Exception;

}
