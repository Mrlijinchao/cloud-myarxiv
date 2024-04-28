package com.lijinchao.feign.facadeImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lijinchao.myarxivDocumentFacade.DemoFacade;

@RestController
@RequestMapping("/openapi")
public class DemoFacadeImpl implements DemoFacade {

    @Value("${spring.application.name}")
    String name;
    @Override
    @GetMapping("/name")
    public String getName() {
        return name;
    }
}
