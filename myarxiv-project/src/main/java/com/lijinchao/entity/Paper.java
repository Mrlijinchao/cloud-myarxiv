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
 * @TableName paper
 */
@TableName(value ="paper")
@Data
public class Paper extends BaseEntity {
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
     *
     */
    private Long licenseId;

    /**
     * 论文提交信息id
     */
    private Long submissionId;

    /**
     * 论文默认分类
     */
    private Long categoryId;

    /**
     *
     */
    private String title;

    /**
     *
     */
    private String abstracts;

    /**
     *
     */
    private String identifier;

    /**
     *
     */
    private String authors;

    /**
     *
     */
    private String comments;

    /**
     *
     */
    private String acmClass;

    /**
     *
     */
    private String mscClass;

    /**
     *
     */
    private String reportNumber;

    /**
     *
     */
    private String journalReference;

    /**
     *
     */
    private String doi;

    /**
     * 为这篇论文文件hash和用户信息hash运行而成
     */
    private String hash;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 0表示为正在使用的版本，1表示为其他版本
     */
    private Integer usingVersion;

    /**
     * 同一篇论文不同版本出自同一篇论文，这个标识相同即可确定为同一篇论文
     */
    private String samePaperIdentifier;

    /**
     * 1001为未提交审核，1002为已提交审核，1003为审核通过、1004为审核不通过
     */
    private String auditStatus;

    /**
     *
     */
    private String remark;



}
