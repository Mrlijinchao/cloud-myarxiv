package com.lijinchao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName role_privilege
 */
@TableName(value ="role_privilege")
@Data
public class RolePrivilege extends BaseEntity {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     * 权限id
     */
    private Long privilegeId;

    /**
     * 角色id
     */
    private Long roleId;

}
