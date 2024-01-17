package com.example.demo.module.board;

import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import com.example.demo.module.board.out_dto.BoardListDTO;
import com.example.demo.module.board.out_dto.BoardList_OutDTO;
import com.example.demo.module.board.out_dto.PageInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;

    @Transactional(readOnly = true)
    public BoardList_OutDTO findAll(BoardListSearch_InDTO boardListSearchInDTO, Pageable pageable) {
        Page<BoardListDTO> boardListOutDTOS = boardQueryRepository.findAllWithUserForList(boardListSearchInDTO, pageable);
        PageInfoDTO pageInfoDTO = new PageInfoDTO(boardListOutDTOS);

        return new BoardList_OutDTO(boardListOutDTOS, pageInfoDTO);
    }
}
