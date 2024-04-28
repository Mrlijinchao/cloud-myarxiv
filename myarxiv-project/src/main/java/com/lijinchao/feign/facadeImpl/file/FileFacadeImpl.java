package com.lijinchao.feign.facadeImpl.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lijinchao.entity.File;
import com.lijinchao.entity.FileMd5;
import com.lijinchao.entity.PaperFile;
import com.lijinchao.entity.SubmissionFile;
import com.lijinchao.enums.GlobalEnum;
import com.lijinchao.mapper.FileMd5Mapper;
import com.lijinchao.myarxivProjectFacade.file.FileFacade;
import com.lijinchao.service.FileMd5Service;
import com.lijinchao.service.FileService;
import com.lijinchao.service.PaperFileService;
import com.lijinchao.service.PaperService;
import com.lijinchao.service.SubmissionFileService;
import com.lijinchao.service.SubmissionService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/openapi/fileFacade")
public class FileFacadeImpl implements FileFacade {

    @Resource
    FileService fileService;

    @Resource
    PaperFileService paperFileService;

    @Resource
    FileMd5Service fileMd5Service;

    @Resource
    FileMd5Mapper fileMd5Mapper;

    @Resource
    SubmissionFileService submissionFileService;

    @Resource
    PaperService paperService;

    @Resource
    SubmissionService submissionService;

    @Override
    public File getFileInfo(Long id) {
        if(ObjectUtils.isEmpty(id)){
            return null;
        }
        return fileService.getById(id);
    }

    @Override
    public void saveFileInfo(File file) {
        if(ObjectUtils.isEmpty(file)){
            return;
        }
        if(ObjectUtils.isEmpty(file.getPaperId()) && ObjectUtils.isEmpty(file.getSubmissionId())){
            return;
        }

//        // 如果此文件已经存过就不需要重新操作了
//        List<File> list = fileService.list(new LambdaQueryWrapper<File>().eq(File::getUserId, file.getUserId())
//                .eq(File::getMd5, file.getMd5()).eq(File::getCid, file.getCid()));
//
//        if(!CollectionUtils.isEmpty(list)){
//            return;
//        }

        // 如果此论文已经存在此文件就不再进行保存
        if(!ObjectUtils.isEmpty(file.getPaperId())){
            List<File> existFile = paperService.getExistFile(file.getPaperId());
            for(File file1 : existFile){
                if(file1.getCid().equals(file.getCid()) && file1.getMd5().equals(file.getMd5())){
                    return;
                }
            }
        }

        // 如果此提交已经存在此文件就不再进行保存
        if(!ObjectUtils.isEmpty(file.getSubmissionId())){
            List<File> existFile = submissionService.getExistFile(file.getSubmissionId());
            for(File file1 : existFile){
                if(file1.getCid().equals(file.getCid()) && file1.getMd5().equals(file.getMd5())){
                    return;
                }
            }
        }

        file.setStatusCd(GlobalEnum.EFFECT.getCode());
        fileService.save(file);
//        // 保存文件和论文的关系（用于版本更新）
        // statusCd 1000为正常情况，2000为更新版本的时候上传的文件，属于下一个版本的文件
        if(!ObjectUtils.isEmpty(file.getPaperId())){
            PaperFile paperFile = new PaperFile();
            paperFile.setFileId(file.getId());
            paperFile.setPaperId(file.getPaperId());
            paperFile.setStatusCd("2000");
            paperFileService.save(paperFile);
        }

        if(!ObjectUtils.isEmpty(file.getSubmissionId())){
            SubmissionFile submissionFile = new SubmissionFile();
            submissionFile.setFileId(file.getId());
            submissionFile.setSubmissionId(file.getSubmissionId());
            submissionFile.setStatusCd(GlobalEnum.EFFECT.getCode());
            submissionFileService.save(submissionFile);
        }

        Long count = fileMd5Mapper.getCount(file.getMd5());
        // 如果count等于null，说明还没有保存过这个文件，插入一条fileMd5数据，否则加一
        if(count == null){
            FileMd5 fileMd5 = new FileMd5();
            fileMd5.setCount(1);
            fileMd5.setMd5(file.getMd5());
            fileMd5.setStatusCd(GlobalEnum.EFFECT.getCode());
            fileMd5Service.save(fileMd5);
        }else{
            // file_md5表对应的count加一
            count++;
            fileMd5Mapper.update(null,new LambdaUpdateWrapper<FileMd5>()
                    .set(FileMd5::getCount,count)
                    .eq(FileMd5::getMd5,file.getMd5()));
        }

    }

    @Override
    public void updateFileInfo(File file) {
        fileService.updateById(file);
    }

    @Override
    public File getFileByMD5(String md5) {
//        List<FileMd5> list = fileMd5Service.list(new LambdaQueryWrapper<FileMd5>()
//                .eq(FileMd5::getMd5, md5));
//        if(CollectionUtils.isEmpty(list)){
//            return false;
//        }
//        return true;
        List<File> list = fileService.list(new LambdaQueryWrapper<File>()
                .eq(File::getMd5, md5));
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

    @Override
    public String removeFileInfo(Long id) {
        File file = fileService.getById(id);
        if(ObjectUtils.isEmpty(file)){
            return null;
        }
        fileService.removeById(id);
        // 移除文件和论文的关系
        paperFileService.remove(new LambdaQueryWrapper<PaperFile>()
                .eq(PaperFile::getFileId,id));
        // 移除和提交的对应关系
        submissionFileService.remove(new LambdaQueryWrapper<SubmissionFile>()
                .eq(SubmissionFile::getFileId,id));
        // file_md5表对应的count减一
        Long count = fileMd5Mapper.getCount(file.getMd5());
        if(count == null){
            return file.getCid();
        }
        // 如果count小于等于1，说明这次删除后这个文件就会真的删除，没有任何人拥有这个文件了
        if(count <= 1){
            fileMd5Service.remove(new LambdaQueryWrapper<FileMd5>()
                    .eq(FileMd5::getMd5,file.getMd5()));
            return file.getCid();
        }else {
            fileMd5Service.update(new LambdaUpdateWrapper<FileMd5>()
                    .set(FileMd5::getCount,(count - 1))
                    .eq(FileMd5::getMd5,file.getMd5()));
            return null;
        }
    }

    @Override
    public File getFileInfoByCid(String cid) {
        if(!StringUtils.hasText(cid)){
            return null;
        }
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper.eq(File::getCid,cid);
        List<File> files = fileService.list(fileLambdaQueryWrapper);
        if(CollectionUtils.isEmpty(files)){
            return null;
        }
        return files.get(0);
    }

}
