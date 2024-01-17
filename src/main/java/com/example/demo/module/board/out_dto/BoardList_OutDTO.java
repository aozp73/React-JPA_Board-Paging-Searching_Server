package com.example.demo.module.board.out_dto;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardList_OutDTO {

    Page<BoardListDTO> boardList;
    PageInfoDTO pageInfo;
}
