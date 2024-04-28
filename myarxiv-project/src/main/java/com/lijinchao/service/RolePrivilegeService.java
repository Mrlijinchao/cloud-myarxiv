package com.lijinchao.service;

import com.lijinchao.entity.Privilege;
import com.lijinchao.entity.RolePrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 时之始
* @description 针对表【role_privilege】的数据库操作Service
* @createDate 2024-01-01 16:01:13
*/
public interface RolePrivilegeService extends IService<RolePrivilege> {
    void deleteByRoleIdAndPrivilegeId(Long roleId, Long privilegeId);
    //    queryRolePrivileges
    List<Privilege> getPrivilegeByRoleId(Long roleId);
}
