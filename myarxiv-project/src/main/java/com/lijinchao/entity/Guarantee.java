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
 * @TableName guarantee
 */
@TableName(value ="guarantee")
@Data
public class Guarantee implements Serializable {
    /**
     * 
     */
    @TableId
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 担保人id
     */
    private Long guaranteeId;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户组织
     */
    private String userOrgnization;

    /**
     * 
     */
    private String subjectCode;

    /**
     * 
     */
    private Date createDate;

    /**
     * 
     */
    private Long createStaff;

    /**
     * 
     */
    private Date updateDate;

    /**
     * 
     */
    private Long updateStaff;

    /**
     * 
     */
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}