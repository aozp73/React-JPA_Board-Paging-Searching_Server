package com.example.demo.integration.board;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import com.example.demo.util.DummyEntityHelper;
import com.example.demo.util.TestSecurityHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BoardUpdateFormIntegrationTest extends AbstractIntegrationTest {

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


    @Test
    @DisplayName("게시글 수정 페이지 응답 성공")
    public void updateForm_success() throws Exception {
        // given
        Long boardId = 1L;

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
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("게시글 수정 페이지 응답 실패 - 다른 작성자의 글")
    public void updateForm_fail_notMatchWriter() throws Exception {
        // given
        Long boardId = 2L;

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/auth/board/" + boardId)
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

    @Test
    @DisplayName("게시글 수정 페이지 응답 실패 - 존재하지 않는 게시글 요청")
    public void updateForm_fail_notExistBoard() throws Exception {
        // given
        Long boardId = 3L;

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/auth/board/" + boardId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("notFound"))
                .andExpect(jsonPath("$.data").value("게시물이 존재하지 않습니다."))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

}
