package com.lijinchao.controller;

import com.lijinchao.constant.MessageConstant;
import com.lijinchao.service.AttributeService;
import com.lijinchao.utils.BaseApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/attr")
public class AttributeController {

    @Resource
    AttributeService attributeService;

    /**
     * 查询字典
     * @param codes
     * @return
     */
    @GetMapping("/query")
    public BaseApiResult queryAttributes(@RequestParam("code") List<String> codes){
        try {
            return BaseApiResult.success(attributeService.getAttributes(codes));
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

}
