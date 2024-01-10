package com.example.demo.module.user;

import com.example.demo.exception.ResponseDTO;
import com.example.demo.module.user.in_dto.Join_InDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor @Slf4j
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<?> join(@RequestBody() @Valid Join_InDTO joinInDTO) {
        log.debug("회원가입 - POST, Controller)");
        userService.save(joinInDTO);

        return ResponseEntity.ok().body(new ResponseDTO<>());
    }
}
