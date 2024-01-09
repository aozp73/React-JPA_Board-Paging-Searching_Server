package com.example.demo.config.security.principal;

import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> userEntity = userRepository.findByEmail(email);

        return userEntity.map(MyUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("올바르지 않은 정보입니다."));
    }
}