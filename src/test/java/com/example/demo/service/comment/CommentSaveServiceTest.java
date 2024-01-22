package com.example.demo.service.comment;

import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardRepository;
import com.example.demo.module.comment.Comment;
import com.example.demo.module.comment.CommentRepository;
import com.example.demo.module.comment.CommentService;
import com.example.demo.module.comment.in_dto.CommentSave_InDTO;
import com.example.demo.module.comment.out_dto.CommentListFlatDTO;
import com.example.demo.module.comment.out_dto.CommentList_OutDTO;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentSaveServiceTest {

    @InjectMocks // 해당 파일에 있는 @Mock Bean 주입
    private CommentService commentService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 작성 성공")
    public void save_SuccessTest() {
        // given
        Long userId = 1L;
        CommentSave_InDTO commentSaveInDTO = make_CommentSave_InDTO(1L, "테스트 저장 댓글1");
        User userEntity = DummyEntityHelper.setUpUser(1L, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        Board boardEntity = DummyEntityHelper.setUpBoard(1L, userEntity, "제목1", "내용1", 10);

        when(boardRepository.findById(eq(commentSaveInDTO.getBoardId()))).thenReturn(Optional.of(boardEntity));
        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(userEntity));

        // when
        commentService.save(commentSaveInDTO, 1L);

        // then
        verify(userRepository).findById(any(Long.class));
        verify(boardRepository).findById(any(Long.class));
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 조회 성공")
    public void findAll_SuccessTest() {
        // given
        Long boardId = 1L;
        Long userId = 1L;
        List<CommentListFlatDTO> commentListFlatDTOS = make_CommentListFLatDTOS();

        when(commentRepository.findAllWithCommentForDetail(eq(boardId))).thenReturn(commentListFlatDTOS);

        // when
        List<CommentList_OutDTO> result = commentService.findAll(boardId, userId);

        // then
        verify(commentRepository).findAllWithCommentForDetail(any(Long.class));
        assertNotNull(result);
        assertEquals(commentListFlatDTOS.size(), result.size());

        for (int i = 0; i < commentListFlatDTOS.size(); i++) {
            CommentListFlatDTO flatDTO = commentListFlatDTOS.get(i);
            CommentList_OutDTO outDTO = result.get(i);

            assertEquals(flatDTO.getId(), outDTO.getCommentId());
            assertEquals(flatDTO.getContent(), outDTO.getContent());
            assertEquals(flatDTO.getUserId().equals(userId), outDTO.getEditable());
            assertEquals(flatDTO.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")), outDTO.getCreatedAt());
            assertEquals(flatDTO.getUserId(), outDTO.getUser().getUserId());
            assertEquals(flatDTO.getUsername(), outDTO.getUser().getUsername());
        }
    }

    private CommentSave_InDTO make_CommentSave_InDTO(Long boardId, String content) {

        return CommentSave_InDTO.builder()
                .boardId(boardId)
                .content(content)
                .build();
    }

    private List<CommentListFlatDTO> make_CommentListFLatDTOS() {
        List<CommentListFlatDTO> commentListFlatDTOS = new ArrayList<>();
        commentListFlatDTOS.add(make_CommentListFlatDTO(1L, 1L, "user1", "댓글 1"));
        commentListFlatDTOS.add(make_CommentListFlatDTO(1L, 2L, "user2", "댓글 2"));

        return commentListFlatDTOS;
    }

    private CommentListFlatDTO make_CommentListFlatDTO(Long boardId, Long userId, String username, String content) {
        return CommentListFlatDTO.builder()
                .id(boardId)
                .userId(userId)
                .username(username)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
