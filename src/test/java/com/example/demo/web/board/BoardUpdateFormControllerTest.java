package com.example.demo.web.board;

import com.example.demo.config.security.MyAuthenticationManagerConfig;
import com.example.demo.config.security.MySecurityConfig;
import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.module.board.BoardController;
import com.example.demo.module.board.BoardService;
import com.example.demo.module.board.out_dto.BoardUpdate_OutDTO;
import com.example.demo.util.TestSecurityHelper;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({MySecurityConfig.class, MyJwtProvider.class, MyAuthenticationManagerConfig.class}) // 추가 하지 않을 경우, 기본 Security 설정 사용
@WebMvcTest(BoardController.class)
public class BoardUpdateFormControllerTest {

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
    @DisplayName("게시글 수정 페이지 응답 성공")
    public void updateForm_SuccessTest() throws Exception {
        // given
        Long boardId = 1L;
        Long userId = 1L;

        when(boardService.updateForm(eq(boardId), eq(userId))).thenReturn(make_BoardUpdate_OutDTO());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/auth/board/" + boardId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))

                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.title").value("제목1"))
                .andExpect(jsonPath("$.data.content").value("내용1"))
                .andDo(MockMvcResultHandlers.print());

        verify(boardService).updateForm(eq(boardId), eq(userId));
    }

    private BoardUpdate_OutDTO make_BoardUpdate_OutDTO() {

        return BoardUpdate_OutDTO.builder()
                .id(1L)
                .userId(1L)
                .title("제목1")
                .content("내용1")
                .build();
    }
}
