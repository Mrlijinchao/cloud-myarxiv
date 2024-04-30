package com.lijinchao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.File;
import com.lijinchao.entity.Paper;
import com.lijinchao.entity.Submission;
import com.lijinchao.entity.User;
import com.lijinchao.entity.dto.SubmissionDto;
import com.lijinchao.service.EndorsementService;
import com.lijinchao.service.SubmissionService;
import com.lijinchao.utils.BaseApiResult;
import com.lijinchao.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/submit")
public class SubmissionController {

    @Resource
    SubmissionService submissionService;

    @Resource
    EndorsementService endorsementService;

    @Resource
    RedisUtil redisUtil;

    /**
     * 如果无需担保就会创建一条submission记录
     * 如果需要担保则返回失败，用户会根据前端提示去找担保人担保
     * @param request
     * @param submission
     * @return
     */
    @PostMapping("/first")
    public BaseApiResult firstStep(HttpServletRequest request, @RequestBody Submission submission){
        try {
            String token = request.getHeader("authorization");
            User user = (User)redisUtil.getByToken(token);
            if(!endorsementService.checkEndorsement(user,submission.getCategoryValue())){
                return BaseApiResult.error(101,"您需要担保");
            }
            // 如果不需要担保就创建一条提交
            if(ObjectUtils.isEmpty(submission.getId())){
                Submission submission1 = submissionService.createSubmission(submission, user.getId());
                return BaseApiResult.success(submission1);
            }else{
                submissionService.updateById(submission);
                return BaseApiResult.success(submission);
            }
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }

    }

    /**
     * 获取一个提交所关联的文件
     * @param submissionId
     * @return
     */
    @GetMapping("/getExistFile")
    public BaseApiResult queryFileInfo(@RequestParam("submissionId") Long submissionId){
        try {
            List<File> existFile = submissionService.getExistFile(submissionId);
            return BaseApiResult.success(existFile);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }

    }

    /**
     * 获取提交信息
     * @param submissionId
     * @return
     */
    @GetMapping("/getSubmissionInfo")
    public BaseApiResult querySubmissionById(@RequestParam("submissionId") Long submissionId){
        try {
            Submission submission = submissionService.getById(submissionId);
            return BaseApiResult.success(submission);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }

    }

    /**
     * 在论文提交的第四步保存paper或者更新paper
     * @param paper
     * @return
     */
    @PostMapping("/fourth")
    public BaseApiResult fourthStep(@RequestBody Paper paper){
        try {
            Paper paper1 = submissionService.savePaper(paper);
            if(paper1 == null){
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
            }
            return BaseApiResult.success(paper1);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }

    }

    /**
     * 提交论文
     * @param submissionDto
     * @param request
     * @return
     */
    @PostMapping("")
    public BaseApiResult submitPaper(@RequestBody SubmissionDto submissionDto,HttpServletRequest request){
        try {
            String token = request.getHeader("authorization");
            User user = (User)redisUtil.getByToken(token);
            submissionService.submitPaper(submissionDto,user);
            return BaseApiResult.success();
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 查询未提交的论文和已提交未审核的论文
     * @param isSubmit
     * @param userId
     * @return
     */
    @GetMapping("/unpublished")
    public BaseApiResult queryUnpublished(@RequestParam Boolean isSubmit,@RequestParam Long userId){
        try {
            if(ObjectUtils.isEmpty(userId)){
                ArrayList<Submission> submissions = new ArrayList<>();
                return BaseApiResult.success(submissions);
            }
            List<Submission> submissionList = submissionService.getUnpublished(isSubmit, userId);
            return BaseApiResult.success(submissionList);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 检查这个submission是否已经提交过
     * @param submissionId
     * @return
     */
    @GetMapping("/checkIsSubmitted")
    public BaseApiResult checkIsSubmitted(@RequestParam Long submissionId){
        try {
            List<Submission> list = submissionService.list(new LambdaQueryWrapper<Submission>()
                    .eq(Submission::getId, submissionId).eq(Submission::getCurrentStep, 6));
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 后台查询用
     * @param pageSize
     * @param pageNum
     * @param subjectId
     * @return
     */
    @GetMapping("/querySubmittedByPage")
    public BaseApiResult querySubmittedByPage(@RequestParam(defaultValue = "10") Integer pageSize,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(required = false) Long subjectId){
        try {
            Page<Submission> submittedByPage = submissionService.getSubmittedByPage(pageSize, pageNum, subjectId);
            return BaseApiResult.success(submittedByPage);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 根据id删除
     * @param submission
     * @return
     */
    @DeleteMapping("/removeSubmission")
    public BaseApiResult removeSubmission(@RequestBody Submission submission){
        try {
            submissionService.deleteSubmission(submission);
            return BaseApiResult.success();
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 撤销已经提交的论文
     * @param submission
     * @return
     */
    @PutMapping("/unSubmit")
    public BaseApiResult unSubmit(@RequestBody Submission submission){
        try {
            submissionService.unSubmit(submission);
            return BaseApiResult.success();
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

}
