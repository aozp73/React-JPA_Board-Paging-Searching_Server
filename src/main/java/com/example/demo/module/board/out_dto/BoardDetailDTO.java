package com.example.demo.module.board.out_dto;

import lombok.*;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDetailDTO {

    private Long boardId;
    private String title;
    private String content;
    private Integer views;
    private String createdAt;
    private Long commentCount;

    private User user = new User();

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private Long userId;
        private String username;
    }

    public BoardDetailDTO(BoardDetailFlatDTO boardDetailFlatDTO) {
        this.boardId = boardDetailFlatDTO.getBoardId();
        this.title = boardDetailFlatDTO.getTitle();
        this.content = boardDetailFlatDTO.getContent();
        this.views = boardDetailFlatDTO.getViews();

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        this.createdAt = boardDetailFlatDTO.getCreatedAt().format(dateFormat);

        this.commentCount = boardDetailFlatDTO.getCommentCount();
        this.user.userId = boardDetailFlatDTO.getUserId();
        this.user.username = boardDetailFlatDTO.getUsername();
    }
}
