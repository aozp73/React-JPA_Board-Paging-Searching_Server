package com.example.demo.module.comment.in_dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdate_InDTO {

    private Long boardId;
    private Long commentId;
    private String content;
}
