package com.lijinchao.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.entity.File;
import com.lijinchao.entity.Paper;
import com.lijinchao.entity.PaperCategory;
import com.lijinchao.entity.PaperFile;
import com.lijinchao.entity.Submission;
import com.lijinchao.entity.SubmissionFile;
import com.lijinchao.entity.User;
import com.lijinchao.entity.UserPaper;
import com.lijinchao.entity.dto.SubmissionDto;
import com.lijinchao.enums.GlobalEnum;
import com.lijinchao.mapper.PaperMapper;
import com.lijinchao.service.FileService;
import com.lijinchao.service.PaperCategoryService;
import com.lijinchao.service.PaperFileService;
import com.lijinchao.service.PaperService;
import com.lijinchao.service.SubmissionFileService;
import com.lijinchao.service.SubmissionService;
import com.lijinchao.mapper.SubmissionMapper;
import com.lijinchao.service.UserPaperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
* @author 时之始
* @description 针对表【submission】的数据库操作Service实现
* @createDate 2024-03-30 15:32:55
*/
@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission>
    implements SubmissionService{

    @Resource
    SubmissionFileService submissionFileService;

    @Resource
    FileService fileService;

    @Resource
    PaperService paperService;

    @Resource
    SubmissionService submissionService;

    @Resource
    PaperMapper paperMapper;

    @Resource
    UserPaperService userPaperService;

    @Resource
    PaperCategoryService paperCategoryService;

    @Resource
    PaperFileService paperFileService;

    @Transactional
    @Override
    public Submission createSubmission(Submission submission,Long userId) {
        submission.setExpires(new Date((new Date()).getTime() + 10 * 24 * 60 * 60 * 1000));
        submission.setType("New");
        submission.setUserId(userId);
        this.save(submission);
        submission.setSubmissionIdentifier("submit/" + submission.getId());
        submission.setTitle("Submission ID:" + submission.getId());
        this.updateById(submission);
        return submission;
    }

    @Override
    public List<File> getExistFile(Long submissionId) {

        if(ObjectUtils.isEmpty(submissionId)){
            return new ArrayList<>();
        }

        List<SubmissionFile> submissionFileList = submissionFileService.list(new LambdaQueryWrapper<SubmissionFile>()
                .eq(SubmissionFile::getSubmissionId, submissionId));

        if(CollectionUtils.isEmpty(submissionFileList)){
            return new ArrayList<>();
        }

        List<Long> ids = submissionFileList.stream().map(SubmissionFile::getFileId).collect(Collectors.toList());

        List<File> fileList = fileService.listByIds(ids);

        return fileList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Paper savePaper(Paper paper) {
        if(ObjectUtils.isEmpty(paper)){
            return null;
        }
        if(ObjectUtils.isEmpty(paper.getSubmissionId())){
            return null;
        }
        List<Paper> list = paperService.list(new LambdaQueryWrapper<Paper>()
                .eq(Paper::getSubmissionId, paper.getSubmissionId()));
        // 如果paper已经存在就更新paper
        if(!CollectionUtils.isEmpty(list)){
            Paper paper1 = list.get(0);
            paper.setId(paper1.getId());
            paperService.updateById(paper);
            return paper;
        }
        // 2000 表示篇论文还未提交完善
        paper.setStatusCd(GlobalEnum.EFFECT.getCode());
        // 1001表示论文未提交审核   1001为未提交审核，1002为已提交审核，1003为审核通过、1004为审核不通过
        paper.setAuditStatus(String.valueOf(1001));
//        paper.setUsingVersion(1);
        // 如果论文不是第一版前端就会传来一个 identifier和上一版相同，否则则自己生成
        if(!StringUtils.hasText(paper.getSamePaperIdentifier())){
            String identifier = UUID.randomUUID().toString().replaceAll("-", "");
            paper.setSamePaperIdentifier(identifier);
        }
        // 如果paper不存在就新增
        paperService.save(paper);

        Submission submission = submissionService.getById(paper.getSubmissionId());
        submission.setPaperId(paper.getId());
        submissionService.updateById(submission);

        return paper;
//        if(ObjectUtils.isEmpty(paper.getId()))
//        Paper paper1 = paperService.getById(paper.getId());
//
//        String md51 = SecureUtil.md5(String.valueOf(paper1));
//        String md5 = SecureUtil.md5(String.valueOf(paper));
//        if(md5.equals(md51)){
//            return;
//        }
//        paperService.updateById(paper);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void submitPaper(SubmissionDto submissionDto, User user) {
        if(ObjectUtils.isEmpty(submissionDto) || ObjectUtils.isEmpty(user)){
            return;
        }
        Submission submission = submissionService.getById(submissionDto.getSubmissionId());
        // 填充submission信息
        // currentStep=6表示这条提交已经完成
        submission.setCurrentStep(6);
        submission.setUserId(user.getId());
        submission.setStatusCd(GlobalEnum.EFFECT.getCode());
        submissionService.updateById(submission);


        // 填充paper信息
        // 设置版本号  TODO 后面审核的时候如果审核通过就会把usingVersion设置为0
        Paper currentPaper = paperService.getById(submission.getPaperId());
        List<Paper> list = paperService.list(new LambdaQueryWrapper<Paper>()
                .eq(Paper::getSamePaperIdentifier, currentPaper.getSamePaperIdentifier())
                .eq(Paper::getUsingVersion, 0));
        // 如果是第一版list就会为空
        if(CollectionUtils.isEmpty(list)){
            currentPaper.setVersion(1);
        }else{
            Paper paper = list.get(0);
            currentPaper.setVersion(paper.getVersion() + 1);
        }

        // 设置 subjectId、categoryId、licenseId
        currentPaper.setLicenseId(submission.getLicenseId());
        String categoryValue = submission.getCategoryValue();
        int i = categoryValue.lastIndexOf(',');
        String category = categoryValue.substring(i+1);
        // 设置具体的category
        currentPaper.setCategoryId(Long.parseLong(category));
        // 设置学科subject
        int i1 = categoryValue.indexOf(',');
        String subject = categoryValue.substring(0, i1);
        currentPaper.setSubjectId(Long.parseLong(subject));

        // 设置审核状态 提交未审核
        currentPaper.setAuditStatus(String.valueOf(1002));

        // 设置identifier
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DATE);
        Long count = paperMapper.getCount();
        currentPaper.setIdentifier("myArxiv:" + year + "." + month + "." + count);

        paperService.updateById(currentPaper);

        // 把paper和file、user、交叉category联系起来
        paperRelation(submissionDto.getSubmissionId(),submissionDto.getCategoryValue(),user.getId(),currentPaper.getId());

    }

    @Override
    public List<Submission> getUnpublished(Boolean isSubmit,Long userId) {
        if(isSubmit){
            // 先查询用户下面的paperId，然后再根据paper的审核码是不是1002(提交未审核)筛选出满足的paperId，最后根据这些PaperId查询submission
            List<UserPaper> userPaperList = userPaperService.list(new LambdaQueryWrapper<UserPaper>()
                    .eq(UserPaper::getUserId, userId));
            if(CollectionUtils.isEmpty(userPaperList)){
                return new ArrayList<>();
            }
            List<Long> paperIds = userPaperList.stream().map(UserPaper::getPaperId).collect(Collectors.toList());

            List<Paper> paperList = paperService.list(new LambdaQueryWrapper<Paper>()
                    .in(Paper::getId, paperIds).eq(Paper::getAuditStatus, 1002));

            if(CollectionUtils.isEmpty(paperList)){
                return new ArrayList<>();
            }
            List<Long> paperIdList = paperList.stream().map(Paper::getId).collect(Collectors.toList());
            List<Submission> submissionList = submissionService.list(new LambdaQueryWrapper<Submission>()
                    .in(Submission::getPaperId, paperIdList));

            return submissionList;
        }else{
            // 直接查询提交步骤不为6的（6表示论文已经提交）
            List<Submission> submissionList = this.list(new LambdaQueryWrapper<Submission>()
                    .ne(Submission::getCurrentStep, 6).eq(Submission::getUserId,userId));
            return submissionList;
        }
    }

    public void paperRelation(Long submissionId,String categoryValue,Long userId,Long paperId){
        // 保存用户和paper的关系
        List<UserPaper> userPaperList = userPaperService.list(new LambdaQueryWrapper<UserPaper>()
                .eq(UserPaper::getPaperId, paperId).eq(UserPaper::getUserId, userId));
        if(CollectionUtils.isEmpty(userPaperList)){
            UserPaper userPaper = new UserPaper();
            userPaper.setPaperId(paperId);
            userPaper.setUserId(userId);
            userPaperService.save(userPaper);
        }

        // 保存paper的交叉类和paper的关系
        if(StringUtils.hasText(categoryValue)){
            PaperCategory paperCategory = new PaperCategory();
            int i = categoryValue.lastIndexOf(',');
            String category = categoryValue.substring(i+1);
            long categoryId = Long.parseLong(category);
            // 设置具体的category
            List<PaperCategory> paperCategoryList = paperCategoryService.list(new LambdaQueryWrapper<PaperCategory>()
                    .eq(PaperCategory::getPaperId, paperId).eq(PaperCategory::getCategoryId, categoryId));
            if(CollectionUtils.isEmpty(paperCategoryList)){
                paperCategory.setCategoryId(categoryId);
                paperCategory.setPaperId(paperId);
                paperCategoryService.save(paperCategory);
            }
        }



        // 保存paper和文件的关系
        List<SubmissionFile> submissionFileList = submissionFileService.list(new LambdaQueryWrapper<SubmissionFile>()
                .eq(SubmissionFile::getSubmissionId, submissionId));
        if(CollectionUtils.isEmpty(submissionFileList)){
            return;
        }
        Set<Long> ids = submissionFileList.stream().map(SubmissionFile::getFileId).collect(Collectors.toSet());
        List<PaperFile> paperFileList = paperFileService.list(new LambdaQueryWrapper<PaperFile>()
                .eq(PaperFile::getPaperId, paperId));
        ArrayList<Long> paperFileIds = new ArrayList<>();
        if(!CollectionUtils.isEmpty(paperFileList)){
            Set<Long> collect = paperFileList.stream().map(PaperFile::getFileId).collect(Collectors.toSet());
            paperFileIds.addAll(collect);
        }
        // 求submission关联的文件对paper已经关联的文件的差集即为需要插入的数据
        ids.removeAll(paperFileIds);

        ArrayList<PaperFile> paperFileArrayList = new ArrayList<>();
        for(Long id : ids){
            PaperFile paperFile = new PaperFile();
            paperFile.setFileId(id);
            paperFile.setPaperId(paperId);
            paperFileArrayList.add(paperFile);
        }
        paperFileService.saveBatch(paperFileArrayList);

    }

//    public static void main(String[] args) {
////        String s = "123,223,234";
////        System.out.println(s.lastIndexOf(','));
////        System.out.println(s.substring(7+1));
////        System.out.println(s.indexOf(','));
////        System.out.println(s.substring(0,3));
//
//        HashSet<Integer> set1 = new HashSet<>();
//        HashSet<Integer> set2 = new HashSet<>();
//        set1.add(1);
//        set1.add(2);
//        set1.add(3);
//        set1.add(4);
//        set2.add(1);
//        set2.add(2);
////        System.out.println(set2);
////        set2.retainAll(set1);
////        System.out.println(set2);
//        System.out.println(set1);
//        set1.removeAll(set2);
//        System.out.println(set1);
//
//    }

}




