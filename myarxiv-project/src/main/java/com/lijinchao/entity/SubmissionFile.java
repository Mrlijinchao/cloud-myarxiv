package com.lijinchao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 * @TableName submission_file
 */
@TableName(value ="submission_file")
@Data
public class SubmissionFile extends BaseEntity{
    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private Long submissionId;

    /**
     *
     */
    private Long fileId;

}
