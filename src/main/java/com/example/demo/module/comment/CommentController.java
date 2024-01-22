package com.example.demo.module.comment;

import com.example.demo.config.security.principal.MyUserDetails;
import com.example.demo.exception.ResponseDTO;
import com.example.demo.module.comment.in_dto.CommentSave_InDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor @Slf4j
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/auth/comment")
    public ResponseEntity<?> save(@RequestBody CommentSave_InDTO commentSaveInDTO,
                                  @AuthenticationPrincipal MyUserDetails myUserDetails) {
        log.debug("댓글 작성 - POST, Controller");

        commentService.save(commentSaveInDTO, myUserDetails.getUser().getId());

        // 전체 댓글 리 렌더링

        return null;
    }
}

