package com.lijinchao.mapper;

import com.lijinchao.entity.Paper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author 时之始
* @description 针对表【paper】的数据库操作Mapper
* @createDate 2024-01-03 08:28:19
* @Entity com.lijinchao.entity.Paper
*/
public interface PaperMapper extends BaseMapper<Paper> {
    @Select("select count(id) from paper where YEAR(create_date) >= YEAR(NOW()) and is_deleted = 0")
    Long getCount();
}




