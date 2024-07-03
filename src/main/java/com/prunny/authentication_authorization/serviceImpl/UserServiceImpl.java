package com.prunny.authentication_authorization.serviceImpl;

import com.accelerex.tasks_manager.dto.*;
import com.accelerex.tasks_manager.exception.GenericAppException;
import com.accelerex.tasks_manager.exception.UserAlreadyExistsException;
import com.accelerex.tasks_manager.exception.UserNotFoundException;
import com.accelerex.tasks_manager.model.auth.User;
import com.accelerex.tasks_manager.model.auth.enums.Role;
import com.accelerex.tasks_manager.repository.auth.UserRepository;
import com.accelerex.tasks_manager.security.JWTUtil;
import com.accelerex.tasks_manager.service.EmailService;
import com.accelerex.tasks_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    public static final String USER_NOT_FOUND_ = "User not found ";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final EmailService emailService;
    @Value("${activation.url}")
    private String activation;

    @Override
    public List<GetUserResponse> getUsers() {
        log.info("Fetching all users...");
        var users = userRepository.findAll();
        List<GetUserResponse> responseList = new ArrayList<>();

        users.forEach(user -> {
            var response = new GetUserResponse(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());

            responseList.add(response);
        });
        log.info("Fetched {} users", responseList.size());
        return responseList;
    }

    @Override
    public GetUserResponse getUserById(Long userId) {
        log.info("Fetching user by ID: {}", userId);
        var user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found "));
        return new GetUserResponse(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());

    }


    @Override
    public String signUpUser(UserSignUpDto signUpDto) {
        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        newUser.setSecurityQuestion(signUpDto.getSecurityQuestion());
        newUser.setSecurityAnswer(signUpDto.getSecurityAnswer());
        userRepository.save(newUser);
        return String.format("Hi %s your account has been created successfully ", newUser.getFirstName());
    }

    @Override
    public SignUpResponse adminSignUp(UserDto dto) {
        var user = userRepository.findUserByEmail(dto.getEmail());

        if (user.isPresent())
            throw new UserAlreadyExistsException("Email already exist");

        User newUser = new User();
        newUser.setEmail(dto.getEmail());
        newUser.setFirstName(dto.getFirstName());
        newUser.setLastName(dto.getLastName());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setDisabled(true);
        newUser.setRole(Role.ADMIN);

        userRepository.save(newUser);
        return (new SignUpResponse(newUser.getFirstName(), newUser.getLastName(), newUser.getEmail()));
    }

    @Override
    public PrincipalDTO loginUser(UserLoginDto dto) {
        var user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found "));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            new PrincipalDTO("Not successful", false, StringUtils.EMPTY);

        return new PrincipalDTO("Successful", true, jwtUtil.generateToken(dto.getEmail()));

    }

    @Override
    public String updateUser(Long userId, UserDto userDto) {
        var user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found "));
        userRepository.save(user);
        return String.format("Hi %s your account has been updated successfully ", user.getFirstName());
    }

    @Override
    public void deleteUser(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_));
        userRepository.delete(user);
    }

    @Override
    public CreateStaffResponse createStaff(StaffDto staffDto) {
        log.info("Creating new staff member with email: {}", staffDto.getEmail());

        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isAdmin = false;
        UserDetails userDetails = (UserDetails) auth;
        var user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException(String.format("Email address %s does not exist", userDetails.getUsername())));

        if (!isAdmin && !user.getRole().equals(Role.ADMIN)) throw new RuntimeException("User is not authorized");

        var staffUser = userRepository.findUserByEmail(staffDto.getEmail());
        if (staffUser.isPresent())
            throw new UserAlreadyExistsException("Email already exists");

        User newUser = new User();
        newUser.setFirstName(staffDto.getFirstName());
        newUser.setLastName(staffDto.getLastName());
        newUser.setEmail(staffDto.getEmail());
        newUser.setRole(Role.STAFF);
        String otp = generateOTP();

        newUser.setOtp(otp);
        userRepository.save(newUser);


        sendActivationEmail(staffDto,otp);
        log.info("New staff member created with ID: {}", newUser.getId());
        return new CreateStaffResponse(newUser.getEmail(), newUser.getPassword());
    }

    public String generateOTP() {
        Random random = new Random();
        int otp = random.nextInt(1000000);
        return String.format("%06d", otp);
    }

    @Override
    public String activateAccount(ActivationDto activationDto, String email) {
        if (!activationDto.getPassword().equals(activationDto.getConfirmPassword()))
            throw new GenericAppException("Passwords do not match");
        log.info("EMAIL {}", email);
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("Email address %s does not exist", email)));

        if (!user.getOtp().equals(activationDto.getOtp()))
            throw new GenericAppException("Invalid OTP");

        user.setPassword(passwordEncoder.encode(activationDto.getPassword()));
        user.setDisabled(false);
        user.setAccountVerified(true);
        user.setOtp(null);
        user.setSecurityQuestion(activationDto.getSecurityQuestion());
        user.setSecurityAnswer(activationDto.getSecurityAnswer());
        userRepository.save(user);

        return "Account activated successfully!";


    }
    private void sendActivationEmail(StaffDto newUser, String otp) {
        String activationLink = activation + newUser.getEmail();

        var subject = "Welcome to Our Service!";
        var body = String.format(
                """
                        Dear %s %s,

                        Welcome to our service! We are excited to have you on board. Please activate your account using the link below:

                        %s

                        OTP: %s

                        Best regards,
                        The Team""",
                newUser.getFirstName(),
                newUser.getLastName(),
                activationLink,
                otp
        );

        emailService.sendEmail(newUser.getEmail(), subject, body);

    }
}





