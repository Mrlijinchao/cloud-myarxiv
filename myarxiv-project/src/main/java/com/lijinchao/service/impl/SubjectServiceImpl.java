package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.entity.AttributeValue;
import com.lijinchao.entity.Category;
import com.lijinchao.entity.Subject;
import com.lijinchao.entity.SubjectCategory;
import com.lijinchao.service.CategoryService;
import com.lijinchao.service.SubjectCategoryService;
import com.lijinchao.service.SubjectService;
import com.lijinchao.mapper.SubjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 时之始
* @description 针对表【subject】的数据库操作Service实现
* @createDate 2024-01-03 08:28:56
*/
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject>
    implements SubjectService{

    @Resource
    SubjectCategoryService subjectCategoryService;

    @Resource
    CategoryService categoryService;

    @Override
    public void addSubject(Subject subject) {
        if(ObjectUtils.isEmpty(subject)){
            return;
        }
        this.save(subject);
    }

    @Override
    public void updateSubject(Subject subject) {
        if(ObjectUtils.isEmpty(subject)){
            return;
        }
        this.updateById(subject);
    }

    @Override
    public void removeSubject(Long id) {
        if(ObjectUtils.isEmpty(id)){
            return;
        }
        this.removeById(id);
    }

    @Override
    public List<Subject> querySubjectCategory(Long subjectId) {
        LambdaQueryWrapper<Subject> subjectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!ObjectUtils.isEmpty(subjectId)){
            subjectLambdaQueryWrapper.eq(Subject::getId,subjectId);
        }

        List<Subject> subjectList = this.list(subjectLambdaQueryWrapper);

        if(CollectionUtils.isEmpty(subjectList)){
            return new ArrayList<>();
        }
        Set<Long> subjectIdSet = subjectList.stream().map(Subject::getId).collect(Collectors.toSet());
        List<SubjectCategory> subjectCategoryList = subjectCategoryService
                .list(new LambdaQueryWrapper<SubjectCategory>()
                .in(SubjectCategory::getSubjectId, subjectIdSet));

        if(CollectionUtils.isEmpty(subjectCategoryList)){
            return new ArrayList<>();
        }

        List<Category> categoryList = categoryService.list();

//        Map<Long, SubjectCategory> subjectCategoryMap = subjectCategoryList.stream().collect(Collectors.toMap(SubjectCategory::getCategoryId,
//                subjectCategory -> subjectCategory));

        Map<Long, Subject> subjectMap = subjectList.stream().collect(Collectors.toMap(Subject::getId, subject -> subject));

        List<Category> categories = setTreeShape(categoryList);

        Map<Long, Category> categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, category -> category));

//        for(Subject subject : subjectList){
//            for()
//        }
        for(SubjectCategory subjectCategory : subjectCategoryList){
            Subject subject = subjectMap.get(subjectCategory.getSubjectId());
            Category category = categoryMap.get(subjectCategory.getCategoryId());
            if(!ObjectUtils.isEmpty(subject) && !ObjectUtils.isEmpty(category)){
                subject.getCategoryList().add(category);
            }
        }
        return subjectList;
    }

    /**
     * 用循环生成组织树
     * @param categoryList
     * @return
     */
    public List<Category> setTreeShape(List<Category> categoryList){
        if(CollectionUtils.isEmpty(categoryList)){
            return new ArrayList<>();
        }
        HashMap<Long, List<Category>> treeMap = new HashMap<>();

        // 把所有子级放到父级下面
        for(Category category : categoryList){
            Long parentId = category.getParentId();
            if(parentId.equals(0L)){
                continue;
            }
            List<Category> children = Optional.ofNullable(treeMap.get(parentId)).orElse(new ArrayList<>());
            children.add(category);
            treeMap.put(parentId,children);
        }
//        List<OrganizationDTO> delItems = new ArrayList<>();

        for(Category category : categoryList){
            List<Category> children = treeMap.get(category.getId());
            if(CollectionUtils.isEmpty(children)){
                continue;
            }
            category.setChildren(children);
        }

        ArrayList<Category> categories = new ArrayList<>();

        for(Category category : categoryList){
            if(category.getParentId().equals(0L)){
                categories.add(category);
            }
        }

        return categories;
    }


}




