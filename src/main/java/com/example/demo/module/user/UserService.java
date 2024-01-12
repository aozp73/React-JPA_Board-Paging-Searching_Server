package com.example.demo.module.user;

import com.example.demo.exception.statuscode.Exception400;
import com.example.demo.exception.statuscode.Exception500;
import com.example.demo.module.user.in_dto.Join_InDTO;
import com.example.demo.module.user.in_dto.Login_OutDTO;
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
        log.debug("회원가입 요청- POST, Service");

        if(userRepository.findByEmail(joinInDTO.getEmail()).isPresent()) {
            throw new Exception400("동일 이메일 존재");
        }

        try {
            userRepository.save(joinInDTO.toEntity(passwordEncoder));
        } catch (Exception exception) {
            throw new Exception500("회원가입 실패");
        }
    }

    @Transactional(readOnly = true)
    public Boolean emailCheck(String email) {
        log.debug("회원가입 입력 중 이메일 체크 - POST, Service");

        return userRepository.findByEmail(email).isEmpty();
    }

    @Transactional(readOnly = true)
    public Login_OutDTO login() {
        log.debug(("로그인 요청 - POST, Service"));

        return null;
    }
}
