package com.lijinchao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 * @TableName file
 */
@TableName(value ="file")
@Data
public class File extends BaseEntity {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private Long size;

    /**
     *
     */
    private String md5;

    /**
     *
     */
    private String hash;

    /**
     *
     */
    private String suffix;

    /**
     * ipfs返回的文件id
     */
    private String cid;

    /**
     * 有时候文件可能存在其他地方
     */
    private String url;

    /**
     *
     */
    private String contentType;

    /**
     *
     */
    private Integer transactionId;

    /**
     *
     */
    private String traderAddress;

    /**
     *
     */
    private String docState;

    /**
     *
     */
    private Long userId;

    /**
     *
     */
    private String userCode;

    /**
     *
     */
    private String errorMsg;

    /**
     *
     */
    private String description;

    /**
     *
     */
    private String abstracts;

    /**
     *
     */
    private Long previewFileId;

    /**
     *
     */
    private Long textFileId;

    /**
     *
     */
    private Long thumbId;

    /**
     * 论文id
     */
    @TableField(exist = false)
    private Long paperId;

    /**
     * 提交id
     */
    @TableField(exist = false)
    private Long submissionId;

}
