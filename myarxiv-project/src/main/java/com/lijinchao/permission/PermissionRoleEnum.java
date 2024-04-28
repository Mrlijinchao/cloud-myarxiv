package com.lijinchao.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionRoleEnum {
    /**
     * 用户角色
     */
    USER("ordinary", "普通用户权限"),
    /**
     * 管理员角色
     */
    ADMIN("admin", "管理员权限"),

    /**
     * 超级管理员角色
     */
    SUPERADMIN("superAdmin", "超级管理员权限"),

    AUDIT("audit","审核论文权限"),

    /**
     * 无需校验,
     */
    NO("no", "无需权限"),
    ;
    /**
     * 权限编码
     */
    private final String code;
    /**
     * 权限名称
     */
    private final String name;
}
