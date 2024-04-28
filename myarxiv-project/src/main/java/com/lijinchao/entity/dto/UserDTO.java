package com.lijinchao.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.lijinchao.entity.User;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class UserDTO extends User {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date minDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date maxDate;

    private Integer pageNum;

    private Integer pageSize;

    private String category;

    private String subject;

}
