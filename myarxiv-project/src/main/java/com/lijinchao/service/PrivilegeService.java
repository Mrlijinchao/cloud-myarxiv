package com.lijinchao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijinchao.entity.Privilege;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lijinchao.entity.Role;
import com.lijinchao.entity.User;

import java.util.List;

/**
* @author 时之始
* @description 针对表【privilege】的数据库操作Service
* @createDate 2024-01-01 15:59:08
*/
public interface PrivilegeService extends IService<Privilege> {
    /**
     * 新增权限
     * @param privilege
     */
    void savePrivilege(Privilege privilege);

    /**
     * 修改权限
     * @param privilege
     */
    void updatePrivilege(Privilege privilege);

    /**
     * 删除权限
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 分页查询权限
     * @param privilege
     * @return
     */
    Page<Privilege> queryPagePrivilege(Privilege privilege);

    /***
     * 多条件分页查询
     * @param privilegeName
     * @param privilegeType
     * @param privilegeCode
     * @param roleName
     * @param roleCode
     * @param userName
     * @param userCode
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<Privilege> multiConditionalPageQuery(String privilegeName, String privilegeType, String privilegeCode,
                                              String roleName, String roleCode, String userName,
                                              String userCode, Integer pageNum, Integer pageSize);

    /**
     * 根据角色信息查询权限
     * @param role
     * @return
     */
    public List<Long> getPrivilegeIdsByRoleInfo(Role role);

    /**
     * 根据用户信息查询权限
     * @param user
     * @return
     */
    public List<Long> getPrivilegeIdsByUserInfo(User user);
}
