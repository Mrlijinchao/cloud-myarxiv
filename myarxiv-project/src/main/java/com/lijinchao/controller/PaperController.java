package com.lijinchao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.File;
import com.lijinchao.entity.Paper;
import com.lijinchao.entity.PaperAudit;
import com.lijinchao.entity.User;
import com.lijinchao.entity.dto.PaperDto;
import com.lijinchao.permission.Permission;
import com.lijinchao.permission.PermissionRoleEnum;
import com.lijinchao.service.LicenseService;
import com.lijinchao.service.PaperService;
import com.lijinchao.utils.BaseApiResult;
import com.lijinchao.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 关于论文的一些接口
 */
@Slf4j
@RestController
@RequestMapping("/paper")
public class PaperController {

    @Resource
    PaperService paperService;

    @Resource
    LicenseService licenseService;

    @Resource
    RedisUtil redisUtil;

    /**
     * 获取license
     * 如果id为null就获取全部
     * @param id
     * @return
     */
    @GetMapping("/license")
    public BaseApiResult getLicense(@RequestParam(required = false) Long id){
        try {
            return licenseService.getLicense(id);
        }catch (Exception e){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    @GetMapping("/getBySubmission")
    public BaseApiResult getPaperBySubmissionId(@RequestParam("submissionId") Long submissionId){
        try {
            List<Paper> list = paperService.list(new LambdaQueryWrapper<Paper>().eq(Paper::getSubmissionId, submissionId));
            if(CollectionUtils.isEmpty(list)){
                return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
            }
            return BaseApiResult.success(list.get(0));
        }catch (Exception e){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }

    }

    @GetMapping("/publishedPaper")
    public BaseApiResult queryPublishedPaperByUser(@RequestParam Long userId){
        try {
            List<PaperDto> publishedPaperByUser = paperService.getPublishedPaperByUser(userId);
            return BaseApiResult.success(publishedPaperByUser);
        }catch (Exception e){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    @GetMapping("/queryPaperInfoById")
    public BaseApiResult queryPaperInfoById(@RequestParam Long paperId){
        try {
            PaperDto paperDto = paperService.getPaperInfoById(paperId);
            if(ObjectUtils.isEmpty(paperDto)){
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
            }
            return BaseApiResult.success(paperDto);
        }catch (Exception e){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }
    @Permission(roleValue = {PermissionRoleEnum.AUDIT})
    @PostMapping("/audit")
    public BaseApiResult paperAudit(@RequestBody PaperAudit paperAudit, HttpServletRequest request){
        try {
            String token = request.getHeader("authorization");
            User user = (User)redisUtil.getByToken(token);
            Boolean aBoolean = paperService.paperAudit(paperAudit, user);
            if(aBoolean){
                return BaseApiResult.success("审核成功！");
            }
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }catch (Exception e){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    @GetMapping("/getPaperByPage")
    public BaseApiResult queryPaperPageByCategory(@RequestParam Long categoryId,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(required = false) String startTime,
                                                @RequestParam(required = false) String endTime){
        try {
            Page<PaperDto> paperPageByCategory = paperService.getPaperPageByCategory(categoryId, pageSize, pageNum, startTime, endTime);
            return BaseApiResult.success(paperPageByCategory);
        }catch (Exception e){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 获取一篇论文所关联的文件
     * @param paperId
     * @return
     */
    @GetMapping("/queryFileByPaperId")
    public BaseApiResult queryFileByPaperId(@RequestParam("paperId") Long paperId){
        try {
            List<File> existFile = paperService.getExistFile(paperId);
            return BaseApiResult.success(existFile);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }

    }

    /**
     * 更新论文版本
     * @return
     */
    @PostMapping("/updateVersion")
    public BaseApiResult updatePaperVersion(@RequestBody Paper paper,HttpServletRequest request){
        try {
            String token = request.getHeader("authorization");
            User user = (User)redisUtil.getByToken(token);
            paperService.updateVersion(paper,user);
            return BaseApiResult.success();
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 根据paperId查询所有版本论文
     * @return
     */
    @GetMapping("/queryPaperVersionAll")
    public BaseApiResult queryPaperVersionAll(Long paperId){
        try {
            return BaseApiResult.success(paperService.getPaperVersionAll(paperId));
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 多条件分页查询论文，如果不传参数就根据全部字段查询
     * @param title
     * @param abstracts
     * @param identifier
     * @param authors
     * @param comments
     * @param acmClass
     * @param mscClass
     * @param reportNumber
     * @param journalReference
     * @param doi
     * @return
     */
    @GetMapping("/queryPaperByCondition")
    public BaseApiResult queryPaperPageByMultipleCondition(@RequestParam(required = false) String title,
                                                           @RequestParam(required = false) String abstracts,
                                                           @RequestParam(required = false) String identifier,
                                                           @RequestParam(required = false) String authors,
                                                           @RequestParam(required = false) String comments,
                                                           @RequestParam(required = false) String acmClass,
                                                           @RequestParam(required = false) String mscClass,
                                                           @RequestParam(required = false) String reportNumber,
                                                           @RequestParam(required = false) String journalReference,
                                                           @RequestParam(required = false) String doi,
                                                           @RequestParam(required = false) Long subjectId,
                                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                                           @RequestParam(required = false) String startTime,
                                                           @RequestParam(required = false) String endTime){
        try {
            if(!StringUtils.hasText(title) && !StringUtils.hasText(abstracts) &&
                    !StringUtils.hasText(identifier) && !StringUtils.hasText(mscClass) &&
                    !StringUtils.hasText(authors) && !StringUtils.hasText(reportNumber) &&
                    !StringUtils.hasText(comments) && !StringUtils.hasText(journalReference) &&
                    !StringUtils.hasText(acmClass) && !StringUtils.hasText(startTime) &&
                    !StringUtils.hasText(doi) && !StringUtils.hasText(endTime) &&
                    ObjectUtils.isEmpty(subjectId)){
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
            }
            Page<PaperDto> page = paperService.getPaperPageByMultipleCondition(title, abstracts, identifier, authors,
                    comments, acmClass, mscClass, reportNumber, journalReference, doi,subjectId, pageSize, pageNum, startTime, endTime);
            return BaseApiResult.success(page);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

}
