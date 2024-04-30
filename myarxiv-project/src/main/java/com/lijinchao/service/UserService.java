package com.lijinchao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijinchao.entity.Privilege;
import com.lijinchao.entity.Role;
import com.lijinchao.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lijinchao.entity.dto.UserDTO;
import com.lijinchao.utils.BaseApiResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 时之始
* @description 针对表【user】的数据库操作Service
* @createDate 2023-12-27 15:26:43
*/
public interface UserService extends IService<User> {


    /**
     * 用户注册
     * @param user
     * @return
     */
    BaseApiResult userRegister(User user) throws Exception;

    /**
     * 检查用户是否拥有权限
     * @param user
     * @param permissions
     * @return
     */
    Boolean checkPermissionForUser(User user, List<String> permissions);

    /**
     * 判断是否为管理员
     * @param userId
     * @return
     */
    public Boolean isAdmin(Long userId);

    /**
     * 根据用户Id查询用户所有权限
     * @param userIds
     * @return
     */
    List<Privilege> getUserPrivilegesByIds(List<Long> userIds);

    /**
     * 根据用户Id查询用户所有的角色
     * @param userId
     * @return
     */
    List<Role> queryRole(Long userId);

    /**
     * 删除用户（可批量）
     * @param userIds
     * @return
     */
    BaseApiResult delUsersById(List<Long> userIds);

    /**
     * 注销用户账号
     * @param token
     * @return
     */
    BaseApiResult closeAccount(String token);

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    BaseApiResult modifyUserInfo(User user,String token);

    /**
     * 修改用户密码
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    BaseApiResult modifyPassword(Long userId,String oldPassword,String newPassword);

    /**
     * 多条件分页查询
     * @param userDTO
     * @return
     */
    Page<User> multiConditionalPageQuery(UserDTO userDTO);

    /**
     * 用户添加权限
     * @param userId
     * @param privilegeId
     */
    void addPrivilege(Long userId, Long privilegeId);

    /**
     * 用户添加角色
     * @param userId
     * @param roleId
     */
    void addRole(Long userId, Long roleId);

    /**
     * 获取补全的用户信息
     * @param user
     * @return
     */
    UserDTO getCompletionUser(User user);

}
