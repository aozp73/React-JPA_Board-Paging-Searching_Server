package com.example.demo.integration.comment;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.module.board.Board;
import com.example.demo.module.comment.in_dto.CommentSave_InDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CommentSaveIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EntityManager em;

    @BeforeEach
    public void init() {
        TestSecurityHelper.setAuthentication();

        // rollBack_AutoIncrement
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE board_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE comment_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();

        /**
         * [초기 데이터 및 Save]
         * - board Entity 1건
         * - user Entity 3건
         * - comment Entity 2건
         */
        User user1 = DummyEntityHelper.setUpUser(em, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        User user2 = DummyEntityHelper.setUpUser(em, "user2@naver.com", "user2", "abc2", UserRole.COMMON);
        User user3 = DummyEntityHelper.setUpUser(em, "user3@naver.com", "user3", "abc3", UserRole.COMMON);

        Board board1 = DummyEntityHelper.setUpBoard(em, user1, "제목1", "내용1", 10);

        DummyEntityHelper.setUpComment(em, user2, board1, "댓글1");
        DummyEntityHelper.setUpComment(em, user3, board1, "댓글2");

        em.flush();
        em.clear();
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("댓글 저장 성공")
    public void save_success() throws Exception {
        // given
        CommentSave_InDTO commentSaveInDTO = CommentSave_InDTO.builder()
                .boardId(1L)
                .content("저장 댓글1")
                .build();
        String content = new ObjectMapper().writeValueAsString(commentSaveInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))

                .andExpect(jsonPath("$.data[0].commentId").value(1))
                .andExpect(jsonPath("$.data[0].content").value("댓글1"))
                .andExpect(jsonPath("$.data[0].editable").value(false))
                .andExpect(jsonPath("$.data[0].createdAt").value("2022.02.12 10:10:10"))
                .andExpect(jsonPath("$.data[0].user.userId").value(2))
                .andExpect(jsonPath("$.data[0].user.username").value("user2"))

                .andExpect(jsonPath("$.data[1].commentId").value(2))
                .andExpect(jsonPath("$.data[1].content").value("댓글2"))
                .andExpect(jsonPath("$.data[1].editable").value(false))
                .andExpect(jsonPath("$.data[1].createdAt").value("2022.02.12 10:10:10"))
                .andExpect(jsonPath("$.data[1].user.userId").value(3))
                .andExpect(jsonPath("$.data[1].user.username").value("user3"))

                .andExpect(jsonPath("$.data[2].commentId").value(3))
                .andExpect(jsonPath("$.data[2].content").value("저장 댓글1"))
                .andExpect(jsonPath("$.data[2].editable").value(true))
                .andExpect(jsonPath("$.data[2].user.userId").value(1))
                .andExpect(jsonPath("$.data[2].user.username").value("user1"))

                .andDo(MockMvcResultHandlers.print());

        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("댓글 저장 실패 - 댓글 내용 공백")
    public void save_fail_validContent() throws Exception {
        // given
        CommentSave_InDTO commentSaveInDTO = CommentSave_InDTO.builder()
                .boardId(1L)
                .content("")
                .build();
        String content = new ObjectMapper().writeValueAsString(commentSaveInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("입력 값 확인"))
                .andExpect(jsonPath("$.data.content").value("댓글 내용을 입력해주세요."))

                .andDo(MockMvcResultHandlers.print());

        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("댓글 저장 실패 - 존재하지 않는 게시글")
    public void save_fail_notExistBoard() throws Exception {
        // given
        CommentSave_InDTO commentSaveInDTO = CommentSave_InDTO.builder()
                .boardId(2L)
                .content("저장 댓글1")
                .build();
        String content = new ObjectMapper().writeValueAsString(commentSaveInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
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
