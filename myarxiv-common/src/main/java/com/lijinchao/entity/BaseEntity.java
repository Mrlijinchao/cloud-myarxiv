package com.lijinchao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public abstract class BaseEntity implements Serializable {

    /**
     * 状态编码
     */
    @TableField(fill = FieldFill.INSERT)
    private String statusCd;

    /**
     * 修改人id
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateStaff;

    /**
     * 修改人名称
     */
    @TableField(exist = false)
    private String updateName;

    /**
     * 修改日期
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    /**
     * 创建人id
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createStaff;

    /**
     * 创建人姓名
     */
    @TableField(exist = false)
    private String createName;

    /**
     * 创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * 删除标志
     */
    @TableLogic
//    @TableField(fill = FieldFill.INSERT)
    private Boolean isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
