package com.prunny.authentication_authorization.dto;

import lombok.Data;

@Data
public class UserLoginDto {
    private String email;
    private String password;
}
