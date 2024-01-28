package com.example.demo.module.board;

import com.example.demo.config.security.principal.MyUserDetails;
import com.example.demo.exception.statuscode.*;
import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import com.example.demo.module.board.in_dto.BoardSave_InDTO;
import com.example.demo.module.board.in_dto.BoardUpdate_InDTO;
import com.example.demo.module.board.out_dto.*;
import com.example.demo.module.comment.CommentRepository;
import com.example.demo.module.comment.out_dto.CommentListFlatDTO;
import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor @Slf4j
public class BoardService {

    private final UserRepository userRepository;
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
                .orElseThrow(() -> new Exception404("게시물이 존재하지 않습니다."));

        boardEntity.setViews(boardEntity.getViews() + 1);
    }

    @Transactional(readOnly = true)
    public BoardDetail_OutDTO findDetailById(Long boardId, MyUserDetails myUserDetails) {
        log.debug("게시글 상세 페이지 - GET, Service 2 = {}", myUserDetails);
        BoardDetailFlatDTO boardDetailDTO;
        List<CommentListFlatDTO> boardDetailCommentDTOS = new ArrayList<>();

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

        BoardDetail_OutDTO boardDetailOutDTO;
        if (myUserDetails == null) {
            boardDetailOutDTO = new BoardDetail_OutDTO(boardDetailDTO, boardDetailCommentDTOS);
        } else {
            boardDetailOutDTO = new BoardDetail_OutDTO(boardDetailDTO, boardDetailCommentDTOS, myUserDetails.getUser().getId());
        }

        return boardDetailOutDTO;
    }

    @Transactional
    public BoardDetail_OutDTO save(BoardSave_InDTO boardSaveInDTO, Long userId) {
        log.debug("게시글 등록 - POST, Service");

        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("회원 정보를 확인해주세요."));

        // 요청값 DB 저장
        Board board = boardSaveInDTO.toEntity(userEntity);
        try {
            boardRepository.save(board);
        } catch (Exception exception) {
            throw new Exception500("게시글 저장에 실패하였습니다.");
        }

        // 저장 데이터 반환
        return getBoardDetailOutDTO(board, userId);
    }


    @Transactional(readOnly = true)
    public BoardUpdate_OutDTO updateForm(Long boardId, Long userId) {

        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시물이 존재하지 않습니다."));

        if (!Objects.equals(boardEntity.getUser().getId(), userId)) {
            throw new Exception401("작성자만 수정할 수 있습니다.");
        }

        return new BoardUpdate_OutDTO().fromEntity(boardEntity);
    }

    @Transactional
    public void update(BoardUpdate_InDTO boardUpdateInDTO, Long userId) {
        log.debug("게시글 수정 - PUT, Service");

        Board boardEntity = boardRepository.findById(boardUpdateInDTO.getId())
                .orElseThrow(() -> new Exception404("게시물이 존재하지 않습니다."));

        userRepository.findById(userId)
                .orElseThrow(() -> new Exception400("회원 정보를 확인해주세요."));

        if (!Objects.equals(boardEntity.getUser().getId(), userId)) {
            throw new Exception401("작성자만 수정할 수 있습니다.");
        }

        // 요청값 DB 반영
        boardUpdateInDTO.toEntity(boardEntity);
    }

    @Transactional
    public void delete(Long boardId, Long userId) {
        log.debug("게시글 삭제 - DELETE, Service");

        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시물이 존재하지 않습니다."));

        if (!Objects.equals(boardEntity.getUser().getId(), userId)) {
            throw new Exception401("작성자만 삭제할 수 있습니다.");
        }

        try {
            boardRepository.deleteById(boardId);
        } catch (Exception exception) {
            throw new Exception500("게시글 삭제에 실패하였습니다.");
        }
    }

    /**
     * 호출: 게시글 등록, 수정
     * 기능: Client가 렌더링 할 DB 데이터 반환
     */
    private BoardDetail_OutDTO getBoardDetailOutDTO(Board boardEntity, Long userId) {
        BoardDetailFlatDTO boardDetailDTO = new BoardDetailFlatDTO();

        try {
            boardDetailDTO = boardRepository.findBoardDetailWithUserForDetail(boardEntity.getId());
        } catch(Exception exception) {
            throw new Exception500("게시글 상세 조회에 실패하였습니다.");
        }

        return new BoardDetail_OutDTO(boardDetailDTO, userId);
    }
}
