package com.lijinchao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 *
 * @TableName attribute_value
 */
@TableName(value ="attribute_value")
@Data
public class AttributeValue extends BaseEntity {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private Long attrId;

    /**
     *
     */
    private Long parentId;

    /**
     *
     */
    private String value;

    /**
     *
     */
    private String code;

    /**
     *
     */
    private String description;

    @TableField(exist = false)
    private List<AttributeValue> children = new ArrayList<>();

}
