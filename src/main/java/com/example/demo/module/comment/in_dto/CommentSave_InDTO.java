package com.example.demo.module.comment.in_dto;

import com.example.demo.module.board.Board;
import com.example.demo.module.comment.Comment;
import com.example.demo.module.user.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CommentSave_InDTO {

    private Long boardId;

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;

    public Comment toEntity(Board boardEntity, User userEntity) {
        return Comment.builder()
                .user(userEntity)
                .board(boardEntity)
                .content(this.content)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

