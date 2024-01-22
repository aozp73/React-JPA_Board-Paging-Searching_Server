package com.example.demo.module.comment;

import com.example.demo.exception.statuscode.CustomException;
import com.example.demo.exception.statuscode.Exception404;
import com.example.demo.exception.statuscode.Exception500;
import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardRepository;
import com.example.demo.module.comment.in_dto.CommentSave_InDTO;
import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor @Slf4j
public class CommentService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;


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
}
