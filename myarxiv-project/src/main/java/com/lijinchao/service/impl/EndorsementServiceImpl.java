package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.entity.AttributeValue;
import com.lijinchao.entity.Endorsement;
import com.lijinchao.entity.Guarantee;
import com.lijinchao.entity.Subject;
import com.lijinchao.entity.User;
import com.lijinchao.service.AttributeService;
import com.lijinchao.service.EndorsementService;
import com.lijinchao.mapper.EndorsementMapper;
import com.lijinchao.service.SubjectService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 时之始
* @description 针对表【endorsement】的数据库操作Service实现
* @createDate 2024-03-31 17:12:22
*/
@Service
public class EndorsementServiceImpl extends ServiceImpl<EndorsementMapper, Endorsement>
    implements EndorsementService{

    @Resource
    AttributeService attributeService;

    @Resource
    SubjectService subjectService;

    @Override
    public Boolean checkEndorsement(User user,String subjectCategoryId) {
        if(ObjectUtils.isEmpty(user)){
            return false;
        }
        ArrayList<String> list = new ArrayList<>();
        list.add("FREE_GUARANTEE_EMAIL");
        list.add("FREE_GUARANTEE_ORG");
        HashMap<String, List<AttributeValue>> attributesMap = attributeService.getAttributes(list);
        List<AttributeValue> guaranteeEmail = attributesMap.get("FREE_GUARANTEE_EMAIL");
        List<AttributeValue> guaranteeOrg = attributesMap.get("FREE_GUARANTEE_ORG");
        Boolean flag = false;
        // 判断免担保邮箱后缀里面是否有此用户的
        if(!CollectionUtils.isEmpty(guaranteeEmail)){
            List<String> collect = guaranteeEmail.stream().map(AttributeValue::getValue).collect(Collectors.toList());
            String email = user.getEmail();
            int index = email.indexOf('@');
            String suffix = email.substring(index + 1);
            boolean isContains = collect.contains(suffix);
            if(isContains){
                flag = true;
                return true;
            }
        }
        // 判断免担保机构里面是否包含用户所属机构
        if(!CollectionUtils.isEmpty(guaranteeOrg)){
            List<String> collect = guaranteeOrg.stream().map(AttributeValue::getValue).collect(Collectors.toList());
            boolean isContains = collect.contains(user.getOrganization());
            if(isContains){
                flag = true;
                return true;
            }
        }

        Subject subject = subjectService.getById(user.getSubjectId());
        if(ObjectUtils.isEmpty(subject)){
            return flag;
        }
        // 判断此用户的此学科是否已经被担保过
        List<Endorsement> list1 = this.list(new LambdaQueryWrapper<Endorsement>().eq(Endorsement::getUserId, user.getId())
                .eq(Endorsement::getSubjectCategoryId, subjectCategoryId));
        if(!CollectionUtils.isEmpty(list1)){
            flag = true;
        }
        return flag;
    }

    @Override
    public void endorsement(Long userId, Long endorserId, String userEmail, String userOrganization, String subjectCategoryId) {
        Endorsement endorsement = new Endorsement();
        endorsement.setUserId(userId);
        endorsement.setEndorserId(endorserId);
        endorsement.setUserEmail(userEmail);
        endorsement.setUserOrganization(userOrganization);
        endorsement.setSubjectCategoryId(subjectCategoryId);
        this.save(endorsement);
    }

}




