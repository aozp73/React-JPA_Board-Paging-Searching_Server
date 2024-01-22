package com.example.demo.module.comment;

import com.example.demo.config.security.principal.MyUserDetails;
import com.example.demo.exception.ResponseDTO;
import com.example.demo.module.comment.in_dto.CommentSave_InDTO;
import com.example.demo.module.comment.in_dto.CommentUpdate_InDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor @Slf4j
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/auth/comment")
    public ResponseEntity<?> save(@RequestBody @Valid  CommentSave_InDTO commentSaveInDTO,
                                  @AuthenticationPrincipal MyUserDetails myUserDetails) {
        log.debug("댓글 작성 - POST, Controller");
        commentService.save(commentSaveInDTO, myUserDetails.getUser().getId());

        return ResponseEntity.ok().body(new ResponseDTO<>().data(commentService.findAll(commentSaveInDTO.getBoardId(), myUserDetails.getUser().getId())));
    }

    @PutMapping("/auth/comment/{boardId}/{commentId}")
    public ResponseEntity<?> update(@RequestBody CommentUpdate_InDTO commentUpdateInDTO,
                                    @AuthenticationPrincipal MyUserDetails myUserDetails) {
        log.debug("댓글 수정 - PUT, Controller");
        commentService.update(commentUpdateInDTO, myUserDetails.getUser().getId());

        return ResponseEntity.ok().body(new ResponseDTO<>().data(commentService.findAll(commentUpdateInDTO.getBoardId(), myUserDetails.getUser().getId())));
    }
}

