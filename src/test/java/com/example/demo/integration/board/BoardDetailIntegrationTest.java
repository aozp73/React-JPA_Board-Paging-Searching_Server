package com.example.demo.integration.board;

import com.example.demo.module.board.Board;
import com.example.demo.module.comment.Comment;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BoardDetailIntegrationTest {

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
        User user1 = setUp_user("user1@naver.com", "user1", "abc1", UserRole.COMMON);
        User user2 = setUp_user("user2@naver.com", "user2", "abc2", UserRole.COMMON);
        User user3 = setUp_user("user3@naver.com", "user3", "abc3", UserRole.COMMON);

        Board board1 = setUp_board(user1, "테스트 제목", "테스트 내용", 10);

        setUp_Comment(user2, board1, "테스트 댓글 1");
        setUp_Comment(user3, board1, "테스트 댓글 2");

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("게시글 상세조회 성공")
    public void detail_SuccessTest() throws Exception {
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
                .andExpect(jsonPath("$.data.boardDetailDTO.title").value("테스트 제목"))
                .andExpect(jsonPath("$.data.boardDetailDTO.views").value(11))
                .andExpect(jsonPath("$.data.boardDetailDTO.content").value("테스트 내용"))
                .andExpect(jsonPath("$.data.boardDetailDTO.createdAt").value("2022.02.10 20:30:00"))
                .andExpect(jsonPath("$.data.boardDetailDTO.commentCount").value(2L))
                .andExpect(jsonPath("$.data.boardDetailDTO.user.userId").value(1L))
                .andExpect(jsonPath("$.data.boardDetailDTO.user.username").value("user1"))

                .andExpect(jsonPath("$.data.commentListDTOS[0].commentId").value(1L))
                .andExpect(jsonPath("$.data.commentListDTOS[0].content").value("테스트 댓글 1"))
                .andExpect(jsonPath("$.data.commentListDTOS[0].user.userId").value(2L))
                .andExpect(jsonPath("$.data.commentListDTOS[0].user.username").value("user2"))
                .andExpect(jsonPath("$.data.commentListDTOS[0].createdAt").value("2022.02.10 20:30:00"))

                .andExpect(jsonPath("$.data.commentListDTOS[1].commentId").value(2L))
                .andExpect(jsonPath("$.data.commentListDTOS[1].content").value("테스트 댓글 2"))
                .andExpect(jsonPath("$.data.commentListDTOS[1].user.userId").value(3L))
                .andExpect(jsonPath("$.data.commentListDTOS[1].user.username").value("user3"))
                .andExpect(jsonPath("$.data.commentListDTOS[1].createdAt").value("2022.02.10 20:30:00"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("게시글 상세조회 실패 - 없는 게시글")
    public void detail_notExixtBoardId_FailTest() throws Exception {
        // given
        Long boardId = 2L;

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/board/" + boardId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").value("게시물이 존재하지 않습니다."))
                .andDo(MockMvcResultHandlers.print());
    }

    private User setUp_user(String email, String username, String password, UserRole role) {
        User user = User.builder()
                .email(email)
                .username(username)
                .password(password)
                .role(role)
                .createdAt(LocalDateTime.of(2022, 2, 10, 20, 30, 0))
                .build();

        return this.em.merge(user);
    }

    private Board setUp_board(User user, String title, String content, Integer views) {
        Board board = Board.builder()
                .user(user)
                .title(title)
                .content(content)
                .views(views)
                .createdAt(LocalDateTime.of(2022, 2, 10, 20, 30, 0))
                .build();

        return this.em.merge(board);
    }

    private void setUp_Comment(User user, Board board, String content) {
        Comment comment = Comment.builder()
                .user(user)
                .board(board)
                .content(content)
                .createdAt(LocalDateTime.of(2022, 2, 10, 20, 30, 0))
                .build();

        this.em.persist(comment);
    }


}
