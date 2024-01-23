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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BoardListIntegrationTest extends AbstractIntegrationTest {

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
         * - board Entity 7건
         * - user Entity 2건
         * - comment Entity 2건
         */
        User user1 = DummyEntityHelper.setUpUser(em, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        User user2 = DummyEntityHelper.setUpUser(em, "user2@naver.com", "user2", "abc2", UserRole.COMMON);

        Board board1 = DummyEntityHelper.setUpBoard(em, user1, "제목1", "내용1", 10);
        Board board2 = DummyEntityHelper.setUpBoard(em, user1, "제목2", "내용2", 20);
        Board board3 = DummyEntityHelper.setUpBoard(em, user1, "제목3", "내용3", 30);
        Board board4 = DummyEntityHelper.setUpBoard(em, user1, "제목4", "내용4", 40);
        Board board5 = DummyEntityHelper.setUpBoard(em, user1, "제목5", "내용5", 50);

        Board board6 = DummyEntityHelper.setUpBoard(em, user2, "제목6", "내용6", 60);
        Board board7 = DummyEntityHelper.setUpBoard(em, user2, "제목7", "내용7", 70);

        DummyEntityHelper.setUpComment(em, user1, board1, "댓글1");
        DummyEntityHelper.setUpComment(em, user2, board7, "댓글2");

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("게시글 목록조회 성공 - 첫 요청")
    public void list_success_firstPage() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/board")
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))

                .andExpect(jsonPath("$.data.boardList.content", hasSize(5)))
                .andExpect(jsonPath("$.data.boardList.content[0].boardId").value(7))
                .andExpect(jsonPath("$.data.boardList.content[0].title").value("제목7"))
                .andExpect(jsonPath("$.data.boardList.content[0].views").value("70"))
                .andExpect(jsonPath("$.data.boardList.content[0].commentCount").value(1))
                .andExpect(jsonPath("$.data.boardList.content[0].user.userId").value(2))
                .andExpect(jsonPath("$.data.boardList.content[0].user.username").value("user2"))

                .andExpect(jsonPath("$.data.boardList.content[4].boardId").value(3))
                .andExpect(jsonPath("$.data.boardList.content[4].title").value("제목3"))
                .andExpect(jsonPath("$.data.boardList.content[4].views").value("30"))
                .andExpect(jsonPath("$.data.boardList.content[4].commentCount").value(0))
                .andExpect(jsonPath("$.data.boardList.content[4].user.userId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[4].user.username").value("user1"))

                .andExpect(jsonPath("$.data.pageInfo.startPage").value(1))
                .andExpect(jsonPath("$.data.pageInfo.endPage").value(2))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("게시글 목록조회 성공 - 두번째 페이지")
    public void list_success_secondPage() throws Exception {
        // given
        String param = "?page=1";

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/board" + param)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))

                .andExpect(jsonPath("$.data.boardList.content", hasSize(2)))
                .andExpect(jsonPath("$.data.boardList.content[0].boardId").value(2))
                .andExpect(jsonPath("$.data.boardList.content[0].title").value("제목2"))
                .andExpect(jsonPath("$.data.boardList.content[0].views").value("20"))
                .andExpect(jsonPath("$.data.boardList.content[0].commentCount").value(0))
                .andExpect(jsonPath("$.data.boardList.content[0].user.userId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[0].user.username").value("user1"))

                .andExpect(jsonPath("$.data.boardList.content[1].boardId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[1].title").value("제목1"))
                .andExpect(jsonPath("$.data.boardList.content[1].views").value("10"))
                .andExpect(jsonPath("$.data.boardList.content[1].commentCount").value(1))
                .andExpect(jsonPath("$.data.boardList.content[1].user.userId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[1].user.username").value("user1"))

                .andExpect(jsonPath("$.data.pageInfo.startPage").value(1))
                .andExpect(jsonPath("$.data.pageInfo.endPage").value(2))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("게시글 목록조회 성공 - 글쓴이 검색")
    public void list_success_searchByAuthor() throws Exception {
        // given
        String param = "?searchType=author&searchKeyword=user1&page=0";

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/board" + param)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))

                .andExpect(jsonPath("$.data.boardList.content", hasSize(5)))
                .andExpect(jsonPath("$.data.boardList.content[0].boardId").value(5))
                .andExpect(jsonPath("$.data.boardList.content[0].title").value("제목5"))
                .andExpect(jsonPath("$.data.boardList.content[0].views").value("50"))
                .andExpect(jsonPath("$.data.boardList.content[0].commentCount").value(0))
                .andExpect(jsonPath("$.data.boardList.content[0].user.userId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[0].user.username").value("user1"))

                .andExpect(jsonPath("$.data.boardList.content[4].boardId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[4].title").value("제목1"))
                .andExpect(jsonPath("$.data.boardList.content[4].views").value("10"))
                .andExpect(jsonPath("$.data.boardList.content[4].commentCount").value(1))
                .andExpect(jsonPath("$.data.boardList.content[4].user.userId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[4].user.username").value("user1"))

                .andExpect(jsonPath("$.data.pageInfo.startPage").value(1))
                .andExpect(jsonPath("$.data.pageInfo.endPage").value(1))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
