package com.lijinchao.service;

import com.lijinchao.entity.Attribute;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lijinchao.entity.AttributeValue;

import java.util.HashMap;
import java.util.List;

/**
* @author 时之始
* @description 针对表【attribute】的数据库操作Service
* @createDate 2024-01-01 16:00:19
*/
public interface AttributeService extends IService<Attribute> {

    HashMap<String,List<AttributeValue>> getAttributes(List<String> codes);
}
