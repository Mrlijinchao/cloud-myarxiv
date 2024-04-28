package com.lijinchao.service;

import com.lijinchao.entity.Endorsement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lijinchao.entity.User;

/**
* @author 时之始
* @description 针对表【endorsement】的数据库操作Service
* @createDate 2024-03-31 17:12:22
*/
public interface EndorsementService extends IService<Endorsement> {
    /**
     * 检查是否需要背书
     * @param user
     * @return
     */
    Boolean checkEndorsement(User user,String subjectCategoryId);

    void endorsement(Long userId,Long endorserId,String userEmail,String userOrganization,String subjectCategoryId);

}
