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
 * @TableName privilege
 */
@TableName(value ="privilege")
@Data
public class Privilege extends BaseEntity {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 权限类型
     */
    private String type;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限编码
     */
    private String code;

    /**
     * 权限路径
     */
    private String path;

    /**
     * 权限描述
     */
    private String description;

}
