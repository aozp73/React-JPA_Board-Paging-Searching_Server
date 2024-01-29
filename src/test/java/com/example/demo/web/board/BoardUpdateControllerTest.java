package com.example.demo.web.board;

import com.example.demo.config.security.MyAuthenticationManagerConfig;
import com.example.demo.config.security.MySecurityConfig;
import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.module.board.BoardController;
import com.example.demo.module.board.BoardService;
import com.example.demo.module.board.in_dto.BoardUpdate_InDTO;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({MySecurityConfig.class, MyJwtProvider.class, MyAuthenticationManagerConfig.class}) // 추가 하지 않을 경우, 기본 Security 설정 사용
@WebMvcTest(BoardController.class)
public class BoardUpdateControllerTest {

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
    @DisplayName("게시글 수정 성공")
    public void update_SuccessTest() throws Exception {
        // given
        Long userId = 1L;
        BoardUpdate_InDTO boardUpdateInDTO = make_BoardUpdate_InDTO();
        String content = new ObjectMapper().writeValueAsString(boardUpdateInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/auth/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcResultHandlers.print());

        verify(boardService).update(any(BoardUpdate_InDTO.class), eq(userId));
    }


    private BoardUpdate_InDTO make_BoardUpdate_InDTO() {
        return BoardUpdate_InDTO.builder()
                .id(1L)
                .title("수정 제목1")
                .content("수정 내용1")
                .build();
    }

    private BoardDetail_OutDTO make_BoardDetail_OutDTO() {
        BoardDetailFlatDTO boardDetailFlatDTO = BoardDetailFlatDTO.builder()
                .boardId(1L)
                .title("수정 제목1")
                .content("수정 내용1")
                .views(0)
                .createdAt(LocalDateTime.now())
                .commentCount(0L)

                .userId(1L)
                .username("user1")
                .build();

        return new BoardDetail_OutDTO(boardDetailFlatDTO, 1L);
    }
}
