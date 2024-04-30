package com.lijinchao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijinchao.entity.File;
import com.lijinchao.entity.Paper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lijinchao.entity.PaperAudit;
import com.lijinchao.entity.User;
import com.lijinchao.entity.dto.PaperDto;
import com.lijinchao.utils.BaseApiResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.List;

/**
* @author 时之始
* @description 针对表【paper】的数据库操作Service
* @createDate 2024-01-03 08:28:19
*/
public interface PaperService extends IService<Paper> {

    /**
     * 根据用户id查询已发表论文
     * @param userId
     * @return
     */
    List<PaperDto> getPublishedPaperByUser(Long userId);

    /**
     * 根据id查询论文信息
     * @param paperId
     * @return
     */
    PaperDto getPaperInfoById(Long paperId);

    /**
     * 审核论文
     * @param paperAudit
     * @param user
     * @return
     */
    Boolean paperAudit(PaperAudit paperAudit, User user);

//    /**
//     * 论文
//     * @param paperAudit
//     * @param user
//     * @return
//     */
//    Boolean paperOnHold(PaperAudit paperAudit,User user);

    /**
     * 根据分类分页查询论文
     * @param categoryId
     * @param pageSize
     * @param pageNum
     * @param startTime
     * @param endTime
     * @return
     */
    Page<PaperDto> getPaperPageByCategory(Long categoryId,Integer pageSize,
                                          Integer pageNum,String startTime,
                                          String endTime) throws ParseException;

    /**
     * 查询已经存在的文件
     * @param paperId
     * @return
     */
    List<File> getExistFile(Long paperId);

    /**
     * 更新论文版本
     * @param paper
     * @param user
     * @return
     */
    Boolean updateVersion(Paper paper,User user);

    /**
     * 获取论文所有版本
     * @param paperId
     * @return
     */
    List<Paper> getPaperVersionAll(Long paperId);

    /**
     * 分页查询论文
     * @return
     */
    Page<PaperDto> getPaperPageByMultipleCondition(String title, String abstracts,
                                                   String identifier, String authors,
                                                   String comments, String acmClass,
                                                   String mscClass, String reportNumber,
                                                   String journalReference, String doi,
                                                   Long subjectId,Integer pageSize, Integer pageNum,
                                                   String startTime, String endTime) throws ParseException;

    /**
     * 包装paper
     * @param paperDtoList
     * @return
     */
    public List<PaperDto> wrapperPaper(List<PaperDto> paperDtoList);

    /**
     * 查询已经审核过的论文
     * @param pageSize
     * @param pageNum
     * @param verifierId
     * @return
     */
    Page<PaperDto> getReviewedPaper(Integer pageSize, Integer pageNum, Long verifierId);

    /**
     * 根据审核码和用户查询论文
     * @return
     */
    Page<PaperDto> queryPaper(Integer pageSize, Integer pageNum, String auditCode, Long userId);


}
