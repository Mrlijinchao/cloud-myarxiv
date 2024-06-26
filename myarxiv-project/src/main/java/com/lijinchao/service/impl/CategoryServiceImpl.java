package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.Category;
import com.lijinchao.entity.Subject;
import com.lijinchao.entity.SubjectCategory;
import com.lijinchao.enums.GlobalEnum;
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
    public BaseApiResult addCategory(Category category) {
        if(ObjectUtils.isEmpty(category)){
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
        }
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<Category>()
                .eq(Category::getCode, category.getCode())
                .eq(Category::getName, category.getName());
        List<Category> list = this.list(lambdaQueryWrapper);
        if(!CollectionUtils.isEmpty(list)){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"此分类已经存在！");
        }
        // subject的id放在category的id里面传送到后端
        Long subjectId = category.getId();
        category.setId(null);
        category.setStatusCd(GlobalEnum.EFFECT.getCode());
        this.save(category);

        if(!ObjectUtils.isEmpty(subjectId)){
            SubjectCategory subjectCategory = new SubjectCategory();
            subjectCategory.setCategoryId(category.getId());
            subjectCategory.setSubjectId(subjectId);
            subjectCategory.setStatusCd(GlobalEnum.EFFECT.getCode());
            subjectCategoryService.save(subjectCategory);
        }

        return BaseApiResult.success(category);
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

    @Override
    public void deleteCategory(Long id) {
        if(ObjectUtils.isEmpty(id)){
            return;
        }
        this.removeById(id);
        LambdaQueryWrapper<SubjectCategory> lambdaQueryWrapper = new LambdaQueryWrapper<SubjectCategory>()
                .eq(SubjectCategory::getCategoryId, id);
        subjectCategoryService.remove(lambdaQueryWrapper);
    }

    @Override
    public BaseApiResult updateCategory(Category category) {
        if(ObjectUtils.isEmpty(category)){
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
        }
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<Category>()
                .eq(Category::getCode, category.getCode())
                .eq(Category::getName, category.getName());
        List<Category> list = this.list(lambdaQueryWrapper);
        if(!CollectionUtils.isEmpty(list) && list.size() > 1){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"此分类已经存在！");
        }

        this.updateById(category);
        return BaseApiResult.success();
    }
}




