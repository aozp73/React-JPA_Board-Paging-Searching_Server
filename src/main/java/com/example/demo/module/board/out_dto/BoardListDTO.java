package com.example.demo.module.board.out_dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardListDTO {

    private Long boardId;
    private String title;
    private Integer views;
    private String createdAt;
    private Integer commentCount;

    private User user;

    // user
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private Long userId;
        private String username;
    }
}
