package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.entity.Attribute;
import com.lijinchao.entity.AttributeValue;
import com.lijinchao.service.AttributeService;
import com.lijinchao.mapper.AttributeMapper;
import com.lijinchao.service.AttributeValueService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 时之始
* @description 针对表【attribute】的数据库操作Service实现
* @createDate 2024-01-01 16:00:19
*/
@Service
public class AttributeServiceImpl extends ServiceImpl<AttributeMapper, Attribute>
    implements AttributeService{

    @Resource
    AttributeValueService attributeValueService;

    @Override
    public HashMap<String, List<AttributeValue>> getAttributes(List<String> codes) {
        if(CollectionUtils.isEmpty(codes)){
            return new HashMap<>();
        }

        List<Attribute> attributeList = this.list(new LambdaQueryWrapper<Attribute>()
                .in(Attribute::getCode, codes));
        if(CollectionUtils.isEmpty(attributeList)){
            return new HashMap<>();
        }

        Set<Long> attrIdSet = attributeList.stream().map(Attribute::getId).collect(Collectors.toSet());

        List<AttributeValue> attributeValueList = attributeValueService.list(new LambdaQueryWrapper<AttributeValue>()
                .in(AttributeValue::getAttrId, attrIdSet));

        HashMap<String, List<AttributeValue>> hashMap = new HashMap<>();

        // 把code对应的所有attributeValue放到一个数组里面
        for(Attribute attribute : attributeList){
            for(AttributeValue attributeValue : attributeValueList){
                if(attributeValue.getAttrId().equals(attribute.getId())){
                    List<AttributeValue> attributeValueList1 = Optional.ofNullable(hashMap.get(attribute.getCode()))
                            .orElse(new ArrayList<AttributeValue>());
                    attributeValueList1.add(attributeValue);
                    hashMap.put(attribute.getCode(),attributeValueList1);
                }
            }
        }

        for(Attribute attribute : attributeList){
            List<AttributeValue> attributeValueList1 = hashMap.get(attribute.getCode());
            List<AttributeValue> attributeValueList2 = setTreeShape(attributeValueList1);
            hashMap.put(attribute.getCode(),attributeValueList2);
        }

        return hashMap;
    }


    /**
     * 用循环生成组织树
     * @param attributeValueList
     * @return
     */
    public List<AttributeValue> setTreeShape(List<AttributeValue> attributeValueList){
        if(CollectionUtils.isEmpty(attributeValueList)){
            return new ArrayList<>();
        }
        HashMap<Long, List<AttributeValue>> treeMap = new HashMap<>();

        // 把所有子级放到父级下面
        for(AttributeValue attributeValue : attributeValueList){
            Long parentId = attributeValue.getParentId();
            if(parentId.equals(0L)){
                continue;
            }
            List<AttributeValue> children = Optional.ofNullable(treeMap.get(parentId)).orElse(new ArrayList<>());
            children.add(attributeValue);
            treeMap.put(parentId,children);
        }
//        List<OrganizationDTO> delItems = new ArrayList<>();

        for(AttributeValue attributeValue : attributeValueList){
            List<AttributeValue> children = treeMap.get(attributeValue.getId());
            if(CollectionUtils.isEmpty(children)){
                continue;
            }
            attributeValue.setChildren(children);
        }

        ArrayList<AttributeValue> attributeValues = new ArrayList<>();

        for(AttributeValue attributeValue : attributeValueList){
            if(attributeValue.getParentId().equals(0L)){
                attributeValues.add(attributeValue);
            }
        }

        return attributeValues;
    }


}




