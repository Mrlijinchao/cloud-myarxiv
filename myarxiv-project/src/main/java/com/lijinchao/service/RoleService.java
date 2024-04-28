package com.lijinchao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijinchao.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lijinchao.entity.User;
import com.lijinchao.entity.dto.RoleDTO;

import java.util.List;

/**
* @author 时之始
* @description 针对表【role】的数据库操作Service
* @createDate 2024-01-01 15:59:24
*/
public interface RoleService extends IService<Role> {
    /**
     * 获取超级管理员角色
     * @return
     */
    Role getSuperAdmin();

    /**
     * 添加角色
     * @param roleDTO
     */
    void addRole(RoleDTO roleDTO);

    /**
     * 查询角色列表
     * @param roleDTO
     * @return
     */
    List<Role> queryRoles(RoleDTO roleDTO);

    /**
     * 删除角色
     * @param ids
     */
    void deleteRole(List<Long> ids);

    /**
     * 修改角色
     * @param role
     */
    void updateRole(Role role);

    /**
     * 多条件查询角色
     * @param roleName
     * @param roleCode
     * @param roleType
     * @param userName
     * @param userCode
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<Role> multiConditionalPageQuery(String roleName, String roleCode,
                                         String roleType, String userName,
                                         String userCode, Integer pageNum, Integer pageSize);

    /**
     * 角色查询用户
     * @param roleId
     * @return
     */
    List<User> queryUser(Long roleId);

    /**
     * 角色添加权限
     * @param roleId
     * @param privilegeId
     */
    void addPrivilege(Long roleId, Long privilegeId);
}
