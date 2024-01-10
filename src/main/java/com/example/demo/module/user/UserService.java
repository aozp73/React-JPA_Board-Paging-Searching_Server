package com.example.demo.module.user;

import com.example.demo.exception.statuscode.CustomException;
import com.example.demo.exception.statuscode.Exception400;
import com.example.demo.exception.statuscode.Exception500;
import com.example.demo.module.user.in_dto.Join_InDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor @Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void save(Join_InDTO joinInDTO) {
        log.debug("회원가입 - POST, Service");

        if(userRepository.findByEmail(joinInDTO.getEmail()).isPresent()) {
            throw new Exception400("동일 이메일 존재");
        }

        try {
            userRepository.save(joinInDTO.toEntity(passwordEncoder));
        } catch (Exception exception) {
            throw new Exception500("회원가입 실패");
        }
    }
}
