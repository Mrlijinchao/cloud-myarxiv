package com.lijinchao.controller;

import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.File;
import com.lijinchao.entity.User;
import com.lijinchao.feign.feignclient.file.FileClient;
import com.lijinchao.service.FileService;
import com.lijinchao.uitls.FileUtils;
import com.lijinchao.uitls.RedisUtil;
import com.lijinchao.utils.BaseApiResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    FileService fileService;

    @Resource
    RedisUtil<User> redisUtil;

    @Resource
    FileClient fileClient;

    @PostMapping("")
    public BaseApiResult upload(@RequestParam("file") MultipartFile[] file,
                                @RequestParam(required = false) Long submissionId,
                                @RequestParam(required = false) Long paperId,
                                HttpServletRequest request){
        try {
            String token = request.getHeader("authorization");
            User user = redisUtil.getByToken(token);
            return fileService.uploadFileBatch(file,submissionId,paperId,user);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

//    /**
//     * 在线显示文件
//     *
//     * @param id 文件id
//     * @return
//     */
//    @GetMapping("/view2/{id}")
//    public ResponseEntity<Object> previewFileOnline(@PathVariable String id) throws UnsupportedEncodingException {
//        Optional<Document> file = fileService.getPreviewById(id);
//        if (file.isPresent()) {
//            return ResponseEntity.ok()
//                    // 这里需要进行中文编码
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + URLEncoder.encode(file.get().getName(), "utf-8") + ".pdf")
//                    .header(HttpHeaders.CONTENT_TYPE, FileContentTypeUtils.getContentType("pdf"))
//                    .header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "").header("Connection", "close")
//                    .header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "")
//                    .body(file.get().getContent());
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageConstant.FILE_NOT_FOUND);
//        }
//    }

    @GetMapping("")
    public BaseApiResult download(@RequestParam(name = "cid",required = false) String cid,
                                  @RequestParam(name = "fileId",required = false) Long fileId,
                                    HttpServletResponse response){
        try {
            if(!StringUtils.hasText(cid)
            && ObjectUtils.isEmpty(fileId)){
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
            }
            // 如果fileId存在就是直接根据fileId查询
            if(!ObjectUtils.isEmpty(fileId)){
                File fileInfo = fileClient.getFileInfo(fileId);
                if(ObjectUtils.isEmpty(fileInfo)){
                    return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
                }
                byte[] bytes = fileService.downloadFile(fileInfo.getCid());
                FileUtils.downloadUtil(response,fileInfo.getName(),fileInfo.getContentType(),bytes);
                return BaseApiResult.success();
            }
            // 如果fileId不存在根据cid查询
            if(StringUtils.hasText(cid)){
                File fileInfoByCid = fileClient.getFileInfoByCid(cid);
                String fileName = UUID.randomUUID().toString();
                String fileContentType = null;
                if(!ObjectUtils.isEmpty(fileInfoByCid)){
                    fileName = fileInfoByCid.getName();
                    fileContentType = fileInfoByCid.getContentType();
                }
                byte[] bytes = fileService.downloadFile(cid);
                FileUtils.downloadUtil(response,fileName,fileContentType,bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success();
    }

    @GetMapping("/new")
    public Object downloadFile(@RequestParam(name = "cid",required = false) String cid,
                                  @RequestParam(name = "fileId",required = false) Long fileId,
                                  HttpServletResponse response) throws IOException {
//        File fileInfoByCid = fileClient.getFileInfoByCid(cid);
//        String fileName = UUID.randomUUID().toString();
//        String fileContentType = null;
//        if(!ObjectUtils.isEmpty(fileInfoByCid)){
//            fileName = fileInfoByCid.getName();
//            fileContentType = fileInfoByCid.getContentType();
//        }
//        byte[] bytes = fileService.downloadFile(cid);
//
//        response.setCharacterEncoding("utf-8");
////        response.setHeader("Content-disposition", "attachment;filename="+ filename + ".pdf");
//        // 设置response的Header
//        response.addHeader("Content-Disposition", "attachment;filename=" + new String("filename".getBytes()));
//        response.addHeader("Content-Length", "" + bytes.length);
//        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
////        response.setContentType("application/octet-stream");
//        toClient.write(bytes);
//        toClient.flush();
//        toClient.close();
//
//
//        return response;

        File fileInfoByCid = fileClient.getFileInfoByCid(cid);
        String fileName = UUID.randomUUID().toString();
        String fileContentType = null;
        if (!ObjectUtils.isEmpty(fileInfoByCid)) {
            fileName = fileInfoByCid.getName();
            fileContentType = fileInfoByCid.getContentType();
        }
        byte[] bytes = fileService.downloadFile(cid);

        response.setContentType(fileContentType);
        response.setHeader("content-type", fileContentType);
        // 设置文件名  URLEncoder.encode(fei_name, "utf-8")
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        response.setHeader("Access-Control-Allow-Origin", "*");

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            ServletOutputStream os = response.getOutputStream();
            os.write(bytes, 0, bytes.length);

            System.out.println("success");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return "你好啊啊啊";
    }

    @GetMapping("/downloadById")
    public BaseApiResult downloadByFileId(@RequestParam("id") Long id,
                                          HttpServletResponse response){
        try {
            if(ObjectUtils.isEmpty(id)){
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
            }
            // TODO 从file_md5表里面查询文件后缀
            File fileInfo = fileClient.getFileInfo(id);
            if(ObjectUtils.isEmpty(fileInfo)){
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
            }
            byte[] bytes = fileService.downloadFile(fileInfo.getCid());
            FileUtils.downloadUtil(response,fileInfo.getName(),fileInfo.getContentType(),bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success();
    }

    @DeleteMapping("")
    public BaseApiResult remove(@RequestBody File file){
        try {
            fileService.removeFile(file.getId());
        }catch (Exception e){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success();
    }


}
