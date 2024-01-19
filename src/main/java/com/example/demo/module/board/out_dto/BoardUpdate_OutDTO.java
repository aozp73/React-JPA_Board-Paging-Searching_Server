package com.example.demo.module.board.out_dto;

import com.example.demo.module.board.Board;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardUpdate_OutDTO {

    private Long id;
    private Long userId;
    private String title;
    private String content;

    public BoardUpdate_OutDTO fromEntity(Board boardEntity) {
        return BoardUpdate_OutDTO.builder()
                .id(boardEntity.getId())
                .userId(boardEntity.getUser().getId())
                .title(boardEntity.getTitle())
                .content(boardEntity.getContent())
                .build();
    }
}

