package com.lijinchao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.Subject;
import com.lijinchao.service.SubjectService;
import com.lijinchao.utils.BaseApiResult;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/subject")
public class SubjectController {

    @Resource
    SubjectService subjectService;

    /**
     * 获取所有学科
     * @return
     */
    @GetMapping("")
    public BaseApiResult getSubject(){
        try {
            return BaseApiResult.success(subjectService.list());
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 添加一门学科
     * @param subject
     * @return
     */
    @PostMapping("")
    public BaseApiResult addSubject(@RequestBody Subject subject){
        try {
            if(ObjectUtils.isEmpty(subject)){
                return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
            }
            LambdaQueryWrapper<Subject> lambdaQueryWrapper = new LambdaQueryWrapper<Subject>()
                    .eq(Subject::getCode, subject.getCode())
                    .eq(Subject::getName, subject.getName());

            List<Subject> list = subjectService.list(lambdaQueryWrapper);

            if(!CollectionUtils.isEmpty(list)){
                return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"此学科已经存在");
            }

            subjectService.addSubject(subject);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success();
    }

    /**
     * 更新一个学科
     * @param subject
     * @return
     */
    @PutMapping("")
    public BaseApiResult updateSubject(@RequestBody Subject subject){
        try {
            subjectService.updateSubject(subject);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success();
    }

    /**
     * 根据Id移除一个学科
     * @param id
     * @return
     */
    @DeleteMapping("")
    public BaseApiResult removeSubject(@RequestParam Long id){
        try {
            subjectService.removeSubject(id);
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success();
    }

    /**
     * 根据学科id查询分类，学科下面的分类是一个分类树
     * 如果不传subjectId就查出所有学科及他们包含的分类
     * @param subjectId
     * @return
     */
    @GetMapping("/query")
    public BaseApiResult querySubjectCategory(@RequestParam(required = false) Long subjectId){
        try {
            return BaseApiResult.success(subjectService.querySubjectCategory(subjectId));
        }catch (Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

}
