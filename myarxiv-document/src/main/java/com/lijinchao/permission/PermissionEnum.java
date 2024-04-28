package com.lijinchao.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionEnum {

    /**
     * 权限类型有两种 sys和menu
     */
    SYS("sys", "系统权限-数据权限"),
    MENU("menu", "菜单权限"),


    /**
     * 具体的权限
     */
    DElETE("delete", "删除权限"),
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
