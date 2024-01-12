package com.example.demo.module.user;

import com.example.demo.config.envBeanConfig.jwt.MyJwtSource;
import com.example.demo.exception.ResponseDTO;
import com.example.demo.module.user.in_dto.Join_InDTO;
import com.example.demo.module.user.in_dto.Login_InDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor @Slf4j
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid Join_InDTO joinInDTO) {
        log.debug("회원가입 요청 - POST, Controller)");
        userService.save(joinInDTO);

        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    @GetMapping("/emailCheck")
    public ResponseEntity<?> emailCheck(@RequestParam String email) {
        log.debug("회원가입 입력 중 이메일 체크 - POST, Controller");
        boolean isValid = userService.emailCheck(email);

        return ResponseEntity.ok().body(new ResponseDTO<>().data(isValid));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid Login_InDTO loginInDTO) {
        log.debug(("로그인 요청 - POST, Controller"));

        return null;
    }
}
