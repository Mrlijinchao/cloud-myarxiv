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
 * @TableName subject_category
 */
@TableName(value ="subject_category")
@Data
public class SubjectCategory extends BaseEntity {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private Long subjectId;

    /**
     * 顶层分类的id，即parent_id为0
     */
    private Long categoryId;

    /**
     * 类型
     */
    private String type;

}
