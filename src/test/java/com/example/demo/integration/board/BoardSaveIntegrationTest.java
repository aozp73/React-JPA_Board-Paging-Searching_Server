package com.example.demo.integration.board;

import com.example.demo.module.board.in_dto.BoardSave_InDTO;
import com.example.demo.module.user.enums.UserRole;
import com.example.demo.util.DummyEntityHelper;
import com.example.demo.util.TestSecurityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BoardSaveIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EntityManager em;

    @BeforeEach
    public void setUp() {
        TestSecurityHelper.setAuthentication();

        // rollBack_AutoIncrement
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE comment_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();

        /**
         * [초기 데이터 및 Save]
         * - user Entity 1건
         */
        DummyEntityHelper.setUpUser(em, "user1@naver.com", "user1", "abc1", UserRole.COMMON);

    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("게시글 등록 성공")
    public void save_SuccessTest() throws Exception {
        // given
        BoardSave_InDTO boardSaveInDTO = make_BoardSave_InDTO();
        String content = new ObjectMapper().writeValueAsString(boardSaveInDTO);

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
    }


    @Test
    @DisplayName("게시글 등록 실패 - title / content 유효성")
    public void save_titleContentValid_FailTest() throws Exception {
        // given
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
    }

    private BoardSave_InDTO make_BoardSave_InDTO() {
        return BoardSave_InDTO.builder()
                .title("제목1")
                .content("내용1")
                .build();
    }

}
