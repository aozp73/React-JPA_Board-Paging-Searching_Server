package com.example.demo.exception.statuscode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private HttpStatus status;

    public CustomException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }

    public CustomException(String msg) {
        this(msg, HttpStatus.BAD_REQUEST);
    }
}
