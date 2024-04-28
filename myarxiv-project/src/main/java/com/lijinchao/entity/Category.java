package com.lijinchao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 *
 * @TableName category
 */
@TableName(value ="category")
@Data
public class Category extends BaseEntity {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private Long parentId;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String code;

    /**
     *
     */
    private String type;

    /**
     *
     */
    private String description;

    @TableField(exist = false)
    private List<Category> children = new ArrayList<>();

}
