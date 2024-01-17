package com.example.demo.module.comment.out_dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentListFlatDTO {
    private Long id;
    private Long userId;
    private String username;
    private String content;
    private Boolean editable;
    private LocalDateTime createdAt;

    public CommentListFlatDTO(Long id, Long userId, String username, String content, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.createdAt = createdAt;
    }
}
