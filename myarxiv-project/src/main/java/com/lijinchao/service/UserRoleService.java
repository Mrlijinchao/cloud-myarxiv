package com.lijinchao.service;

import com.lijinchao.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 时之始
* @description 针对表【user_role(用户角色管理表)】的数据库操作Service
* @createDate 2024-01-01 16:01:30
*/
public interface UserRoleService extends IService<UserRole> {
    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}
