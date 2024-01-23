package com.example.demo.integration.board;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.module.board.Board;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import com.example.demo.util.DummyEntityHelper;
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
public class BoardDetailIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EntityManager em;

    @BeforeEach
    public void init() {
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

    @Test
    @DisplayName("게시글 상세조회 성공")
    public void detail_success() throws Exception {
        // given
        Long boardId = 1L;

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/board/" + boardId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))

                .andExpect(jsonPath("$.data.boardDetailDTO.boardId").value(1L))
                .andExpect(jsonPath("$.data.boardDetailDTO.title").value("제목1"))
                .andExpect(jsonPath("$.data.boardDetailDTO.views").value(11))
                .andExpect(jsonPath("$.data.boardDetailDTO.content").value("내용1"))
                .andExpect(jsonPath("$.data.boardDetailDTO.createdAt").value(DummyEntityHelper.boardDetailTime))
                .andExpect(jsonPath("$.data.boardDetailDTO.commentCount").value(2L))
                .andExpect(jsonPath("$.data.boardDetailDTO.user.userId").value(1L))
                .andExpect(jsonPath("$.data.boardDetailDTO.user.username").value("user1"))

                .andExpect(jsonPath("$.data.commentListDTOS[0].commentId").value(1L))
                .andExpect(jsonPath("$.data.commentListDTOS[0].content").value("댓글1"))
                .andExpect(jsonPath("$.data.commentListDTOS[0].editable").value(false))
                .andExpect(jsonPath("$.data.commentListDTOS[0].user.userId").value(2L))
                .andExpect(jsonPath("$.data.commentListDTOS[0].user.username").value("user2"))
                .andExpect(jsonPath("$.data.commentListDTOS[0].createdAt").value(DummyEntityHelper.boardDetailTime))

                .andExpect(jsonPath("$.data.commentListDTOS[1].commentId").value(2L))
                .andExpect(jsonPath("$.data.commentListDTOS[1].content").value("댓글2"))
                .andExpect(jsonPath("$.data.commentListDTOS[1].editable").value(false))
                .andExpect(jsonPath("$.data.commentListDTOS[1].user.userId").value(3L))
                .andExpect(jsonPath("$.data.commentListDTOS[1].user.username").value("user3"))
                .andExpect(jsonPath("$.data.commentListDTOS[1].createdAt").value(DummyEntityHelper.boardDetailTime))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("게시글 상세조회 실패 - 존재하지 않는 게시글")
    public void detail_fail_notExistBoard() throws Exception {
        // given
        Long boardId = 2L;

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/board/" + boardId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").value("게시물이 존재하지 않습니다."))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
