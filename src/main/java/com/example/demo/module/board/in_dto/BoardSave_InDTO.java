package com.example.demo.module.board.in_dto;

import com.example.demo.module.board.Board;
import com.example.demo.module.user.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BoardSave_InDTO {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 30, message = "제목을 30자 이내로 작성해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    public Board toEntity(User user) {
        return Board.builder()
                .user(user)
                .title(this.title)
                .content(this.content)
                .views(0)
                .createdAt(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
    }
}
