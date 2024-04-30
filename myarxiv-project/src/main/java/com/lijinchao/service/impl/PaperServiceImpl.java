package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.entity.Category;
import com.lijinchao.entity.File;
import com.lijinchao.entity.License;
import com.lijinchao.entity.Paper;
import com.lijinchao.entity.PaperAudit;
import com.lijinchao.entity.PaperCategory;
import com.lijinchao.entity.PaperFile;
import com.lijinchao.entity.Subject;
import com.lijinchao.entity.SubmissionFile;
import com.lijinchao.entity.User;
import com.lijinchao.entity.UserPaper;
import com.lijinchao.entity.dto.PaperDto;
import com.lijinchao.enums.GlobalEnum;
import com.lijinchao.service.CategoryService;
import com.lijinchao.service.FileService;
import com.lijinchao.service.LicenseService;
import com.lijinchao.service.PaperAuditService;
import com.lijinchao.service.PaperCategoryService;
import com.lijinchao.service.PaperFileService;
import com.lijinchao.service.PaperService;
import com.lijinchao.mapper.PaperMapper;
import com.lijinchao.service.SubjectService;
import com.lijinchao.service.UserPaperService;
import com.lijinchao.utils.BaseApiResult;
import com.lijinchao.utils.BeanUtilCopy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author 时之始
* @description 针对表【paper】的数据库操作Service实现
* @createDate 2024-01-03 08:28:19
*/
@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper>
    implements PaperService{

    @Resource
    UserPaperService userPaperService;

    @Resource
    LicenseService licenseService;

    @Resource
    SubjectService subjectService;

    @Resource
    CategoryService categoryService;

    @Resource
    PaperCategoryService paperCategoryService;

    @Resource
    PaperAuditService paperAuditService;

    @Resource
    PaperMapper paperMapper;

    @Resource
    PaperFileService paperFileService;

    @Resource
    FileService fileService;

    @Override
    public List<PaperDto> getPublishedPaperByUser(Long userId) {
        if(ObjectUtils.isEmpty(userId)){
            return new ArrayList<>();
        }

        List<UserPaper> userPaperList = userPaperService.list(new LambdaQueryWrapper<UserPaper>()
                .eq(UserPaper::getUserId, userId));

        if(CollectionUtils.isEmpty(userPaperList)){
            return new ArrayList<>();
        }

        List<Long> paperIds = userPaperList.stream().map(UserPaper::getPaperId).collect(Collectors.toList());

        // 需要审核通过（AuditStatus为1003），为当前使用版本（usingVersion为0）
        List<Paper> paperList = this.list(new LambdaQueryWrapper<Paper>()
                .eq(Paper::getAuditStatus, 1003).eq(Paper::getUsingVersion,0).in(Paper::getId, paperIds));
//        List<Paper> paperList = this.listByIds(paperIds);

        if(CollectionUtils.isEmpty(paperList)){
            return new ArrayList<>();
        }
        // 复制paperList
        List<PaperDto> paperDtoList = BeanUtilCopy.copyListProperties(paperList, PaperDto::new);

        // TODO wrapperPaper

        return paperDtoList;
    }

    @Override
    public PaperDto getPaperInfoById(Long paperId) {
        if(ObjectUtils.isEmpty(paperId)){
            return null;
        }
        Paper paper = this.getById(paperId);
        PaperDto paperDto = new PaperDto();
        BeanUtilCopy.copyProperties(paper,paperDto);
        ArrayList<PaperDto> paperDtos = new ArrayList<>();
        paperDtos.add(paperDto);
        List<PaperDto> paperDtoList = wrapperPaper(paperDtos);
        if(CollectionUtils.isEmpty(paperDtoList)){
            return null;
        }
        return paperDtoList.get(0);
    }

    @Override
    public List<PaperDto> wrapperPaper(List<PaperDto> paperDtoList){
        if(CollectionUtils.isEmpty(paperDtoList)){
            return new ArrayList<>();
        }

        // 先封装 license、subject、PrimaryCategory信息
        List<Long> licenseIds = paperDtoList.stream().map(PaperDto::getLicenseId).collect(Collectors.toList());
        List<Long> subjectIds = paperDtoList.stream().map(PaperDto::getSubjectId).collect(Collectors.toList());
        List<Long> categoryIds = paperDtoList.stream().map(PaperDto::getCategoryId).collect(Collectors.toList());

//        Map<Long, PaperDto> paperDtoLicenseMap = paperDtoList.stream()
//                .collect(Collectors.toMap(PaperDto::getLicenseId, Function.identity()));
//        Map<Long, PaperDto> paperDtoSubjectMap = paperDtoList.stream()
//                .collect(Collectors.toMap(PaperDto::getSubjectId, Function.identity()));
//        Map<Long, PaperDto> paperDtoCategoryMap = paperDtoList.stream()
//                .collect(Collectors.toMap(PaperDto::getCategoryId, Function.identity()));

        List<License> licenseList = licenseService.listByIds(licenseIds);
        List<Subject> subjectList = subjectService.listByIds(subjectIds);
        List<Category> categoryList = categoryService.listByIds(categoryIds);

        Map<Long, License> licenseMap = licenseList.stream().collect(Collectors.toMap(License::getId, Function.identity()));
        Map<Long, Subject> subjectMap = subjectList.stream().collect(Collectors.toMap(Subject::getId, Function.identity()));
        Map<Long, Category> categoryMap1 = categoryList.stream().collect(Collectors.toMap(Category::getId, Function.identity()));

        for(PaperDto paperDto : paperDtoList){
            License license = licenseMap.get(paperDto.getLicenseId());
            paperDto.setLicense(license);
            Subject subject = subjectMap.get(paperDto.getSubjectId());
            paperDto.setSubject(subject);
            Category category = categoryMap1.get(paperDto.getCategoryId());
            paperDto.setPrimaryCategory(category);
        }

//        for(License license : licenseList){
//            PaperDto paperDto = paperDtoLicenseMap.get(license.getId());
//            if(!ObjectUtils.isEmpty(paperDto)){
//                paperDto.setLicense(license);
//            }
//        }
//        for(Subject subject : subjectList){
//            PaperDto paperDto = paperDtoSubjectMap.get(subject.getId());
//            if(!ObjectUtils.isEmpty(paperDto)){
//                paperDto.setSubject(subject);
//            }
//        }
//        for(Category category : categoryList){
//            PaperDto paperDto = paperDtoCategoryMap.get(category.getId());
//            if(!ObjectUtils.isEmpty(paperDto)){
//                paperDto.setPrimaryCategory(category);
//                // TODO 封装subjectCategory
//
//            }
//        }

        // TODO 封装submission

        // 封装交叉类的信息
        List<Long> paperIds = paperDtoList.stream().map(PaperDto::getId).collect(Collectors.toList());

        List<PaperCategory> paperCategoryList = paperCategoryService.list(new LambdaQueryWrapper<PaperCategory>()
                .in(PaperCategory::getPaperId, paperIds));

        if(CollectionUtils.isEmpty(paperCategoryList)){
            return paperDtoList;
        }

        List<Long> paperCategoryIds = paperCategoryList.stream().map(PaperCategory::getCategoryId)
                .collect(Collectors.toList());
        List<Category> categories = categoryService.listByIds(paperCategoryIds);
        Map<Long, Category> categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, Function.identity()));

        if(CollectionUtils.isEmpty(categories)){
            return paperDtoList;
        }

        for(PaperDto paperDto : paperDtoList){

            for(PaperCategory paperCategory : paperCategoryList){
                if(paperDto.getId().equals(paperCategory.getPaperId())){
                    Category category = categoryMap.get(paperCategory.getCategoryId());
                    paperDto.getCrossCategoryList().add(category);
                }
            }

        }

        // 封装文件信息
        List<PaperFile> paperFileList = paperFileService.list(new LambdaQueryWrapper<PaperFile>()
                .in(PaperFile::getPaperId, paperIds));
        if(CollectionUtils.isEmpty(paperFileList)){
            return paperDtoList;
        }
        List<Long> paperFileIds = paperFileList.stream().map(PaperFile::getFileId).collect(Collectors.toList());

        List<File> fileList = fileService.listByIds(paperFileIds);

        if(CollectionUtils.isEmpty(fileList)){
            return paperDtoList;
        }

        Map<Long, File> fileMap = fileList.stream().collect(Collectors.toMap(File::getId, Function.identity()));

        for(PaperDto paperDto : paperDtoList){
            for(PaperFile paperFile : paperFileList){
                if(paperDto.getId().equals(paperFile.getPaperId())){
                    paperDto.getFileList().add(fileMap.get(paperFile.getFileId()));
                }
            }
        }


        return paperDtoList;

    }


    @Override
    public Boolean paperAudit(PaperAudit paperAudit, User user) {
        // 将paper里面的审核状态改了
        this.update(new LambdaUpdateWrapper<Paper>().eq(Paper::getId,paperAudit.getPaperId())
                .set(Paper::getAuditStatus,paperAudit.getAuditCode())
                .set(Paper::getUsingVersion,0));
        paperAudit.setUserId(user.getId());
        paperAudit.setStatusCd(GlobalEnum.EFFECT.getCode());
        List<PaperAudit> list = paperAuditService.list(new LambdaQueryWrapper<PaperAudit>()
                .eq(PaperAudit::getPaperId, paperAudit.getPaperId()));
        if(CollectionUtils.isEmpty(list)){
            // 保存审核信息
            paperAuditService.save(paperAudit);
        }else{
            PaperAudit paperAudit1 = list.get(0);
            paperAudit.setId(paperAudit1.getId());
            paperAuditService.updateById(paperAudit);
        }

        return true;
    }

    @Override
    public Page<PaperDto> getPaperPageByCategory(Long categoryId, Integer pageSize, Integer pageNum, String startTime, String endTime) throws ParseException {

        Category category = categoryService.getById(categoryId);
        List<Category> categoryList = categoryService.list();
        ArrayList<Long> categoryIds = new ArrayList<>();
        if(ObjectUtils.isEmpty(category) || CollectionUtils.isEmpty(categoryList)){
            return new Page<>();
        }
        // 找出这个分类以及其子分类的id
        categoryIds.add(category.getId());
        for(Category category1 : categoryList){
            if(category1.getParentId().equals(category.getId())){
                categoryIds.add(category1.getId());
            }
        }

        LambdaQueryWrapper<Paper> lambdaQueryWrapper = new LambdaQueryWrapper<Paper>()
                .eq(Paper::getAuditStatus, 1003)
                .eq(Paper::getUsingVersion, 0)
                .in(Paper::getCategoryId, categoryIds);

//        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
//        Date startDate = sdf.parse(startTime);
//        Date endDate = sdf.parse(endTime);
//        lambdaQueryWrapper.le(Paper::getUpdateDate,endDate);
//        lambdaQueryWrapper.ge(Paper::getUpdateDate,startDate);

        if(StringUtils.hasText(startTime)){
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = sdf.parse(startTime);
            lambdaQueryWrapper.ge(Paper::getUpdateDate,startDate);
        }

        if(StringUtils.hasText(endTime)){
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
            Date endDate = sdf.parse(endTime);
            lambdaQueryWrapper.le(Paper::getUpdateDate,endDate);
        }

        // 查询paper信息
        Page<Paper> page = new Page<>(pageNum, pageSize);
        Page<Paper> paperPage = paperMapper.selectPage(page, lambdaQueryWrapper);
        List<Paper> paperList = paperPage.getRecords();
        List<PaperDto> paperDtoList = BeanUtilCopy.copyListProperties(paperList, PaperDto::new);

        // 填充数据
        List<PaperDto> paperDtos = wrapperPaper(paperDtoList);
        Page<PaperDto> paperDtoPage = new Page<>();
        BeanUtilCopy.copyProperties(paperPage,paperDtoPage);
        paperDtoPage.setRecords(paperDtos);
        return paperDtoPage;
    }

    @Override
    public List<File> getExistFile(Long paperId) {
        if(ObjectUtils.isEmpty(paperId)){
            return new ArrayList<>();
        }
        // 这里 statusCd为1000表示正常，2000为更新版本时候用的
        List<PaperFile> paperFileList = paperFileService.list(new LambdaQueryWrapper<PaperFile>()
                .eq(PaperFile::getPaperId, paperId).eq(PaperFile::getStatusCd,"1000"));

        if(CollectionUtils.isEmpty(paperFileList)){
            return new ArrayList<>();
        }

        List<Long> fileIds = paperFileList.stream().map(PaperFile::getFileId).collect(Collectors.toList());

        List<File> fileList = fileService.listByIds(fileIds);

        return fileList;
    }

    @Override
    public Boolean updateVersion(Paper paper, User user) {
        if(ObjectUtils.isEmpty(paper) || ObjectUtils.isEmpty(paper.getId())){
            return false;
        }

        Paper lastPaper = this.getById(paper.getId());
        paper.setId(null);

        // 设置最新版本数据
        paper.setVersion(lastPaper.getVersion() + 1);
        paper.setSamePaperIdentifier(lastPaper.getSamePaperIdentifier());
        paper.setUsingVersion(0);
        // 目前更新版本不审核，直接设置审核为通过
        paper.setAuditStatus(String.valueOf(1003));
        paper.setSubjectId(lastPaper.getSubjectId());
        paper.setCategoryId(lastPaper.getCategoryId());
        paper.setLicenseId(lastPaper.getLicenseId());
        paper.setTitle(lastPaper.getTitle());
        paper.setStatusCd(GlobalEnum.EFFECT.getCode());
        // 暂且以第一版为准
        paper.setIdentifier(lastPaper.getIdentifier());
        this.save(paper);


        // 修改上一版本数据
        lastPaper.setUsingVersion(1);
        this.updateById(lastPaper);

        // 把新版本paper和file、category、交叉category关联起来
        paperRelation(user.getId(),paper.getId(),lastPaper.getId());

        return true;
    }


    public void paperRelation(Long userId,Long paperId,Long lastPaperId){
        // 保存用户和paper的关系
        UserPaper userPaper = new UserPaper();
        userPaper.setPaperId(paperId);
        userPaper.setUserId(userId);
        userPaperService.save(userPaper);

        // 保存paper的交叉类和paper的关系
        List<PaperCategory> paperCategoryList = paperCategoryService.list(new LambdaQueryWrapper<PaperCategory>()
                .eq(PaperCategory::getPaperId, lastPaperId));

        if(!CollectionUtils.isEmpty(paperCategoryList)){

            Set<Long> ids = paperCategoryList.stream().map(PaperCategory::getCategoryId).collect(Collectors.toSet());
            ArrayList<PaperCategory> paperCategories = new ArrayList<>();

            for(Long id : ids){
                PaperCategory paperCategory = new PaperCategory();
                paperCategory.setPaperId(paperId);
                paperCategory.setCategoryId(id);
                paperCategories.add(paperCategory);
            }
            paperCategoryService.saveBatch(paperCategories);
        }


        // 保存paper和文件的关系
        List<PaperFile> paperFileList = paperFileService.list(new LambdaQueryWrapper<PaperFile>()
                .eq(PaperFile::getPaperId, lastPaperId));
        if(CollectionUtils.isEmpty(paperFileList)){
            return;
        }
        Set<Long> ids = paperFileList.stream().map(PaperFile::getFileId).collect(Collectors.toSet());
        ArrayList<PaperFile> paperFileArrayList = new ArrayList<>();
        for(Long id : ids){
            PaperFile paperFile = new PaperFile();
            paperFile.setFileId(id);
            paperFile.setPaperId(paperId);
            paperFileArrayList.add(paperFile);
        }
        paperFileService.saveBatch(paperFileArrayList);

    }

    @Override
    public List<Paper> getPaperVersionAll(Long paperId) {
        if(ObjectUtils.isEmpty(paperId)){
            return new ArrayList<>();
        }
        Paper paper = this.getById(paperId);
        if(ObjectUtils.isEmpty(paper)){
            return new ArrayList<>();
        }
        List<Paper> paperList = this.list(new LambdaQueryWrapper<Paper>()
                .eq(Paper::getSamePaperIdentifier, paper.getSamePaperIdentifier()));
        return paperList;
    }

    @Override
    public Page<PaperDto> getPaperPageByMultipleCondition(String title, String abstracts,
                                                          String identifier, String authors,
                                                          String comments, String acmClass,
                                                          String mscClass, String reportNumber,
                                                          String journalReference, String doi,
                                                          Long subjectId,Integer pageSize, Integer pageNum,
                                                          String startTime, String endTime) throws ParseException {

        LambdaQueryWrapper<Paper> lambdaQueryWrapper = new LambdaQueryWrapper<Paper>()
                .eq(Paper::getAuditStatus, 1003)
                .eq(Paper::getUsingVersion, 0);
        // 如果有subjectId就固定查询这个学科下的论文
        if(!ObjectUtils.isEmpty(subjectId)){
            lambdaQueryWrapper.eq(Paper::getSubjectId,subjectId);
        }
        if(StringUtils.hasText(title) && StringUtils.hasText(abstracts) &&
                StringUtils.hasText(identifier) && StringUtils.hasText(mscClass) &&
                StringUtils.hasText(authors) && StringUtils.hasText(reportNumber) &&
                StringUtils.hasText(comments) && StringUtils.hasText(journalReference) &&
                StringUtils.hasText(acmClass) && StringUtils.hasText(doi)){

            lambdaQueryWrapper.and(o -> o.like(Paper::getTitle,title).or().like(Paper::getAbstracts,abstracts)
                    .or().like(Paper::getIdentifier,identifier).or().like(Paper::getAuthors,authors)
                    .or().like(Paper::getComments,comments).or().like(Paper::getAcmClass,acmClass)
                    .or().like(Paper::getMscClass,mscClass).or().like(Paper::getReportNumber,reportNumber)
                    .or().like(Paper::getJournalReference,journalReference).or().like(Paper::getDoi,doi));
        }else{
            if(StringUtils.hasText(title)){
                lambdaQueryWrapper.like(Paper::getTitle,title);
            }
            if(StringUtils.hasText(abstracts)){
                lambdaQueryWrapper.like(Paper::getAbstracts,abstracts);
            }
            if(StringUtils.hasText(identifier)){
                lambdaQueryWrapper.like(Paper::getIdentifier,identifier);
            }
            if(StringUtils.hasText(authors)){
                lambdaQueryWrapper.like(Paper::getAuthors,authors);
            }
            if(StringUtils.hasText(comments)){
                lambdaQueryWrapper.like(Paper::getComments,comments);
            }
            if(StringUtils.hasText(acmClass)){
                lambdaQueryWrapper.like(Paper::getAcmClass,acmClass);
            }
            if(StringUtils.hasText(mscClass)){
                lambdaQueryWrapper.like(Paper::getMscClass,mscClass);
            }
            if(StringUtils.hasText(reportNumber)){
                lambdaQueryWrapper.like(Paper::getReportNumber,reportNumber);
            }
            if(StringUtils.hasText(journalReference)){
                lambdaQueryWrapper.like(Paper::getJournalReference,journalReference);
            }
            if(StringUtils.hasText(doi)){
                lambdaQueryWrapper.like(Paper::getDoi,doi);
            }
            if(StringUtils.hasText(startTime)){
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = sdf.parse(startTime);
                lambdaQueryWrapper.ge(Paper::getUpdateDate,startDate);
            }
            if(StringUtils.hasText(endTime)){
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
                Date endDate = sdf.parse(endTime);
                lambdaQueryWrapper.le(Paper::getUpdateDate,endDate);
            }
        }


            Page<Paper> page = new Page<>(pageNum, pageSize);

            Page<Paper> paperPage = paperMapper.selectPage(page, lambdaQueryWrapper);
            List<Paper> paperList = paperPage.getRecords();
            Page<PaperDto> paperDtoPage = new Page<>();
            BeanUtilCopy.copyProperties(paperPage,paperDtoPage);
            if(CollectionUtils.isEmpty(paperList)){
                return paperDtoPage;
            }

            // 填充数据
            List<PaperDto> paperDtoList = BeanUtilCopy.copyListProperties(paperList, PaperDto::new);
            List<PaperDto> paperDtos = wrapperPaper(paperDtoList);
            paperDtoPage.setRecords(paperDtos);
            return paperDtoPage;
    }

    @Override
    public Page<PaperDto> getReviewedPaper(Integer pageSize, Integer pageNum, Long verifierId) {

        Page<Paper> page = new Page<>(pageNum, pageSize);

        // 如果有带审核者id就查询其关联的论文，否则查询全部
        if(!ObjectUtils.isEmpty(verifierId)){

            List<PaperAudit> paperAuditList = paperAuditService.list(new LambdaQueryWrapper<PaperAudit>()
                    .eq(PaperAudit::getUserId, verifierId));

            if(CollectionUtils.isEmpty(paperAuditList)){
                return new Page<PaperDto>(pageNum,pageSize);
            }
            List<Long> paperIds = paperAuditList.stream().map(PaperAudit::getPaperId).collect(Collectors.toList());

            LambdaQueryWrapper<Paper> lambdaQueryWrapper = new LambdaQueryWrapper<Paper>()
            .eq(Paper::getAuditStatus, "1003").in(Paper::getId,paperIds);

            Page<Paper> paperPage = paperMapper.selectPage(page, lambdaQueryWrapper);
            List<Paper> paperList = paperPage.getRecords();
            List<PaperDto> paperDtos = BeanUtilCopy.copyListProperties(paperList, PaperDto::new);

            List<PaperDto> paperDtoList = wrapperPaper(paperDtos);
            Page<PaperDto> paperDtoPage = new Page<>();
            BeanUtilCopy.copyProperties(paperPage,paperDtoPage);
            paperDtoPage.setRecords(paperDtoList);
            return paperDtoPage;
        }else{
            LambdaQueryWrapper<Paper> lambdaQueryWrapper = new LambdaQueryWrapper<Paper>()
                    .eq(Paper::getAuditStatus, "1003");
            Page<Paper> paperPage = paperMapper.selectPage(page, lambdaQueryWrapper);
            List<Paper> paperList = paperPage.getRecords();
            List<PaperDto> paperDtos = BeanUtilCopy.copyListProperties(paperList, PaperDto::new);
            List<PaperDto> paperDtoList = wrapperPaper(paperDtos);

            Page<PaperDto> paperDtoPage = new Page<>();
            BeanUtilCopy.copyProperties(paperPage,paperDtoPage);
            paperDtoPage.setRecords(paperDtoList);
            return paperDtoPage;

        }
    }

    @Override
    public Page<PaperDto> queryPaper(Integer pageSize, Integer pageNum, String auditCode, Long userId) {
        Page<Paper> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Paper> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if(!ObjectUtils.isEmpty(userId)){
            List<UserPaper> userPaperList = userPaperService.list(new LambdaQueryWrapper<UserPaper>()
                    .eq(UserPaper::getUserId, userId));
            if(!CollectionUtils.isEmpty(userPaperList)){
                List<Long> ids = userPaperList.stream().map(UserPaper::getPaperId).collect(Collectors.toList());
                lambdaQueryWrapper.in(Paper::getId,ids);
            }
        }

        if(StringUtils.hasText(auditCode)){
            lambdaQueryWrapper.eq(Paper::getAuditStatus,auditCode);
        }

        Page<Paper> paperPage = paperMapper.selectPage(page, lambdaQueryWrapper);

        List<Paper> paperList = paperPage.getRecords();
        if(CollectionUtils.isEmpty(paperList)){
            return new Page<>(pageNum,pageSize);
        }

        List<PaperDto> paperDtos = BeanUtilCopy.copyListProperties(paperList, PaperDto::new);
        List<PaperDto> paperDtos1 = wrapperPaper(paperDtos);

        Page<PaperDto> paperDtoPage = new Page<>();
        BeanUtilCopy.copyProperties(paperPage,paperDtoPage);
        paperDtoPage.setRecords(paperDtos1);

        return paperDtoPage;
    }

}




