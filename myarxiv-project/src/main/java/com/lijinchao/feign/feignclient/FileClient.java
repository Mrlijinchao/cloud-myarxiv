package com.lijinchao.feign.feignclient;

import com.lijinchao.myarxivDocumentFacade.file.FileFacade;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "myarxiv-document",contextId = "FileClient",path = "/openapi/fileFacade")
public interface FileClient extends FileFacade {
}
