package com.lijinchao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName file_md5
 */
@TableName(value ="file_md5")
@Data
public class FileMd5 extends BaseEntity {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     * 文件的md5值
     */
    private String md5;

    /**
     * 文件的引用次数
     */
    private Integer count;

}
