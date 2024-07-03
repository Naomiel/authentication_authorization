package com.prunny.authentication_authorization.controller;

import com.accelerex.tasks_manager.dto.*;
import com.accelerex.tasks_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<GetUserResponse>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping()
    public ResponseEntity<String> signUpUser(@RequestBody UserSignUpDto signUpDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUpUser(signUpDto));
    }

    @PostMapping("/admin/signup")
    public ResponseEntity<?> adminSignUp(@RequestBody UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.adminSignUp(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<PrincipalDTO> loginUser(@RequestBody UserLoginDto loginDto) {
        PrincipalDTO authenticatedUser = userService.loginUser(loginDto);

        if (Objects.isNull(authenticatedUser))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(authenticatedUser);

    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestBody ActivationDto activationDto, @RequestParam String email) {
        return ResponseEntity.ok(userService.activateAccount(activationDto, email));
    }

}
