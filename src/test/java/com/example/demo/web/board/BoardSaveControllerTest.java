package com.example.demo.web.board;

import com.example.demo.config.security.MyAuthenticationManagerConfig;
import com.example.demo.config.security.MySecurityConfig;
import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.module.board.BoardController;
import com.example.demo.module.board.BoardService;
import com.example.demo.module.board.in_dto.BoardSave_InDTO;
import com.example.demo.module.board.out_dto.BoardDetailFlatDTO;
import com.example.demo.module.board.out_dto.BoardDetail_OutDTO;
import com.example.demo.util.TestSecurityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({MySecurityConfig.class, MyJwtProvider.class, MyAuthenticationManagerConfig.class}) // 추가 하지 않을 경우, 기본 Security 설정 사용
@WebMvcTest(BoardController.class)
public class BoardSaveControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BoardService boardService;

    @BeforeEach
    public void setUp() {
        TestSecurityHelper.setAuthentication();
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("게시글 등록 성공")
    public void save_SuccessTest() throws Exception {
        // given
        Long userId = 1L;
        BoardSave_InDTO boardSaveInDTO = make_BoardSave_InDTO();
        String content = new ObjectMapper().writeValueAsString(boardSaveInDTO);

        when(boardService.save(any(BoardSave_InDTO.class), eq(userId))).thenReturn(make_BoardDetail_OutDTO());

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))

                .andExpect(jsonPath("$.data.boardDetailDTO.boardId").value(1))
                .andExpect(jsonPath("$.data.boardDetailDTO.title").value("제목1"))
                .andExpect(jsonPath("$.data.boardDetailDTO.content").value("내용1"))
                .andExpect(jsonPath("$.data.boardDetailDTO.views").value(0))
                .andExpect(jsonPath("$.data.boardDetailDTO.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.data.boardDetailDTO.commentCount").value(0))
                .andExpect(jsonPath("$.data.boardDetailDTO.user.userId").value(1))
                .andExpect(jsonPath("$.data.boardDetailDTO.user.username").value("user1"))

                .andExpect(jsonPath("$.data.commentListDTOS").isEmpty())
                .andDo(MockMvcResultHandlers.print());

        verify(boardService).save(any(BoardSave_InDTO.class), eq(userId));
    }

    @Test
    @DisplayName("게시글 등록 실패 - title / content 유효성")
    public void save_titleContentValid_FailTest() throws Exception {
        // given
        Long userId = 1L;
        BoardSave_InDTO boardSaveInDTO = BoardSave_InDTO.builder()
                .title("")
                .content("")
                .build();
        String content = new ObjectMapper().writeValueAsString(boardSaveInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("입력 값 확인"))
                .andExpect(jsonPath("$.data.title").value("제목을 입력해주세요."))
                .andExpect(jsonPath("$.data.content").value("내용을 입력해주세요."))

                .andDo(MockMvcResultHandlers.print());

        verify(boardService, never()).save(any(BoardSave_InDTO.class), eq(userId));
    }

    private BoardSave_InDTO make_BoardSave_InDTO() {
        return BoardSave_InDTO.builder()
                .title("제목1")
                .content("내용1")
                .build();
    }

    private BoardDetail_OutDTO make_BoardDetail_OutDTO() {
        BoardDetailFlatDTO boardDetailFlatDTO = BoardDetailFlatDTO.builder()
                .boardId(1L)
                .title("제목1")
                .content("내용1")
                .views(0)
                .createdAt(LocalDateTime.now())
                .commentCount(0L)

                .userId(1L)
                .username("user1")
                .build();

        return new BoardDetail_OutDTO(boardDetailFlatDTO, 1L);
    }
}
