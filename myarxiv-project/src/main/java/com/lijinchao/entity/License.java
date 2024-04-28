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
 * @TableName license
 */
@TableName(value ="license")
@Data
public class License extends BaseEntity {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private String description;

    /**
     *
     */
    private String link;

    /**
     *
     */
    private String type;

    /**
     *
     */
    private String code;

}
