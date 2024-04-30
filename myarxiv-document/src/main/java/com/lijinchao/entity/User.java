package com.lijinchao.entity;

import lombok.Data;

/**
 *
 * @TableName user
 */
@Data
public class User extends BaseEntity {
    /**
     * id
     */
//    @TableId
    private Long id;

    /**
     * 账号
     */
    private String code;

    /**
     * 名字
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 国家
     */
    private String country;

    /**
     * 组织
     */
    private String organization;

    /**
     * 职业状态
     */
    private String careerStatus;

    /**
     * 学科
     */
    private Long subjectId;

    /**
     * 所属一级分类
     */
    private Long categoryId;

    /**
     * 用户自己网站或者信息网页地址
     */
    private String homePage;

    /**
     * 身份证号
     */
    private String identityCode;

    /**
     * 性别
     */
    private String gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 学历
     */
    private String education;

    /**
     * 学校
     */
    private String school;

    /**
     * 备注
     */
    private String remark;

    /**
     * 描述
     */
    private String description;

//    @TableField(exist = false)
    private String isAdmin;

//    @TableField(exist = false)
    private Subject subjectObj;

//    @TableField(exist = false)
    private Category categoryObj;

}
