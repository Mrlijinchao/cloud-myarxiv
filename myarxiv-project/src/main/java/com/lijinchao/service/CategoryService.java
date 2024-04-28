package com.lijinchao.service;

import com.lijinchao.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lijinchao.entity.Subject;

import java.util.List;

/**
* @author 时之始
* @description 针对表【category】的数据库操作Service
* @createDate 2024-01-01 16:00:33
*/
public interface CategoryService extends IService<Category> {

    /**
     * 添加分类
     * @param category
     * @return
     */
    void addCategory(Category category);

    /**
     * 查询学科分类（分层）
     * @return
     */
    List<Subject> getSubjectCategory();

    /**
     * 获取categoryValue  例如： 1，1，3
     * @param categoryId
     * @return
     */
    String getCategoryValue(Long categoryId);

}
