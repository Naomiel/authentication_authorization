package com.prunny.authentication_authorization.security;


import com.accelerex.tasks_manager.model.auth.User;
import com.accelerex.tasks_manager.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        CustomUserDetails userDetails = null;
        if (user != null) {
            userDetails = new CustomUserDetails(user);
        }
        return userDetails;
    }
}
