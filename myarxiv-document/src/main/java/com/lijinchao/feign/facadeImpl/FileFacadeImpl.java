package com.lijinchao.feign.facadeImpl;

import com.lijinchao.myarxivDocumentFacade.file.FileFacade;
import com.lijinchao.myarxivProjectFacade.auth.AuthFacade;
import com.lijinchao.service.FileService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/openapi/fileFacade")
public class FileFacadeImpl implements FileFacade {

    @Resource
    FileService fileService;

    @Override
    public void removeFile(List<Long> ids) throws IOException {
        for(Long id : ids){
            fileService.removeFile(id);
        }
    }
}
