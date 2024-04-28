package com.lijinchao.utils;

import com.lijinchao.permission.PermissionEnum;
import com.lijinchao.permission.PermissionRoleEnum;

import java.util.ArrayList;
import java.util.List;

public class EnumUtil {

    public static List<String> getValue(PermissionEnum[] permissionEnum, PermissionRoleEnum[] permissionRoleEnum){
        ArrayList<String> permissionList = new ArrayList<>();
        for (PermissionEnum per: permissionEnum){
            permissionList.add(per.getCode());
        }
        for (PermissionRoleEnum per: permissionRoleEnum){
            permissionList.add(per.getCode());
        }
        return permissionList;
    }

}
