package com.lijinchao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户角色管理表
 * @TableName user_role
 */
@TableName(value ="user_role")
@Data
public class UserRole extends BaseEntity {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 人员id
     */
    private Long userId;

    /**
     * 角色id
     */
    private Long roleId;

}
