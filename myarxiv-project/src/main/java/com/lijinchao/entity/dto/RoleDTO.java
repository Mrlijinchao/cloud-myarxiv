package com.lijinchao.entity.dto;

import com.lijinchao.entity.Role;
import lombok.Data;

import java.util.List;

@Data
public class RoleDTO extends Role {
    /**
     * 用户id列表
     */
    private List<Long> userIds;

    /**
     * 权限id列表
     */
    private List<Long> privilegeIds;
}
