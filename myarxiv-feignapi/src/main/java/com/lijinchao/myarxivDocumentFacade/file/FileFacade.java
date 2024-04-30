package com.lijinchao.myarxivDocumentFacade.file;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

/**
 * 其他模块调用document模块的接口
 */
public interface FileFacade {

    /**
     * 删除文件
     * @param ids
     */
    @DeleteMapping("/removeFile")
    void removeFile(@RequestBody List<Long> ids) throws IOException;

}
