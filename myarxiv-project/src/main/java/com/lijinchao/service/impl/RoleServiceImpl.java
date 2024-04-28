package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.entity.Role;
import com.lijinchao.entity.RolePrivilege;
import com.lijinchao.entity.User;
import com.lijinchao.entity.UserRole;
import com.lijinchao.entity.dto.RoleDTO;
import com.lijinchao.entity.dto.UserDTO;
import com.lijinchao.enums.GlobalEnum;
import com.lijinchao.mapper.RolePrivilegeMapper;
import com.lijinchao.mapper.UserMapper;
import com.lijinchao.permission.PermissionRoleEnum;
import com.lijinchao.service.RolePrivilegeService;
import com.lijinchao.service.RoleService;
import com.lijinchao.mapper.RoleMapper;
import com.lijinchao.service.UserRoleService;
import com.lijinchao.service.UserService;
import com.lijinchao.utils.BeanUtilCopy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 时之始
* @description 针对表【role】的数据库操作Service实现
* @createDate 2024-01-01 15:59:24
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{
    @Resource
    RoleMapper roleMapper;
    @Resource
    RolePrivilegeService rolePrivilegeService;
    @Resource
    UserRoleService userRoleService;
    @Resource
    UserService userService;
    @Resource
    UserMapper userMapper;
    @Resource
    RolePrivilegeMapper rolePrivilegeMapper;

    @Override
    public Role getSuperAdmin() {
        Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getCode, PermissionRoleEnum.SUPERADMIN.getCode()));
        return role;
    }

    @Transactional
    @Override
    public void addRole(RoleDTO roleDTO) {
        //新增角色
        saveRole(roleDTO);

        /**
         * 如果创建角色的时候添加了权限和人员的话，在这里可以直接完成关联
         */
        //新增角色权限
        addRolePrivilege(roleDTO);
        //新增人员角色
        addUserRole(roleDTO);
    }

    @Override
    public List<Role> queryRoles(RoleDTO roleDTO) {
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = packageQueryWrapper(roleDTO);
        roleLambdaQueryWrapper.orderByDesc(Role::getCreateDate);
        return this.list(roleLambdaQueryWrapper);
    }

    @Override
    public void deleteRole(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        // 在角色表中删除角色
        this.removeByIds(ids);

        // 在用户角色表中删除该角色的关系
        userRoleService.remove(new LambdaQueryWrapper<UserRole>()
                .in(UserRole::getRoleId,ids));
        // 在角色权限表中删除该角色的关系
        rolePrivilegeService.remove(new LambdaQueryWrapper<RolePrivilege>()
                .in(RolePrivilege::getRoleId,ids));

    }

    @Override
    public void updateRole(Role role) {
        if(ObjectUtils.isEmpty(role)){
            return;
        }
        this.updateById(role);
    }

    @Override
    public Page<Role> multiConditionalPageQuery(String roleName, String roleCode, String roleType, String userName,
                                                String userCode, Integer pageNum, Integer pageSize) {
        ArrayList<Long> roleIds = new ArrayList<>();
        // 根据用户信息查询角色id
        if(!ObjectUtils.isEmpty(userName) || !ObjectUtils.isEmpty(userCode)){
            UserDTO userDTO = new UserDTO();
            userDTO.setCode(userCode);
            userDTO.setName(userName);
            Page<User> userPage = userService.multiConditionalPageQuery(userDTO);
            List<User> records = userPage.getRecords();
            if(!CollectionUtils.isEmpty(records)){
                Set<Long> userIds = records.stream().map(User::getId).collect(Collectors.toSet());
                List<UserRole> userRoleList = userRoleService.list(new LambdaQueryWrapper<UserRole>().in(UserRole::getUserId, userIds));
                if(!CollectionUtils.isEmpty(userRoleList)){
                    Set<Long> ids = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
                    roleIds.addAll(ids);
                }
            }
        }

        RoleDTO roleDTO = new RoleDTO();
        Page<Role> page = new Page<>(pageNum,pageSize);
        roleDTO.setName(roleName);
        roleDTO.setCode(roleCode);
        roleDTO.setType(roleType);
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = packageQueryWrapper(roleDTO);
        if(!CollectionUtils.isEmpty(roleIds)){
            roleLambdaQueryWrapper.in(Role::getId,roleIds);
        }
        Page<Role> rolePage = this.page(page, roleLambdaQueryWrapper);
        return rolePage;
    }

    private LambdaQueryWrapper<Role> packageQueryWrapper(RoleDTO roleDTO){
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.ne(Role::getStatusCd, GlobalEnum.INVALID);
        if(!ObjectUtils.isEmpty(roleDTO.getId())){
            roleLambdaQueryWrapper.eq(Role::getId,roleDTO.getId());
        }

        if(!ObjectUtils.isEmpty(roleDTO.getType())){
            roleLambdaQueryWrapper.eq(Role::getType,roleDTO.getType());
        }

        if(!ObjectUtils.isEmpty(roleDTO.getCode())){
            roleLambdaQueryWrapper.like(Role::getCode,roleDTO.getCode());
        }

        if(!ObjectUtils.isEmpty(roleDTO.getName())){
            roleLambdaQueryWrapper.like(Role::getName,roleDTO.getName());
        }
        return roleLambdaQueryWrapper;
    }

    /**
     * 新增角色
     * @param roleDTO
     */
    private void saveRole(RoleDTO roleDTO){
        //页面有传id说明不是新增的
        if(!ObjectUtils.isEmpty(roleDTO.getId())){
            return;
        }
        Role newRole = new Role();
        BeanUtilCopy.copyProperties(roleDTO,newRole);
        newRole.setStatusCd(String.valueOf(GlobalEnum.EFFECT.getCode()));
        this.save(newRole);
        roleDTO.setId(newRole.getId());
    }

    /**
     * 新增角色权限
     * @param roleDTO
     */
    private void addRolePrivilege(RoleDTO roleDTO){
        List<Long> privilegeIds = roleDTO.getPrivilegeIds();
        //1、有角色id并且有权限id才是新增
        if(ObjectUtils.isEmpty(roleDTO.getId()) || CollectionUtils.isEmpty(privilegeIds)){
            return;
        }
        //2、查询角色具有的权限id， 此前不存在再新增
        List<RolePrivilege> oleRolePrivileges = rolePrivilegeService.list(new LambdaQueryWrapper<RolePrivilege>()
                .eq(RolePrivilege::getRoleId, roleDTO.getId()).ne(RolePrivilege::getStatusCd, GlobalEnum.INVALID));
        Set<Long> oldPrivilegeIds = oleRolePrivileges.stream().map(RolePrivilege::getPrivilegeId).collect(Collectors.toSet());
        List<RolePrivilege> rolePrivileges = new ArrayList<>();
        for (Long privId : privilegeIds) {
            if (oldPrivilegeIds.contains(privId)) {
                continue;
            }
            RolePrivilege rolePrivilege = new RolePrivilege();
            rolePrivilege.setPrivilegeId(privId);
            rolePrivilege.setRoleId(roleDTO.getId());
            rolePrivilege.setStatusCd(String.valueOf(GlobalEnum.EFFECT.getCode()));
            rolePrivileges.add(rolePrivilege);
        }
        if(CollectionUtils.isEmpty(rolePrivileges)){
            return;
        }
        rolePrivilegeService.saveBatch(rolePrivileges);
    }

    /**
     * 新增人员角色
     * @param roleDTO
     */
    private void addUserRole(RoleDTO roleDTO){
        List<Long> userIds = roleDTO.getUserIds();
        //有角色id并且有人员id才是新增
        if(ObjectUtils.isEmpty(roleDTO.getId()) || CollectionUtils.isEmpty(userIds)){
            return;
        }
        List<UserRole> oleUserRoles = userRoleService.list(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getRoleId, roleDTO.getId()).ne(UserRole::getStatusCd, GlobalEnum.INVALID));
        Set<Long> oldUserIds = oleUserRoles.stream().map(UserRole::getUserId).collect(Collectors.toSet());
        List<UserRole> userRoles = new ArrayList<>();
        for (Long userId : userIds) {
            if (oldUserIds.contains(userId)) {
                continue;
            }
            UserRole userRole = new UserRole();
            userRole.setRoleId(roleDTO.getId());
            userRole.setStatusCd(String.valueOf(GlobalEnum.EFFECT.getCode()));
            userRole.setUserId(userId);
            userRoles.add(userRole);
        }
        userRoleService.saveBatch(userRoles);
    }

    /**
     * 根据角色Id查询角色包括的用户
     * @param roleId
     * @return
     */
    @Override
    public List<User> queryUser(Long roleId) {
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        List<UserRole> userRoles=userRoleService.list(queryWrapper);
        List<Long> userIds = userRoles.stream()
                .map(UserRole::getUserId)
                .collect(Collectors.toList());
        if(userIds.isEmpty()){
            return new ArrayList<>();
        }
        return userMapper.selectBatchIds(userIds).stream()
                .map(user -> {
                    user.setPassword("");
                    return user;
                }).collect(Collectors.toList());
    }

    /**
     * 根据角色Id和权限Id，为角色添加权限
     * @param roleId
     * @param privilegeId
     */
    @Override
    public void addPrivilege(Long roleId, Long privilegeId) {
        RolePrivilege rolePrivilege=new RolePrivilege();
        rolePrivilege.setRoleId(roleId);
        rolePrivilege.setPrivilegeId(privilegeId);
        rolePrivilegeMapper.insert(rolePrivilege);
    }
}




