package com.example.demo.service.board;

import com.example.demo.exception.statuscode.Exception401;
import com.example.demo.exception.statuscode.Exception404;
import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardRepository;
import com.example.demo.module.board.BoardService;
import com.example.demo.module.comment.CommentRepository;
import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
import com.example.demo.module.user.enums.UserRole;
import com.example.demo.util.DummyEntityHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardDeleteServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock private BoardRepository boardRepository;

    @Test
    @DisplayName("게시글 삭제 성공")
    public void delete_SuccessTest() {
        // given
        Long boardId = 1L;
        Long userId = 1L;

        User userEntity = DummyEntityHelper.setUpUser(1L, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        Board boardEntity = DummyEntityHelper.setUpBoard(1L, userEntity, "제목1", "내용1", 10);

        when(boardRepository.findById(any(Long.class))).thenReturn(Optional.of(boardEntity));

        // when
        boardService.delete(boardId, userId);

        // then
        verify(boardRepository).findById(any(Long.class));
        verify(boardRepository).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 존재하지 않는 게시글")
    public void delete_notExistBoard_FailTest() {
        // given
        Long boardId = 2L;
        Long userId = 1L;

        when(boardRepository.findById(any(Long.class))).thenReturn(Optional.empty());


        // when & then
        Exception404 exception404 = assertThrows(Exception404.class, () ->
                boardService.delete(boardId, userId)
        );
        assertEquals(exception404.getMessage(), "게시물이 존재하지 않습니다.");

        verify(boardRepository).findById(any(Long.class));
        verify(boardRepository, never()).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 다른 작성자의 게시글")
    public void delete_notMatchWriter_FailTest() {
        // given
        Long boardId = 1L;
        Long userId = 2L;

        User userEntity = DummyEntityHelper.setUpUser(1L, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        Board boardEntity = DummyEntityHelper.setUpBoard(1L, userEntity, "제목1", "내용1", 10);

        when(boardRepository.findById(any(Long.class))).thenReturn(Optional.of(boardEntity));

        // when & then
        Exception401 exception401 = assertThrows(Exception401.class, () ->
                boardService.delete(boardId, userId)
        );
        assertEquals(exception401.getMessage(), "작성자만 삭제할 수 있습니다.");

        verify(boardRepository).findById(any(Long.class));
        verify(boardRepository, never()).deleteById(any(Long.class));
    }
}
