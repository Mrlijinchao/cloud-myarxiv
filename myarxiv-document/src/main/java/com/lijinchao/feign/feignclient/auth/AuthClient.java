package com.lijinchao.feign.feignclient.auth;

import com.lijinchao.myarxivProjectFacade.auth.AuthFacade;
import org.springframework.cloud.openfeign.FeignClient;

//@FeignClient(name = "doc-management-system-project",path = "/openapi/facadeAuth",url = "${system-project.url}")
@FeignClient(name = "myarxiv-project",contextId = "AuthClient",path = "/openapi/authFacade")
public interface AuthClient extends AuthFacade {
}
