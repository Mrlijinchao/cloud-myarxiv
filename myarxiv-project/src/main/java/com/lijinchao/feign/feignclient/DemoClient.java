package com.lijinchao.feign.feignclient;



import com.lijinchao.myarxivDocumentFacade.DemoFacade;
import org.springframework.cloud.openfeign.FeignClient;

//@FeignClient(name = "doc-management-system",path = "/openapi",url = "${system.url}")
@FeignClient(name = "myarxiv-document",contextId = "DemoClient",path = "/openapi")
public interface DemoClient extends DemoFacade {
}
