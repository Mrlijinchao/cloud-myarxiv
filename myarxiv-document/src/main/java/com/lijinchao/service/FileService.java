package com.lijinchao.service;

import com.lijinchao.entity.File;
import com.lijinchao.entity.User;
import com.lijinchao.utils.BaseApiResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    /**
     * 上传文件
     * @param file
     * @param user
     * @return
     * @throws IOException
     */
    BaseApiResult uploadFile(MultipartFile file,File fileInfo, User user) throws IOException;

    /**
     * 批量上传文件
     * @param files
     * @param submissionId
     * @param user
     * @return
     */
    BaseApiResult uploadFileBatch(MultipartFile[] files,Long submissionId,Long paperId,User user);

    /**
     * 下载文件
     * @param cid
     * @return
     * @throws IOException
     */
    byte[] downloadFile(String cid) throws IOException;

    /**
     * 删除文件
     * @param id
     * @return
     */
    void removeFile(Long id) throws IOException;

}
