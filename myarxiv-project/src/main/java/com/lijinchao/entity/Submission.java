package com.lijinchao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.lijinchao.entity.dto.PaperDto;
import lombok.Data;

/**
 *
 * @TableName submission
 */
@TableName(value ="submission")
@Data
public class Submission extends BaseEntity {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     * 这个提交的论文ID
     */
    private Long paperId;

    /**
     *
     */
    private Long userId;

    /**
     *
     */
    private String submissionIdentifier;

    /**
     *
     */
    private String title;

    /**
     *
     */
    private String type;

    /**
     *
     */
    private Date expires;

    /**
     * 1表示同意
     */
    private Integer certifyInformation;

    /**
     * 1表示同意了条款
     */
    private Integer agreement;

    /**
     * Myself表示自己；Third-party表示第三方
     */
    private String authorship;

    /**
     * 许可ID
     */
    private Long licenseId;

    /**
     * 分类值
     */
    private String categoryValue;

    /**
     *
     */
    private Integer currentStep;

    @TableField(exist = false)
    private PaperDto paperDto;

}
