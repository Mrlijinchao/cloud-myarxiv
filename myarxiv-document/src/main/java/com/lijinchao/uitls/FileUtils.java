package com.lijinchao.uitls;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FileUtils {

    /**
     * 下载文件工具
     *
     * @param response response对象
//     * @param realPath 文件路径
     * @param fileName 文件名称
     */
    public static void downloadUtil(final HttpServletResponse response,String fileName,String contentType,byte[] content) throws IOException {

        if (content.length !=0 ) {
            response.setHeader("content-type", "application/octet-stream");
            if(StringUtils.hasText(contentType)){
                response.setContentType(contentType);
            }else {
                response.setContentType("application/octet-stream");
            }
            // 下载文件能正常显示中文
            try {
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {

                OutputStream os = response.getOutputStream();
                os.write(content,0,content.length);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
    }

}
