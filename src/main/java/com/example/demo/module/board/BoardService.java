package com.example.demo.module.board;

import com.example.demo.exception.statuscode.Exception500;
import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import com.example.demo.module.board.out_dto.BoardListDTO;
import com.example.demo.module.board.out_dto.BoardList_OutDTO;
import com.example.demo.module.board.out_dto.PageInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor @Slf4j
public class BoardService {

    private final BoardQueryRepository boardQueryRepository;

    @Transactional(readOnly = true)
    public BoardList_OutDTO findAll(BoardListSearch_InDTO boardListSearchInDTO, Pageable pageable) {
        log.debug("게시글 목록 - GET, Service");

        Page<BoardListDTO> boardListOutDTOS = null;
        try {
            boardListOutDTOS = boardQueryRepository.findAllWithUserForList(boardListSearchInDTO, pageable);
        } catch(Exception exception) {
            throw new Exception500("게시글 조회에 실패하였습니다.");
        }

        PageInfoDTO pageInfoDTO = new PageInfoDTO(boardListOutDTOS);

        return new BoardList_OutDTO(boardListOutDTOS, pageInfoDTO);
    }
}
