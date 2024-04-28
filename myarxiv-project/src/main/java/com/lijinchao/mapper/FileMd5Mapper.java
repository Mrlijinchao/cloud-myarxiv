package com.lijinchao.mapper;

import com.lijinchao.entity.FileMd5;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author 时之始
* @description 针对表【file_md5】的数据库操作Mapper
* @createDate 2024-01-02 21:49:26
* @Entity com.lijinchao.entity.FileMd5
*/
public interface FileMd5Mapper extends BaseMapper<FileMd5> {
    @Select("select count from file_md5 where (md5 = #{md5}) and is_deleted = 0")
    Long getCount(String md5);
}




