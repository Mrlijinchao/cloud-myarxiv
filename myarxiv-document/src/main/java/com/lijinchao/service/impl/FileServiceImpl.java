package com.lijinchao.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.File;
import com.lijinchao.entity.User;
import com.lijinchao.feign.feignclient.file.FileClient;
import com.lijinchao.service.FileService;
import com.lijinchao.service.IPFSService;
import com.lijinchao.utils.BaseApiResult;
import com.lijinchao.utils.ErrorApiResult;
import com.lijinchao.utils.SuccessApiResult;
import io.ipfs.multihash.Multihash;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    List<String> availableSuffixList = com.google.common.collect.Lists
            .newArrayList("pdf", "png","jpg", "docx","doc", "pptx", "xlsx", "html", "md", "txt");

    @Resource
    IPFSService ipfsService;

    @Resource
    FileClient fileClient;

    @Transactional
    @Override
    public BaseApiResult uploadFile(MultipartFile file,File fileInfo, User user) throws IOException {

        if (file == null || file.isEmpty() ||
        ObjectUtils.isEmpty(user)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
        }
        if(ObjectUtils.isEmpty(fileInfo)){
            fileInfo = new File();
        }
        String originFileName = file.getOriginalFilename();
        if (!StringUtils.hasText(originFileName)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,"没有文件名");
        }
        String suffix = null;
        //获取文件后缀名
        if (StringUtils.hasText(originFileName)) {
            suffix = originFileName.substring(originFileName.lastIndexOf(".") + 1);
            fileInfo.setSuffix(suffix);
        }
        if (!availableSuffixList.contains(suffix)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.FORMAT_ERROR);
        }
        String fileMd5 = SecureUtil.md5(file.getInputStream());
        // TODO 检查如果已经存在该文件，则实现秒传
        File fileByMD5 = fileClient.getFileByMD5(fileMd5);
        String cid;
        if(ObjectUtils.isEmpty(fileByMD5)){
            // 保存文件到IPFS
            InputStream inputStream = file.getInputStream();
            cid = ipfsService.saveFile(inputStream);
        }else{
            cid = fileByMD5.getCid();
            // 相同文件计数加一

        }

        // TODO 保存文件信息
        fileInfo.setCid(cid);
        fileInfo.setContentType(file.getContentType());
        fileInfo.setName(file.getOriginalFilename());
        fileInfo.setSize(file.getSize());
        fileInfo.setUserCode(user.getCode());
        fileInfo.setUserId(user.getId());
        fileInfo.setMd5(fileMd5);
        fileClient.saveFileInfo(fileInfo);

        LinkedHashMap hashMap = new LinkedHashMap<String,Object>();
        hashMap.put("fileName",originFileName);
        hashMap.put("cid",cid);
        hashMap.put("md5Hash",fileMd5);
        return BaseApiResult.success(hashMap);
    }

    @Override
    public BaseApiResult uploadFileBatch(MultipartFile[] files, Long submissionId,Long paperId, User user) {
        HashMap<String, Object> returnInfo = new HashMap<>();
        ArrayList<Object> successResData = new ArrayList<>();
        ArrayList<String> successInfo = new ArrayList<>();
        ArrayList<String> errorInfo = new ArrayList<>();
        for(MultipartFile file : files){
            File fileInfo = new File();
            fileInfo.setSubmissionId(submissionId);
            fileInfo.setPaperId(paperId);
            try {
                BaseApiResult baseApiResult = uploadFile(file, fileInfo, user);
                // 如果code等于200就记录正常数据，如果code不等于200就记录错误数据
                if(baseApiResult.code.equals(200)){
                    SuccessApiResult successApiResult = (SuccessApiResult)baseApiResult;
                    Object data = successApiResult.getData();
                    successResData.add(data);
                    successInfo.add(file.getOriginalFilename()+ "：处理成功！");
                }else{
                    ErrorApiResult errorApiResult = (ErrorApiResult)baseApiResult;
                    String message = errorApiResult.message;
                    errorInfo.add(file.getOriginalFilename() + "：" + message);
                }

            } catch (IOException e) {
                e.printStackTrace();
                // 如果发生异常保存失败
                errorInfo.add(file.getOriginalFilename() + "：此文件保存失败");
            }
        }
        returnInfo.put("successResData",successResData);
        returnInfo.put("success",successInfo);
        returnInfo.put("error",errorInfo);
        return BaseApiResult.success(returnInfo);
    }

    @Override
    public byte[] downloadFile(String cid) throws IOException {
        byte[] content = ipfsService.getFile(cid);
        return content;
    }

    @Override
    public void removeFile(Long id) throws IOException {
        if(ObjectUtils.isEmpty(id)){
            return;
        }
        // 删除MYSQL数据库里面的文件信息
        String cid = fileClient.removeFileInfo(id);
        if(StringUtils.hasText(cid)){
            // 解除IPFS数据库里面的文件pin
            ipfsService.removeFile(cid);
        }
    }
}
