package com.lijinchao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName endorsement
 */
@TableName(value ="endorsement")
@Data
public class Endorsement implements Serializable {
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
    private Long endorserId;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户组织
     */
    private String userOrganization;

    /**
     * 学科和和一二级分类的Id字符串
     */
    private String subjectCategoryId;

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
