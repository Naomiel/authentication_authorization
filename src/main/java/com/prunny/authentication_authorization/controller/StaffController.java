package com.prunny.authentication_authorization.controller;

import com.accelerex.tasks_manager.dto.CreateStaffResponse;
import com.accelerex.tasks_manager.dto.StaffDto;
import com.accelerex.tasks_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/staff")
public class StaffController {
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<CreateStaffResponse> createStaff(@RequestBody StaffDto staffCreationDto) {
        var createdStaff = userService.createStaff(staffCreationDto);
        return new ResponseEntity<>(createdStaff, HttpStatus.CREATED);
    }
}
