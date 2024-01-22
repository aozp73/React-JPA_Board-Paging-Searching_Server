package com.example.demo.service.comment;

import com.example.demo.exception.statuscode.Exception401;
import com.example.demo.exception.statuscode.Exception404;
import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardRepository;
import com.example.demo.module.comment.Comment;
import com.example.demo.module.comment.CommentRepository;
import com.example.demo.module.comment.CommentService;
import com.example.demo.module.comment.in_dto.CommentSave_InDTO;
import com.example.demo.module.comment.in_dto.CommentUpdate_InDTO;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentUpdateServiceTest {

    @InjectMocks // 해당 파일에 있는 @Mock Bean 주입
    private CommentService commentService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 수정 성공")
    public void update_SuccessTest() {
        // given
        Long userId = 1L;
        CommentUpdate_InDTO commentUpdateInDTO = make_CommentUpdate_InDTO(1L, 1L,"테스트 저장 댓글1");

        User userEntity = DummyEntityHelper.setUpUser(1L, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        Board boardEntity = DummyEntityHelper.setUpBoard(1L, userEntity, "제목1", "내용1", 10);
        Comment commentEntity = DummyEntityHelper.setUpComment(1L, userEntity, boardEntity, "수정 댓글 1");

        when(commentRepository.findById(eq(commentUpdateInDTO.getCommentId()))).thenReturn(Optional.of(commentEntity));

        // when
        commentService.update(commentUpdateInDTO, userId);

        // then
        verify(commentRepository).findById(any(Long.class));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 존재하지 않는 댓글")
    public void update_notExistComment_FailTest() {
        // given
        Long userId = 1L;
        CommentUpdate_InDTO commentUpdateInDTO = make_CommentUpdate_InDTO(2L, 1L,"테스트 저장 댓글1");

        when(commentRepository.findById(eq(commentUpdateInDTO.getCommentId()))).thenReturn(Optional.empty());

        // when & then
        Exception404 exception404 = assertThrows(Exception404.class, () ->
                commentService.update(commentUpdateInDTO, userId)
        );
        assertEquals(exception404.getMessage(), "댓글이 존재하지 않습니다.");

        verify(commentRepository).findById(any(Long.class));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 다른 작성자의 댓글")
    public void update_notMatchWriter_FailTest() {
        // given
        Long userId = 2L;
        CommentUpdate_InDTO commentUpdateInDTO = make_CommentUpdate_InDTO(1L, 1L,"테스트 저장 댓글1");

        User userEntity = DummyEntityHelper.setUpUser(1L, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        Board boardEntity = DummyEntityHelper.setUpBoard(1L, userEntity, "제목1", "내용1", 10);
        Comment commentEntity = DummyEntityHelper.setUpComment(1L, userEntity, boardEntity, "수정 댓글 1");

        when(commentRepository.findById(eq(commentUpdateInDTO.getCommentId()))).thenReturn(Optional.of(commentEntity));

        // when & then
        Exception401 exception401 = assertThrows(Exception401.class, () ->
                commentService.update(commentUpdateInDTO, userId)
        );
        assertEquals(exception401.getMessage(), "댓글 작성자만 수정할 수 있습니다.");

        verify(commentRepository).findById(any(Long.class));
    }


    private CommentUpdate_InDTO make_CommentUpdate_InDTO(Long boardId, Long commentId, String content) {

        return CommentUpdate_InDTO.builder()
                .boardId(boardId)
                .commentId(commentId)
                .content(content)
                .build();
    }
}
