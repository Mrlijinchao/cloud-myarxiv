package com.lijinchao.permission;

import java.lang.annotation.*;

/**
 * @ClassName Permission
 * @Description 设置访问权限，可根据不同接口要求，设置为管理员或者普通用户
 * @Author lijinchao
 * @Date 2022/12/7 12:19
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Permission {

    /**
     * 权限数组，数据库里面有对应的
     */
//    @AliasFor("value")
    PermissionEnum[] value() default {};

    /**
     * 角色权限数组
     */
    PermissionRoleEnum[] roleValue() default {};
}
