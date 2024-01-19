package com.example.demo.service.board;

import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardQueryRepository;
import com.example.demo.module.board.BoardRepository;
import com.example.demo.module.board.BoardService;
import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import com.example.demo.module.board.in_dto.BoardSave_InDTO;
import com.example.demo.module.board.out_dto.BoardDetailFlatDTO;
import com.example.demo.module.board.out_dto.BoardDetail_OutDTO;
import com.example.demo.module.board.out_dto.BoardListDTO;
import com.example.demo.module.board.out_dto.BoardList_OutDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class BoardSaveServiceTest {

    @InjectMocks // 해당 파일에 있는 @Mock Bean 주입
    private BoardService boardService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private BoardRepository boardRepository;

    @Test
    @DisplayName("게시글 등록 성공")
    public void save_Success() {
        // given
        BoardSave_InDTO boardSaveInDTO = make_BoardSave_InDTO();
        User userEntity = DummyEntityHelper.setUpUser(1L, "user1@naver.com", "user1", "abc1", UserRole.COMMON);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userEntity));
        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> {
            Board savedBoard = invocation.getArgument(0);
            savedBoard.setId(1L);
            return savedBoard;
        });

        when(boardRepository.findBoardDetailWithUserForDetail(any(Long.class))).thenReturn(make_BoardDetailFlatDTO());
        // when
        BoardDetail_OutDTO result = boardService.save(boardSaveInDTO, 1L);

        // then
        assertNotNull(result);
        assertEquals(1, result.getBoardDetailDTO().getBoardId());
        assertEquals("제목1", result.getBoardDetailDTO().getTitle());
        assertEquals("내용1", result.getBoardDetailDTO().getContent());
        assertEquals(0, result.getBoardDetailDTO().getViews());
        assertEquals(0, result.getBoardDetailDTO().getCommentCount());
        assertEquals(1, result.getBoardDetailDTO().getUser().getUserId());
        assertEquals("mockUser", result.getBoardDetailDTO().getUser().getUsername());

        verify(userRepository).findById(any(Long.class));
        verify(boardRepository).save(any(Board.class));
        verify(boardRepository).findBoardDetailWithUserForDetail(any(Long.class));
    }

    private BoardSave_InDTO make_BoardSave_InDTO() {

        return BoardSave_InDTO.builder()
                .title("제목1")
                .content("내용1")
                .build();
    }

    private BoardDetailFlatDTO make_BoardDetailFlatDTO() {

        return BoardDetailFlatDTO.builder()
                .boardId(1L)
                .title("제목1")
                .content("내용1")
                .views(0)
                .createdAt(LocalDateTime.now())
                .commentCount(0L)

                .userId(1L)
                .username("mockUser")
                .build();
    }
}
