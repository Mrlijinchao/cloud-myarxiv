package com.lijinchao.entity.dto;

import com.lijinchao.entity.Paper;
import com.lijinchao.entity.Submission;
import lombok.Data;

@Data
public class SubmissionDto {

    Long submissionId;

    /**
     * 形式例如：1，1，3
     * 1表示subjectId
     * 1表示categoryId
     * 3表示categoryId
     */
    String categoryValue;

    /**
     * 标识符，同一片论文不同的版本标识符一样，表示它们是同一篇论文的不同版本
     */
    String identifier;

}
