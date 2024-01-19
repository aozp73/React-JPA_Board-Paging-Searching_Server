package com.example.demo.service.board;

import com.example.demo.exception.statuscode.Exception401;
import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardRepository;
import com.example.demo.module.board.BoardService;
import com.example.demo.module.board.out_dto.BoardUpdate_OutDTO;
import com.example.demo.module.user.User;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoardUpdateFormServiceTest {

    @InjectMocks private BoardService boardService;

    @Mock private BoardRepository boardRepository;


    @Test
    @DisplayName("게시글 수정 페이지 데이터 - 응답 성공")
    public void updateForm_SuccessTest() {
        // given
        Long boardId = 1L, userId = 1L;
        User userEntity = DummyEntityHelper.setUpUser(1L, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        Board boardEntity = DummyEntityHelper.setUpBoard(1L, userEntity, "제목1", "내용1", 10);

        when(boardRepository.findById(any(Long.class))).thenReturn(Optional.of(boardEntity));

        // when
        BoardUpdate_OutDTO result = boardService.updateForm(boardId, userId);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals("제목1", result.getTitle());
        assertEquals("내용1", result.getContent());

        verify(boardRepository).findById(any(Long.class));
    }

    @Test
    @DisplayName("게시글 수정 페이지 데이터 - 응답 실패 (다른 작성자의 글)")
    public void updateForm_NotMatchWriter_FailTest() {
        // given
        Long boardId = 1L, userId = 2L;
        User userEntity = DummyEntityHelper.setUpUser(1L, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        Board boardEntity = DummyEntityHelper.setUpBoard(1L, userEntity, "제목1", "내용1", 10);

        when(boardRepository.findById(any(Long.class))).thenReturn(Optional.of(boardEntity));

        // when & then
        Exception401 thrown = assertThrows(Exception401.class, () -> boardService.updateForm(boardId, userId));
        assertEquals("작성자만 수정할 수 있습니다.", thrown.getMessage());

        verify(boardRepository).findById(any(Long.class));
    }
}
