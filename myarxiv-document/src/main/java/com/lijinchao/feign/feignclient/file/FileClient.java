package com.lijinchao.feign.feignclient.file;

import com.lijinchao.myarxivProjectFacade.file.FileFacade;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "myarxiv-project",contextId = "FileClient",path = "/openapi/fileFacade")
public interface FileClient extends FileFacade {
}
