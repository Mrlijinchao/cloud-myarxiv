package com.lijinchao.service;

import com.lijinchao.entity.Subject;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 时之始
* @description 针对表【subject】的数据库操作Service
* @createDate 2024-01-03 08:28:56
*/
public interface SubjectService extends IService<Subject> {

    /**
     * 添加一个学科
     * @param subject
     */
    void addSubject(Subject subject);

    void updateSubject(Subject subject);

    void removeSubject(Long id);

    /**
     * 根据subjectId查询分类树
     * @param subjectId
     * @return
     */
    List<Subject> querySubjectCategory(Long subjectId);

}
