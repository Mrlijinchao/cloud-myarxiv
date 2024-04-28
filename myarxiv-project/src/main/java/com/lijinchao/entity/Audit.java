package com.lijinchao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName aduit
 */
@TableName(value ="aduit")
@Data
public class Audit extends BaseEntity {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private Long userId;

    /**
     *
     */
    private Long paperId;

    /**
     *
     */
    private Long submissionId;

    /**
     *
     */
    private Integer isPass;

    /**
     *
     */
    private String verifierReason;

}
