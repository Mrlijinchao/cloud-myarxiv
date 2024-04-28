package com.lijinchao.controller;

import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.Category;
import com.lijinchao.entity.Subject;
import com.lijinchao.service.CategoryService;
import com.lijinchao.utils.BaseApiResult;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    CategoryService categoryService;

    /**
     * 添加一个分类
     * @param category
     * @return
     */
    @PostMapping("")
    public BaseApiResult addCategory(@RequestBody Category category){
        try {
            categoryService.addCategory(category);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success();
    }

    @GetMapping("/querySubjectCategory")
    public BaseApiResult querySubjectCategory(){
        try {
            List<Subject> subjectCategory = categoryService.getSubjectCategory();
            return BaseApiResult.success(subjectCategory);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    @GetMapping("/queryCategoryValue")
    public BaseApiResult queryCategoryValue(Long categoryId){
        try {
            String categoryValue = categoryService.getCategoryValue(categoryId);
            if(ObjectUtils.isEmpty(categoryValue)){
                return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
            }
            return BaseApiResult.success(categoryValue);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }


}
