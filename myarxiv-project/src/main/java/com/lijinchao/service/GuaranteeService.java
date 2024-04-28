package com.lijinchao.service;

import com.lijinchao.entity.Guarantee;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lijinchao.entity.User;

/**
* @author 时之始
* @description 针对表【guarantee】的数据库操作Service
* @createDate 2024-03-30 16:10:03
*/
public interface GuaranteeService extends IService<Guarantee> {
    /**
     * 检查是否需要担保
     * @param user
     * @return
     */
    Boolean checkGuarantee(User user);
}
