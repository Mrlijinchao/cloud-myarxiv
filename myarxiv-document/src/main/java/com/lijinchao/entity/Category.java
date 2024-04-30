package com.lijinchao.entity;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @TableName category
 */
//@TableName(value ="category")
@Data
public class Category extends BaseEntity {
    /**
     *
     */
//    @TableId
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

//    @TableField(exist = false)
    private List<Category> children = new ArrayList<>();

}
