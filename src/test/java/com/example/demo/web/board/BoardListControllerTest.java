package com.example.demo.web.board;

import com.example.demo.config.security.MyAuthenticationManagerConfig;
import com.example.demo.config.security.MySecurityConfig;
import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.module.board.BoardController;
import com.example.demo.module.board.BoardService;
import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({MySecurityConfig.class, MyJwtProvider.class, MyAuthenticationManagerConfig.class}) // 추가 하지 않을 경우, 기본 Security 설정 사용
@WebMvcTest(BoardController.class)
public class BoardListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Captor
    ArgumentCaptor<BoardListSearch_InDTO> searchCaptor;
    @Captor
    ArgumentCaptor<Pageable> pageableCaptor;

    @Test
    @DisplayName("게시글 목록조회 성공 - 첫 요청")
    public void list_firstPage_SuccessTest() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/board")
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcResultHandlers.print());
        verify(boardService).findAll(any(BoardListSearch_InDTO.class), any(Pageable.class));

            // params 검증
            verify(boardService).findAll(searchCaptor.capture(), pageableCaptor.capture());
            BoardListSearch_InDTO capturedSearchDTO = searchCaptor.getValue();
            Pageable capturedPageable = pageableCaptor.getValue();

            assertEquals(0, capturedPageable.getPageNumber());
            assertEquals(5, capturedPageable.getPageSize());
            assertEquals(0, capturedPageable.getOffset());
            assertNull(capturedSearchDTO.getSearchKeyword());
            assertNull(capturedSearchDTO.getSearchType());
    }

    @Test
    @DisplayName("게시글 목록조회 성공 - parameter 전달")
    public void list_transmitParam_SuccessTest() throws Exception {
        // given
        String param = "?searchType=title&searchKeyword=aozp&page=1";

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/board" + param)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcResultHandlers.print());
        verify(boardService).findAll(any(BoardListSearch_InDTO.class), any(Pageable.class));

            // params 검증
            verify(boardService).findAll(searchCaptor.capture(), pageableCaptor.capture());
            BoardListSearch_InDTO capturedSearchDTO = searchCaptor.getValue();
            Pageable capturedPageable = pageableCaptor.getValue();

            assertEquals(1, capturedPageable.getPageNumber());
            assertEquals(5, capturedPageable.getPageSize());
            assertEquals(5, capturedPageable.getOffset());
            assertEquals("title", capturedSearchDTO.getSearchType());
            assertEquals("aozp", capturedSearchDTO.getSearchKeyword());
    }
}
