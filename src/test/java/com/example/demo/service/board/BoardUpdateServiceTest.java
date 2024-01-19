package com.example.demo.service.board;

import com.example.demo.exception.statuscode.Exception401;
import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardRepository;
import com.example.demo.module.board.BoardService;
import com.example.demo.module.board.in_dto.BoardUpdate_InDTO;
import com.example.demo.module.board.out_dto.BoardDetailFlatDTO;
import com.example.demo.module.board.out_dto.BoardDetail_OutDTO;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardUpdateServiceTest {

    @InjectMocks // 해당 파일에 있는 @Mock Bean 주입
    private BoardService boardService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private BoardRepository boardRepository;

    @Test
    @DisplayName("게시글 수정 성공")
    public void update_SuccessTest() {
        // given
        BoardUpdate_InDTO boardUpdateInDTO = make_BoardUpdate_InDTO();
        User userEntity = DummyEntityHelper.setUpUser(1L, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        Board boardEntity = DummyEntityHelper.setUpBoard(1L, userEntity, "제목1", "내용1", 10);

        when(boardRepository.findById(any(Long.class))).thenReturn(Optional.of(boardEntity));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userEntity));
        when(boardRepository.findBoardDetailWithUserForDetail(any(Long.class))).thenReturn(make_BoardDetailFlatDTO());

        // when
        BoardDetail_OutDTO result = boardService.update(boardUpdateInDTO, 1L);

        // then
        assertNotNull(result);
        assertEquals(1, result.getBoardDetailDTO().getBoardId());
        assertEquals("수정 제목1", result.getBoardDetailDTO().getTitle());
        assertEquals("수정 내용1", result.getBoardDetailDTO().getContent());
        assertEquals(10, result.getBoardDetailDTO().getViews());
        assertEquals(0, result.getBoardDetailDTO().getCommentCount());
        assertEquals(1, result.getBoardDetailDTO().getUser().getUserId());
        assertEquals("user1", result.getBoardDetailDTO().getUser().getUsername());

        verify(userRepository).findById(any(Long.class));
        verify(boardRepository).findById(any(Long.class));
        verify(boardRepository).findBoardDetailWithUserForDetail(any(Long.class));
    }

    @Test
    @DisplayName("게시글 수정 실패 - 타 작성자의 게시글")
    public void update_NotMatchWriter_FailTest() {
        // given
        BoardUpdate_InDTO boardUpdateInDTO = BoardUpdate_InDTO.builder().id(1L).title("수정 제목1").content("수정 내용1").build();
        User userEntity = DummyEntityHelper.setUpUser(1L, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        Board boardEntity = DummyEntityHelper.setUpBoard(1L, userEntity, "제목1", "내용1", 10);

        when(boardRepository.findById(any(Long.class))).thenReturn(Optional.of(boardEntity));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userEntity));

        // when & then
        Exception401 thrown = assertThrows(Exception401.class, () -> boardService.update(boardUpdateInDTO, 2L));
        assertEquals("작성자만 수정할 수 있습니다.", thrown.getMessage());

        verify(boardRepository).findById(any(Long.class));
        verify(userRepository).findById(any(Long.class));
        verify(boardRepository, never()).findBoardDetailWithUserForDetail(any(Long.class));
    }

    private BoardUpdate_InDTO make_BoardUpdate_InDTO() {

        return BoardUpdate_InDTO.builder()
                .id(1L)
                .title("제목1")
                .content("내용1")
                .build();
    }

    private BoardDetailFlatDTO make_BoardDetailFlatDTO() {

        return BoardDetailFlatDTO.builder()
                .boardId(1L)
                .title("수정 제목1")
                .content("수정 내용1")
                .views(10)
                .createdAt(LocalDateTime.now())
                .commentCount(0L)

                .userId(1L)
                .username("user1")
                .build();
    }
}
