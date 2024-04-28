package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.entity.UserRole;
import com.lijinchao.service.UserRoleService;
import com.lijinchao.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author 时之始
* @description 针对表【user_role(用户角色管理表)】的数据库操作Service实现
* @createDate 2024-01-01 16:01:30
*/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>
    implements UserRoleService{

    @Resource
    UserRoleMapper userRoleMapper;
    /**
     * 根据用户id和角色id，删除指定用户的指定角色
     * @param userId
     * @param roleId
     */
    @Override
    public void deleteByUserIdAndRoleId(Long userId, Long roleId) {
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("role_id", roleId);
        userRoleMapper.delete(queryWrapper);
    }

}




