package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.entity.Privilege;
import com.lijinchao.entity.Role;
import com.lijinchao.entity.RolePrivilege;
import com.lijinchao.entity.User;
import com.lijinchao.enums.GlobalEnum;
import com.lijinchao.service.*;
import com.lijinchao.mapper.PrivilegeMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 时之始
* @description 针对表【privilege】的数据库操作Service实现
* @createDate 2024-01-01 15:59:08
*/
@Service
public class PrivilegeServiceImpl extends ServiceImpl<PrivilegeMapper, Privilege>
    implements PrivilegeService{

    @Resource
    UserService userService;

    @Resource
    UserPrivilegeService userPrivilegeService;

    @Resource
    RolePrivilegeService rolePrivilegeService;

    @Resource
    RoleService roleService;

    @Override
    public void savePrivilege(Privilege privilege) {
        if(ObjectUtils.isEmpty(privilege)){
            return;
        }
        privilege.setStatusCd(GlobalEnum.EFFECT.getCode());
        this.save(privilege);
    }

    @Override
    public void updatePrivilege(Privilege privilege) {
        if(ObjectUtils.isEmpty(privilege)){
            return;
        }
        this.updateById(privilege);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        this.removeByIds(ids);
    }

    @Override
    public Page<Privilege> queryPagePrivilege(Privilege privilege) {
        if(ObjectUtils.isEmpty(privilege)){
            return new Page<>();
        }
        Page<Privilege> page = new Page<>(1,10);
//        if(!ObjectUtils.isEmpty(privilege.getPageNum())){
//            page.setCurrent(privilege.getPageNum());
//        }
//        if(!ObjectUtils.isEmpty(privilege.getPageSize())){
//            page.setSize(privilege.getPageSize());
//        }
        LambdaQueryWrapper<Privilege> privilegeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!ObjectUtils.isEmpty(privilege.getId())){
            privilegeLambdaQueryWrapper.eq(Privilege::getId,privilege.getId());
        }
        if(!ObjectUtils.isEmpty(privilege.getName())){
            privilegeLambdaQueryWrapper.like(Privilege::getName,privilege.getName());
        }
        if(!ObjectUtils.isEmpty(privilege.getType())){
            privilegeLambdaQueryWrapper.like(Privilege::getType,privilege.getType());
        }
        if(!ObjectUtils.isEmpty(privilege.getCode())){
            privilegeLambdaQueryWrapper.like(Privilege::getCode,privilege.getCode());
        }
        Page<Privilege> rivilegePage = this.page(page, privilegeLambdaQueryWrapper);
        return rivilegePage;
    }

    @Override
    public Page<Privilege> multiConditionalPageQuery(String privilegeName, String privilegeType, String privilegeCode,
                                                     String roleName, String roleCode, String userName, String userCode,
                                                     Integer pageNum, Integer pageSize) {
        // 什么参数都没传就分页返回全部权限
        if(ObjectUtils.isEmpty(privilegeName) && ObjectUtils.isEmpty(privilegeType) && ObjectUtils.isEmpty(privilegeCode)
                && ObjectUtils.isEmpty(roleName)&& ObjectUtils.isEmpty(roleCode) && ObjectUtils.isEmpty(userName)
                && ObjectUtils.isEmpty(userCode)){
            return this.page(new Page<>(pageNum,pageSize), new LambdaQueryWrapper<Privilege>());
        }

        ArrayList<Long> privilegeIds = new ArrayList<>();
        // 获取用户权限Id
        User user = new User();
        user.setName(userName);
        user.setCode(userCode);
        List<Long> privilegeIdsByUserInfo = getPrivilegeIdsByUserInfo(user);
        privilegeIds.addAll(privilegeIdsByUserInfo);

        // 获取角色权限Id
        Role role = new Role();
        role.setName(roleName);
        role.setCode(roleCode);
        List<Long> privilegeIdsByRoleInfo = getPrivilegeIdsByRoleInfo(role);
        privilegeIds.addAll(privilegeIdsByRoleInfo);

        Page<Privilege> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Privilege> privilegeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Integer flag = 0;
        if(!ObjectUtils.isEmpty(privilegeName)){
            flag++;
            privilegeLambdaQueryWrapper.like(Privilege::getName,privilegeName);
        }
        if(!ObjectUtils.isEmpty(privilegeType)){
            flag++;
            privilegeLambdaQueryWrapper.eq(Privilege::getType,privilegeType);
        }
        if(!ObjectUtils.isEmpty(privilegeCode)){
            flag++;
            privilegeLambdaQueryWrapper.like(Privilege::getCode,privilegeCode);
        }

        if(!CollectionUtils.isEmpty(privilegeIds)){
            flag++;
            privilegeLambdaQueryWrapper.in(Privilege::getId,privilegeIds);
        }
        if(!flag.equals(0)){
            return this.page(page, privilegeLambdaQueryWrapper);
        }

        return new Page<>();
    }

    /**
     * 根据角色信息查询角色权限Id
     * @param role
     * @return
     */
    @Override
    public List<Long> getPrivilegeIdsByRoleInfo(Role role){
        if(ObjectUtils.isEmpty(role)){
            return new ArrayList<>();
        }

        //先获取到角色Id
        ArrayList<Long> roleId = new ArrayList<>();
        if(!ObjectUtils.isEmpty(role.getId())){
            roleId.add(role.getId());
        }
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 是否有条件成立的标志
        Integer flag = 0;
        if(!ObjectUtils.isEmpty(role.getName())){
            flag++;
            roleLambdaQueryWrapper.like(Role::getName,role.getName());
        }
        if(!ObjectUtils.isEmpty(role.getCode())){
            flag++;
            roleLambdaQueryWrapper.like(Role::getCode,role.getCode());
        }
        // 如果有条件成立就查询
        if(!flag.equals(0)){
            List<Role> list = roleService.list(roleLambdaQueryWrapper);
            if(!CollectionUtils.isEmpty(list)){
                Set<Long> ids = list.stream().map(Role::getId).collect(Collectors.toSet());
                roleId.addAll(ids);
            }
        }

        // 获取角色权限
        List<RolePrivilege> rolePrivilegeList = rolePrivilegeService.list(new LambdaQueryWrapper<RolePrivilege>().eq(RolePrivilege::getRoleId, roleId));
        ArrayList<Long> privilegeIds = new ArrayList<>();
        if(!CollectionUtils.isEmpty(rolePrivilegeList)){
            Set<Long> ids = rolePrivilegeList.stream().map(RolePrivilege::getPrivilegeId).collect(Collectors.toSet());
            privilegeIds.addAll(ids);
        }

        return privilegeIds;

    }

    /**
     * 根据用户信息查询用户权限Id
     * 后续可添加查询条件
     * @param user
     * @return
     */
    @Override
    public List<Long> getPrivilegeIdsByUserInfo(User user){
        if(ObjectUtils.isEmpty(user)){
            return new ArrayList<>();
        }
        //先获取到用户Id
        ArrayList<Long> userId = new ArrayList<>();
        if(!ObjectUtils.isEmpty(user.getId())){
            userId.add(user.getId());
        }
        Integer flag = 0;
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!ObjectUtils.isEmpty(user.getName())){
            flag++;
            userLambdaQueryWrapper.like(User::getName,user.getName());
        }
        if(!ObjectUtils.isEmpty(user.getCode())){
            flag++;
            userLambdaQueryWrapper.like(User::getCode,user.getCode());
        }
        if(!flag.equals(0)){
            List<User> list = userService.list(userLambdaQueryWrapper);

            if(!ObjectUtils.isEmpty(list)){
                Set<Long> ids = list.stream().map(User::getId).collect(Collectors.toSet());
                userId.addAll(ids);
            }
        }
        ArrayList<Long> privilegeIds = new ArrayList<>();
        // 获取用户权限
        List<Privilege> userPrivileges = userService.getUserPrivilegesByIds(userId);
        if(!CollectionUtils.isEmpty(userPrivileges)){
            Set<Long> ids = userPrivileges.stream().map(Privilege::getId).collect(Collectors.toSet());
            privilegeIds.addAll(ids);
        }

        return privilegeIds;
    }

}




