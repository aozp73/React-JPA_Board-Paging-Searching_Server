package com.example.demo.module.board;

import com.example.demo.exception.statuscode.CustomException;
import com.example.demo.exception.statuscode.Exception400;
import com.example.demo.exception.statuscode.Exception500;
import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import com.example.demo.module.board.out_dto.*;
import com.example.demo.module.comment.CommentRepository;
import com.example.demo.module.comment.out_dto.CommentListFlatDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor @Slf4j
public class BoardService {

    private final BoardQueryRepository boardQueryRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

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

    @Transactional
    public void viewsCount(Long boardId) {
        log.debug("게시글 상세 페이지 - GET, Service 1");

        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception400("게시물이 존재하지 않습니다."));

        boardEntity.setViews(boardEntity.getViews() + 1);
    }

    @Transactional(readOnly = true)
    public BoardDetail_OutDTO findDetailById(Long boardId) {
        log.debug("게시글 상세 페이지 - GET, Service 2");
        BoardDetailFlatDTO boardDetailDTO = null;
        List<CommentListFlatDTO> boardDetailCommentDTOS = null;

        try {
            boardDetailDTO = boardRepository.findBoardDetailWithUserForDetail(boardId);
        } catch(Exception exception) {
            throw new Exception500("게시글 상세 조회에 실패하였습니다.");
        }
        try {
            boardDetailCommentDTOS = commentRepository.findAllWithCommentForDetail(boardId);
        } catch(Exception exception) {
            throw new Exception500("게시글 댓글 조회에 실패하였습니다.");
        }

        return new BoardDetail_OutDTO(boardDetailDTO, boardDetailCommentDTOS);
    }
}
