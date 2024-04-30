package com.lijinchao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijinchao.entity.File;
import com.lijinchao.entity.Paper;
import com.lijinchao.entity.Submission;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lijinchao.entity.User;
import com.lijinchao.entity.dto.SubmissionDto;


import java.io.IOException;
import java.util.List;

/**
* @author 时之始
* @description 针对表【submission】的数据库操作Service
* @createDate 2024-03-30 15:32:55
*/
public interface SubmissionService extends IService<Submission> {

    /**
     * 创建一条提交信息
     * @param submission
     */
    Submission createSubmission(Submission submission,Long userId);

    /**
     * 查询已经存在的文件
     * @param submissionId
     * @return
     */
    List<File> getExistFile(Long submissionId);

    /**
     * 在论文提交的第四步保存paper或者更新paper
     * @param paper
     */
    Paper savePaper(Paper paper);

    /**
     * 提交论文
     * @param submissionDto
     * @param user
     */
    void submitPaper(SubmissionDto submissionDto, User user);

    /**
     * 查询已提交还未审核的论文
     * @return
     */
    List<Submission> getUnpublished(Boolean isSubmit,Long userId);

    /**
     * 查询已经提交还未审核的论文
     * @param pageSize
     * @param pageNum
     * @param subjectId
     * @return
     */
    Page<Submission> getSubmittedByPage(Integer pageSize,Integer pageNum,Long subjectId);

    /**
     * 根据Id删除submission
     * @param submission
     */
    void deleteSubmission(Submission submission) throws IOException;

    /**
     * 撤销提交，撤回审核
     * @param submission
     */
    void unSubmit(Submission submission);


}
