package com.example.demo.module.board.out_dto;

import com.example.demo.module.comment.out_dto.CommentListDTO;
import com.example.demo.module.comment.out_dto.CommentListFlatDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDetail_OutDTO {

    BoardDetailDTO boardDetailDTO;
    List<CommentListDTO> commentListDTOS = new ArrayList<>();

    public BoardDetail_OutDTO(BoardDetailFlatDTO boardDetailFlatDTO, List<CommentListFlatDTO> commentListFlatDTOS) {
        this.boardDetailDTO = new BoardDetailDTO(boardDetailFlatDTO);
        this.commentListDTOS = commentListFlatDTOS.stream()
                .map(CommentListDTO::new)
                .collect(Collectors.toList());
    }

    public BoardDetail_OutDTO(BoardDetailFlatDTO boardDetailFlatDTO) {
        this.boardDetailDTO = new BoardDetailDTO(boardDetailFlatDTO);
    }
}
