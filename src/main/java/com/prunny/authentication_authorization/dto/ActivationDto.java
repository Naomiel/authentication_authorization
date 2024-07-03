package com.prunny.authentication_authorization.dto;

import com.accelerex.tasks_manager.model.auth.enums.SecurityQuestion;
import lombok.Data;

@Data
public class ActivationDto {
    public SecurityQuestion securityQuestion;
    public String securityAnswer;
    private String password;
    private String confirmPassword;
    private String otp;
}
