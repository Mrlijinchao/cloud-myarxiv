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
 * @TableName paper_category
 */
@TableName(value ="paper_category")
@Data
public class PaperCategory extends BaseEntity {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private Long paperId;

    /**
     *
     */
    private Long categoryId;

}
