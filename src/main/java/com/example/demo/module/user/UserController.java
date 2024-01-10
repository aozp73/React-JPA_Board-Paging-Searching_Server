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
    public ResponseEntity<?> join(@RequestBody() @Valid Join_InDTO joinInDTO, BindingResult bindingResult) {
        log.debug("회원가입 - POST, Controller)");

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });

            return ResponseEntity.badRequest().body(
                    new ResponseDTO<>().fail(HttpStatus.BAD_REQUEST, "입력 값 확인", errors)
            );
        }

        userService.save(joinInDTO);
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }
}
