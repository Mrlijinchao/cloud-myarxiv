package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.Category;
import com.lijinchao.entity.Subject;
import com.lijinchao.entity.SubjectCategory;
import com.lijinchao.service.CategoryService;
import com.lijinchao.mapper.CategoryMapper;
import com.lijinchao.service.SubjectCategoryService;
import com.lijinchao.service.SubjectService;
import com.lijinchao.utils.BaseApiResult;
import com.lijinchao.utils.BeanUtilCopy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author 时之始
* @description 针对表【category】的数据库操作Service实现
* @createDate 2024-01-01 16:00:33
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    @Resource
    SubjectService subjectService;

    @Resource
    SubjectCategoryService subjectCategoryService;


    @Override
    public void addCategory(Category category) {
        if(ObjectUtils.isEmpty(category)){
            return ;
        }

        this.save(category);
    }

    @Override
    public List<Subject> getSubjectCategory() {
        List<Subject> subjectList = subjectService.list();
        if(CollectionUtils.isEmpty(subjectList)){
            return new ArrayList<>();
        }

        List<Long> subjectIds = subjectList.stream().map(Subject::getId).collect(Collectors.toList());

        Map<Long, Subject> subjectMap = subjectList.stream().collect(Collectors
                .toMap(Subject::getId, Function.identity()));

        List<SubjectCategory> subjectCategoryList = subjectCategoryService.list(new LambdaQueryWrapper<SubjectCategory>()
                .in(SubjectCategory::getSubjectId, subjectIds));

        if(CollectionUtils.isEmpty(subjectCategoryList)){
            return subjectList;
        }
        List<Long> categoryIds = subjectCategoryList.stream().map(SubjectCategory::getCategoryId)
                .collect(Collectors.toList());

        List<Category> categoryList = this.listByIds(categoryIds);

        if(CollectionUtils.isEmpty(categoryList)){
            return subjectList;
        }

        HashMap<Long, List<Category>> treeMap = new HashMap<>();

        // 把所有子级放到父级下面
        for(Category category: categoryList){
            Long parentId = category.getParentId();
            if(parentId.equals(0L)){
                continue;
            }
            List<Category> children = Optional.ofNullable(treeMap.get(parentId)).orElse(new ArrayList<>());
            children.add(category);
            treeMap.put(parentId,children);
        }

        for(SubjectCategory subjectCategory : subjectCategoryList){
            Subject subject = subjectMap.get(subjectCategory.getSubjectId());
            List<Category> categories = treeMap.get(subjectCategory.getCategoryId());
            if(ObjectUtils.isEmpty(subject)){
                continue;
            }
            subject.getCategoryList().addAll(categories);
        }

        return subjectList;
    }

    @Override
    public String getCategoryValue(Long categoryId) {
        if (ObjectUtils.isEmpty(categoryId)) {
            return null;
        }
        Category category = this.getById(categoryId);
        Long parentId = category.getParentId();
        if (!parentId.equals(0L)) {
            List<SubjectCategory> subjectCategoryList = subjectCategoryService.list(new LambdaQueryWrapper<SubjectCategory>()
                    .eq(SubjectCategory::getCategoryId, parentId));
            if (!CollectionUtils.isEmpty(subjectCategoryList)) {
                SubjectCategory subjectCategory = subjectCategoryList.get(0);
                String categoryValue = subjectCategory.getSubjectId().toString() + "," + parentId.toString() + "," + categoryId.toString();
                return categoryValue;
            }
        } else {
            List<SubjectCategory> subjectCategoryList = subjectCategoryService.list(new LambdaQueryWrapper<SubjectCategory>()
                    .eq(SubjectCategory::getCategoryId, categoryId));
            if (!CollectionUtils.isEmpty(subjectCategoryList)) {
                SubjectCategory subjectCategory = subjectCategoryList.get(0);
                String categoryValue = subjectCategory.getSubjectId().toString() + "," + categoryId.toString();
                return categoryValue;
            }
        }
        return null;
    }
}




