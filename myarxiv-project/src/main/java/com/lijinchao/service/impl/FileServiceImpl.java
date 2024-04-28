package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.entity.File;
import com.lijinchao.service.FileService;
import com.lijinchao.mapper.FileMapper;
import org.springframework.stereotype.Service;

/**
* @author 时之始
* @description 针对表【file】的数据库操作Service实现
* @createDate 2024-01-02 11:28:46
*/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File>
    implements FileService{

}




