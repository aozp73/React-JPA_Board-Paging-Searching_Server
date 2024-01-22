package com.example.demo.service.comment;

import com.example.demo.exception.statuscode.Exception401;
import com.example.demo.exception.statuscode.Exception404;
import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardRepository;
import com.example.demo.module.comment.Comment;
import com.example.demo.module.comment.CommentRepository;
import com.example.demo.module.comment.CommentService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentDeleteServiceTest {

    @InjectMocks // 해당 파일에 있는 @Mock Bean 주입
    private CommentService commentService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 삭제 성공")
    public void delete_SuccessTest() {
        // given
        Long boardId = 1L;
        Long commentId = 1L;
        Long userId = 1L;

        User userEntity = DummyEntityHelper.setUpUser(1L, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        Board boardEntity = DummyEntityHelper.setUpBoard(1L, userEntity, "제목1", "내용1", 10);
        Comment commentEntity = DummyEntityHelper.setUpComment(1L, userEntity, boardEntity, "댓글1");

        when(boardRepository.findById(eq(boardId))).thenReturn(Optional.of(boardEntity));
        when(commentRepository.findById(eq(commentId))).thenReturn(Optional.of(commentEntity));

        // when
        commentService.delete(boardId, commentId, userId);

        // then
        verify(boardRepository).findById(any(Long.class));
        verify(commentRepository).findById(any(Long.class));
        verify(commentRepository).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 존재하지 않는 게시글")
    public void delete_notExistBoard_FailTest() {
        // given
        Long boardId = 1L;
        Long commentId = 1L;
        Long userId = 1L;

        when(boardRepository.findById(eq(boardId))).thenReturn(Optional.empty());

        // when & then
        Exception404 exception404 = assertThrows(Exception404.class, () ->
                commentService.delete(boardId, commentId, userId)
        );
        assertEquals(exception404.getMessage(), "게시물이 존재하지 않습니다.");

        // then
        verify(boardRepository).findById(any(Long.class));
        verify(commentRepository, never()).findById(any(Long.class));
        verify(commentRepository, never()).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 존재하지 않는 댓글")
    public void delete_notExistComment_FailTest() {
        // given
        Long boardId = 1L;
        Long commentId = 1L;
        Long userId = 1L;

        User userEntity = DummyEntityHelper.setUpUser(1L, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        Board boardEntity = DummyEntityHelper.setUpBoard(1L, userEntity, "제목1", "내용1", 10);

        when(boardRepository.findById(eq(boardId))).thenReturn(Optional.of(boardEntity));
        when(commentRepository.findById(eq(commentId))).thenReturn(Optional.empty());

        // when & then
        Exception404 exception404 = assertThrows(Exception404.class, () ->
                commentService.delete(boardId, commentId, userId)
        );
        assertEquals(exception404.getMessage(), "댓글이 존재하지 않습니다.");

        // then
        verify(boardRepository).findById(any(Long.class));
        verify(commentRepository).findById(any(Long.class));
        verify(commentRepository, never()).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 다른 작성자의 댓글")
    public void delete_notMatchWriter_FailTest() {
        // given
        Long boardId = 1L;
        Long commentId = 1L;
        Long userId = 1L;

        User userEntity = DummyEntityHelper.setUpUser(2L, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        Board boardEntity = DummyEntityHelper.setUpBoard(1L, userEntity, "제목1", "내용1", 10);
        Comment commentEntity = DummyEntityHelper.setUpComment(1L, userEntity, boardEntity, "댓글1");

        when(boardRepository.findById(eq(boardId))).thenReturn(Optional.of(boardEntity));
        when(commentRepository.findById(eq(commentId))).thenReturn(Optional.of(commentEntity));

        // when & then
        Exception401 exception401 = assertThrows(Exception401.class, () ->
                commentService.delete(boardId, commentId, userId)
        );
        assertEquals(exception401.getMessage(), "댓글 작성자만 삭제할 수 있습니다.");

        // then
        verify(boardRepository).findById(any(Long.class));
        verify(commentRepository).findById(any(Long.class));
        verify(commentRepository, never()).deleteById(any(Long.class));
    }

}
