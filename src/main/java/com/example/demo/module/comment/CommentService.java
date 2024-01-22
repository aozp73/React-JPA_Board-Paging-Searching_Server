package com.example.demo.module.comment;

import com.example.demo.exception.statuscode.*;
import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardRepository;
import com.example.demo.module.comment.in_dto.CommentSave_InDTO;
import com.example.demo.module.comment.in_dto.CommentUpdate_InDTO;
import com.example.demo.module.comment.out_dto.CommentListFlatDTO;
import com.example.demo.module.comment.out_dto.CommentList_OutDTO;
import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor @Slf4j
public class CommentService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;


    @Transactional(readOnly = true)
    public List<CommentList_OutDTO> findAll(Long boardId, Long userId) {

        List<CommentListFlatDTO> commentList;

        try {
            commentList = commentRepository.findAllWithCommentForDetail(boardId);
        } catch (Exception exception) {
            throw new Exception500("페이지를 새로고침 해주세요.");
        }

        return commentList.stream()
                .map(commentListFlatDTO -> new CommentList_OutDTO(commentListFlatDTO, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void save(CommentSave_InDTO commentSaveInDTO, Long userId) {
        log.debug("댓글 작성 - POST, Service");

        Board boardEntity = boardRepository.findById(commentSaveInDTO.getBoardId())
                .orElseThrow(() -> new Exception404("게시물이 존재하지 않습니다."));

        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("회원 정보를 확인해주세요."));

        try {
            commentRepository.save(commentSaveInDTO.toEntity(boardEntity, userEntity));
        } catch (Exception exception) {
            throw new Exception500("댓글 저장에 실패하였습니다.");
        }
    }

    @Transactional
    public void update(CommentUpdate_InDTO commentUpdateInDTO, Long userId) {
        log.debug("댓글 수정 - PUT, Service");

        boardRepository.findById(commentUpdateInDTO.getBoardId())
                .orElseThrow(() -> new Exception404("게시물이 존재하지 않습니다."));

        Comment commentEntity = commentRepository.findById(commentUpdateInDTO.getCommentId())
                .orElseThrow(() -> new Exception404("댓글이 존재하지 않습니다."));

        if (!Objects.equals(commentEntity.getUser().getId(), userId)) {
            throw new Exception401("댓글 작성자만 수정할 수 있습니다.");
        }

        commentEntity.setContent(commentUpdateInDTO.getContent());
    }

    @Transactional
    public void delete(Long boardId, Long commentId, Long userId) {
        log.debug("댓글 삭제 - DELETE, Service ");

        boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시물이 존재하지 않습니다."));

        Comment commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new Exception404("댓글이 존재하지 않습니다."));

        if (!Objects.equals(commentEntity.getUser().getId(), userId)) {
            throw new Exception401("댓글 작성자만 삭제할 수 있습니다.");
        }

        try {
            commentRepository.deleteById(commentId);
        } catch (Exception exception) {
            throw new Exception500("댓글 삭제에 실패하였습니다.");
        }
    }
}
