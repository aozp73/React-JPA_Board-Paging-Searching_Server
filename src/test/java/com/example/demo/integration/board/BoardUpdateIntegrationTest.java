package com.example.demo.integration.board;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.module.board.in_dto.BoardUpdate_InDTO;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import com.example.demo.util.DummyEntityHelper;
import com.example.demo.util.TestSecurityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BoardUpdateIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EntityManager em;


    @BeforeEach
    public void setUp() {
        TestSecurityHelper.setAuthentication();

        // rollBack_AutoIncrement
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE board_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();

        /**
         * [초기 데이터 및 Save]
         * - user Entity 2건
         * - board Entity 2건
         */
        User user1 = DummyEntityHelper.setUpUser(em, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        User user2 = DummyEntityHelper.setUpUser(em, "user2@naver.com", "user2", "abc2", UserRole.COMMON);

        DummyEntityHelper.setUpBoard(em, user1, "제목1", "내용1", 10);
        DummyEntityHelper.setUpBoard(em, user2, "제목2", "내용2", 10);

        em.flush();
        em.clear();
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("게시글 수정 성공")
    public void update_success() throws Exception {
        // given
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
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("게시글 수정 실패 - 다른 작성자의 글")
    public void update_fail_notMatchWriter() throws Exception {
        // given
        BoardUpdate_InDTO boardUpdateInDTO = BoardUpdate_InDTO.builder().id(2L).title("수정 제목1").content("수정 제목2").build();
        String content = new ObjectMapper().writeValueAsString(boardUpdateInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/auth/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.msg").value("unAuthorized"))
                .andExpect(jsonPath("$.data").value("작성자만 수정할 수 있습니다."))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }


    private BoardUpdate_InDTO make_BoardUpdate_InDTO() {
        return BoardUpdate_InDTO.builder()
                .id(1L)
                .title("수정 제목1")
                .content("수정 내용1")
                .build();
    }
}
