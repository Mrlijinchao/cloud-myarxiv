package com.lijinchao.entity.dto;

import com.lijinchao.entity.User;
import lombok.Data;

@Data
public class RegisterDTO {
    private User user;
    private String verificationCode;
}
