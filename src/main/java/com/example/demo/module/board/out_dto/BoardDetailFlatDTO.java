package com.example.demo.module.board.out_dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDetailFlatDTO {
    private Long boardId;
    private String title;
    private String content;
    private Integer views;
    private LocalDateTime createdAt;
    private Long commentCount;

    private Long userId;
    private String username;

    public BoardDetailFlatDTO(Long id, Long userId, String title, String content, int views, LocalDateTime createdAt,
                               String username, Long commentCount) {
        this.boardId = id;
        this.title = title;
        this.content = content;
        this.views = views;
        this.createdAt = createdAt;
        this.commentCount = commentCount;

        this.userId = userId;
        this.username = username;
    }
}
