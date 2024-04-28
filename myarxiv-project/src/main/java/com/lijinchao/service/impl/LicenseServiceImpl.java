package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.entity.License;
import com.lijinchao.service.LicenseService;
import com.lijinchao.mapper.LicenseMapper;
import com.lijinchao.utils.BaseApiResult;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
* @author 时之始
* @description 针对表【license】的数据库操作Service实现
* @createDate 2024-01-03 08:28:06
*/
@Service
public class LicenseServiceImpl extends ServiceImpl<LicenseMapper, License>
    implements LicenseService{

    @Override
    public BaseApiResult getLicense(Long id) {
        if(ObjectUtils.isEmpty(id)){
            List<License> list = this.list();
            return BaseApiResult.success(list);
        }
        License license = this.getById(id);
        return BaseApiResult.success(license);
    }
}




