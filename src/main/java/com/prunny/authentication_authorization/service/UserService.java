package com.prunny.authentication_authorization.service;


import com.accelerex.tasks_manager.dto.*;

import java.util.List;

public interface UserService {
    List<GetUserResponse> getUsers();

    GetUserResponse getUserById(Long userId);

    String signUpUser(UserSignUpDto signUpDto);

    SignUpResponse adminSignUp(UserDto dto);

    PrincipalDTO loginUser(UserLoginDto dto);

    String updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);

    CreateStaffResponse createStaff(StaffDto staffCreationDto);

    String activateAccount(ActivationDto activationDto, String email);
}
