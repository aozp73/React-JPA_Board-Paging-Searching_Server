package com.example.demo.module.comment.out_dto;

import lombok.*;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentList_OutDTO {

    private Long commentId;
    private String content;
    private Boolean editable;
    private String createdAt;

    private User user = new User();

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private Long userId;
        private String username;
    }

    public CommentList_OutDTO(CommentListFlatDTO commentListFlatDTO) {
       this.commentId = commentListFlatDTO.getId();
       this.content = commentListFlatDTO.getContent();

       DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
       this.createdAt = commentListFlatDTO.getCreatedAt().format(dateFormat);

       this.editable = false;

       this.user.userId = commentListFlatDTO.getUserId();
       this.user.username = commentListFlatDTO.getUsername();
    }

    public CommentList_OutDTO(CommentListFlatDTO commentListFlatDTO, Long userId) {
       this.commentId = commentListFlatDTO.getId();
       this.content = commentListFlatDTO.getContent();

       DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
       this.createdAt = commentListFlatDTO.getCreatedAt().format(dateFormat);

       this.editable = commentListFlatDTO.getUserId().equals(userId);

       this.user.userId = commentListFlatDTO.getUserId();
       this.user.username = commentListFlatDTO.getUsername();
    }
}
