package com.lijinchao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 人员角色管理表
 * @TableName user_privilege
 */
@TableName(value ="user_privilege")
@Data
public class UserPrivilege extends BaseEntity {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 权限id
     */
    private Long privilegeId;

    /**
     * 人员id
     */
    private Long userId;

}
