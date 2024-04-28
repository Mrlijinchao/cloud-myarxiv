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
 * @TableName paper_audit
 */
@TableName(value ="paper_audit")
@Data
public class PaperAudit extends BaseEntity {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     * 审核者id
     */
    private Long userId;

    /**
     *
     */
    private Long paperId;

    /**
     * 审核码,1001为未提交审核，1002为已提交审核，1003为审核通过、1004为审核不通过
     */
    private String auditCode;

    /**
     * 审核反馈信息
     */
    private String feedback;


}
