package com.example.demo.module.comment.in_dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdate_InDTO {

    private Long boardId;
    private Long commentId;

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;
}
