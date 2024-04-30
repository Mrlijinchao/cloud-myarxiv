package com.lijinchao.entity;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @TableName subject
 */
//@TableName(value ="subject")
@Data
public class Subject extends BaseEntity {
    /**
     *
     */
//    @TableId
    private Long id;

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
    private List<Category> categoryList = new ArrayList<>();

}
