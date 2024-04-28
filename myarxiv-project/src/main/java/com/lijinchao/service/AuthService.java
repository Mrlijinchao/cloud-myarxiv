package com.lijinchao.service;

import com.lijinchao.utils.BaseApiResult;

public interface AuthService {

    /**
     * 背书
     * 先验证此用户是否有替别人背书的资格
     * 然后再背书
     * @param code
     * @param password
     * @param cipherText
     * @return
     */
    BaseApiResult endorsement(String code,String password,String endorsementCode,String cipherText) throws Exception;

}
