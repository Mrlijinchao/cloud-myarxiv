package com.lijinchao.service;

import com.lijinchao.entity.License;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lijinchao.utils.BaseApiResult;

/**
* @author 时之始
* @description 针对表【license】的数据库操作Service
* @createDate 2024-01-03 08:28:06
*/
public interface LicenseService extends IService<License> {
    /**
     * 获取license
     * @param id
     * @return
     */
    BaseApiResult getLicense(Long id);
}
