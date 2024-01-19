package com.example.demo.module.board.in_dto;

import com.example.demo.module.board.Board;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardUpdate_InDTO {

    private Long id;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 30, message = "제목을 30자 이내로 작성해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    public Board toEntity(Board boardEntity) {
        boardEntity.setTitle(this.title);
        boardEntity.setContent(this.content);

        return boardEntity;
    }
}

