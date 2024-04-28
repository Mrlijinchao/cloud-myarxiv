package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.entity.Privilege;
import com.lijinchao.entity.RolePrivilege;
import com.lijinchao.service.PrivilegeService;
import com.lijinchao.service.RolePrivilegeService;
import com.lijinchao.mapper.RolePrivilegeMapper;
import com.lijinchao.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 时之始
* @description 针对表【role_privilege】的数据库操作Service实现
* @createDate 2024-01-01 16:01:13
*/
@Service
public class RolePrivilegeServiceImpl extends ServiceImpl<RolePrivilegeMapper, RolePrivilege>
    implements RolePrivilegeService{
    @Resource
    RolePrivilegeMapper rolePrivilegeMapper;

    @Resource
    RoleService roleService;

    @Resource
    PrivilegeService privilegeService;

    /**
     * 根据角色id和权限id，删除指定角色的指定权限
     * @param roleId
     * @param privilegeId
     */
    @Override
    public void deleteByRoleIdAndPrivilegeId(Long roleId, Long privilegeId) {
        QueryWrapper<RolePrivilege> queryWrapper= new QueryWrapper<>();
        queryWrapper.eq("role_id",roleId)
                .eq("privilege_id",privilegeId);
        rolePrivilegeMapper.delete(queryWrapper);

    }

    @Override
    public List<Privilege> getPrivilegeByRoleId(Long roleId) {

        if(ObjectUtils.isEmpty(roleId)){
            return new ArrayList<>();
        }
        Set<Long> pIds = new HashSet<>();
        List<Privilege> privileges = new ArrayList<>();
        List<RolePrivilege> rolePrivilegeList= this.list(new LambdaQueryWrapper<RolePrivilege>().eq(RolePrivilege::getRoleId, roleId));

        if(!CollectionUtils.isEmpty(rolePrivilegeList)){
            Set<Long> ids = rolePrivilegeList.stream().map(RolePrivilege::getPrivilegeId).collect(Collectors.toSet());
            pIds.addAll(ids);
        }

        if(!CollectionUtils.isEmpty(pIds)){
            List<Privilege> privileges1 = privilegeService.listByIds(pIds);
            privileges.addAll(privileges1);
        }

        return privileges;
    }

}




