package com.lijinchao.enums;

public enum GlobalEnum {

    /**
     * 状态
     */
    EFFECT("1000", "生效"),
    INVALID("1100", "失效"),

    /**
     * 默认角色角色Id
     */
    SYS_ADMINISTRATOR("100002", "管理员"),
    ORDINARY_USER("1022", "普通用户"),

    /**
     *  角色类型
     */
    ROLE_TYPE_SYS("1001", "系统角色"),
    ROLE_TYPE_PROJECT("1002","普通角色"),


    /**
     * 文档归档审核
     */
    NOT_REVIEWED("1001","未归档未审核的文档"),
    REVIEWING("1002","归档审核中"),
    PASSED("1003","归档审核通过"),
    NOT_PASSED("1004","归档审核未通过"),

    /**
     * 分类类型
     */
    DOC_NUM("docNum","文档的档号门类"),
    GENERAL("general","普通分类"),

    DOC_META_TAG_TYPE("docMeta","文档元数据标签"),
    FILE_TAG_TYPE("file","文件标签")

    ;







    String code;

    String message;

    GlobalEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
