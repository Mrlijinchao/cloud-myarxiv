package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.Endorsement;
import com.lijinchao.entity.Paper;
import com.lijinchao.entity.User;
import com.lijinchao.entity.UserPaper;
import com.lijinchao.service.AuthService;
import com.lijinchao.service.EndorsementService;
import com.lijinchao.service.PaperService;
import com.lijinchao.service.UserPaperService;
import com.lijinchao.service.UserService;
import com.lijinchao.utils.AesEncryptUtil;
import com.lijinchao.utils.BaseApiResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    UserService userService;

    @Resource
    EndorsementService endorsementService;

    @Resource
    UserPaperService userPaperService;

    @Resource
    PaperService paperService;

    @Transactional
    @Override
    public BaseApiResult endorsement(String code, String password,String endorsementCode, String cipherText) throws Exception {
        if(!StringUtils.hasText(code) || !StringUtils.hasText(password)
                || !StringUtils.hasText(cipherText) || !StringUtils.hasText(endorsementCode)){
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
        }

        // 解码密文
        // 明文形式为: userId:1,1,3:MXJSD9
        // 以冒号隔离 1,1,3 == subjectId:categoryId:categoryId
        String str = AesEncryptUtil.desEncrypt(cipherText);

        String[] split = str.split(":");

        Long userId = Long.parseLong(split[0]);
        String categoryValue = split[1];
        String endorsementCode1 = split[2];

        // 检查用户（被背书者）是否已经背过书了，如果已经背过书就无需往下操作
        User user = userService.getById(userId);
        if(endorsementService.checkEndorsement(user,categoryValue)){
            return BaseApiResult.success("此用户已经具备资格，无需再背书");
        }

        // 检查背书码是否与加密的一样
        if(!endorsementCode.equals(endorsementCode1)){
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,"背书码错误");
        }

        // 检查用户（背书者）账号是否存在
        List<User> list = userService.list(new LambdaQueryWrapper<User>().eq(User::getCode, code)
                .eq(User::getPassword, password));

        if(CollectionUtils.isEmpty(list)){
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,"账号或者密码错误！");
        }

        User user1 = list.get(0);
        // 检查此用户（背书者）是否具备背书资格
        if(user.getId().equals(user1.getId())){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"您不可以给自己背书！");
        }
        Long categoryId = Long.parseLong(categoryValue.substring(categoryValue.length() - 1));
        List<UserPaper> list1 = userPaperService.list(new LambdaQueryWrapper<UserPaper>().eq(UserPaper::getUserId, user1.getId()));
        if(CollectionUtils.isEmpty(list1)){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"您不具备背书资格！");
        }
        List<Long> paperIds = list1.stream().map(UserPaper::getPaperId).collect(Collectors.toList());
        List<Paper> papers = paperService.listByIds(paperIds);
        if(CollectionUtils.isEmpty(papers)){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"您不具备背书资格！");
        }
        List<Long> categoryIds = papers.stream().map(Paper::getCategoryId).collect(Collectors.toList());

        int count = 0;
        for(Long id : categoryIds){
            if(id.equals(categoryId)){
                count++;
            }
            // 需要相关领域发布过至少三遍论文才具备背书资格
            if(count >= 3){
                // 增加一条背书记录
                endorsementService.endorsement(userId,user1.getId(),user.getEmail(),user.getOrganization(),categoryValue);
                return BaseApiResult.success("背书成功！");
            }
        }
        if(count >= 3){
            // 增加一条背书记录
            endorsementService.endorsement(userId,user1.getId(),user.getEmail(),user.getOrganization(),categoryValue);
            return BaseApiResult.success("背书成功！");
        }else{
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"您不具备背书资格！");
        }
    }

}
