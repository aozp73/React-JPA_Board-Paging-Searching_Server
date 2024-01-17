package com.example.demo.integration.board;

import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardService;
import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import com.example.demo.module.comment.Comment;
import com.example.demo.module.refreshtoken.RefreshTokenRepository;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BoardListIntegrationTest {

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
        User user1 = setUp_user("user1@naver.com", "user1", "1234", UserRole.COMMON);
        User user2 = setUp_user("user2@naver.com", "user2", "5678", UserRole.ADMIN);

        Board board1 = setUp_board(user1, "제목 1", "내용 1", 1);
        setUp_board(user1, "제목 2", "내용 2", 2);
        setUp_board(user1, "제목 3", "내용 3", 3);
        setUp_board(user1, "제목 4", "내용 4", 4);
        setUp_board(user1, "제목 5", "내용 5", 5);

        setUp_board(user2, "제목 6", "내용 6", 6);
        Board board7 = setUp_board(user2, "제목 7", "내용 7", 7);

        setUp_Comment(user1, board1, "댓글 1");
        setUp_Comment(user2, board7, "댓글 2");

        em.flush();
        em.clear();
    }

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

                .andExpect(jsonPath("$.data.boardList.content", hasSize(5)))
                .andExpect(jsonPath("$.data.boardList.content[0].boardId").value(7))
                .andExpect(jsonPath("$.data.boardList.content[0].title").value("제목 7"))
                .andExpect(jsonPath("$.data.boardList.content[0].views").value("7"))
                .andExpect(jsonPath("$.data.boardList.content[0].commentCount").value(1))
                .andExpect(jsonPath("$.data.boardList.content[0].user.userId").value(2))
                .andExpect(jsonPath("$.data.boardList.content[0].user.username").value("user2"))

                .andExpect(jsonPath("$.data.boardList.content[4].boardId").value(3))
                .andExpect(jsonPath("$.data.boardList.content[4].title").value("제목 3"))
                .andExpect(jsonPath("$.data.boardList.content[4].views").value("3"))
                .andExpect(jsonPath("$.data.boardList.content[4].commentCount").value(0))
                .andExpect(jsonPath("$.data.boardList.content[4].user.userId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[4].user.username").value("user1"))

                .andExpect(jsonPath("$.data.pageInfo.startPage").value(1))
                .andExpect(jsonPath("$.data.pageInfo.endPage").value(2))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("게시글 목록조회 성공 - 두번째 페이지")
    public void list_secondPage_SuccessTest() throws Exception {
        // given
        String param = "?searchType=title&searchKeyword=제목&page=1";

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
                .andExpect(jsonPath("$.data.boardList.content[0].title").value("제목 2"))
                .andExpect(jsonPath("$.data.boardList.content[0].views").value("2"))
                .andExpect(jsonPath("$.data.boardList.content[0].commentCount").value(0))
                .andExpect(jsonPath("$.data.boardList.content[0].user.userId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[0].user.username").value("user1"))

                .andExpect(jsonPath("$.data.boardList.content[1].boardId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[1].title").value("제목 1"))
                .andExpect(jsonPath("$.data.boardList.content[1].views").value("1"))
                .andExpect(jsonPath("$.data.boardList.content[1].commentCount").value(1))
                .andExpect(jsonPath("$.data.boardList.content[1].user.userId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[1].user.username").value("user1"))

                .andExpect(jsonPath("$.data.pageInfo.startPage").value(1))
                .andExpect(jsonPath("$.data.pageInfo.endPage").value(2))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("게시글 목록조회 성공 - 글쓴이 검색")
    public void list_searchByAuthor_SuccessTest() throws Exception {
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
                .andExpect(jsonPath("$.data.boardList.content[0].title").value("제목 5"))
                .andExpect(jsonPath("$.data.boardList.content[0].views").value("5"))
                .andExpect(jsonPath("$.data.boardList.content[0].commentCount").value(0))
                .andExpect(jsonPath("$.data.boardList.content[0].user.userId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[0].user.username").value("user1"))

                .andExpect(jsonPath("$.data.boardList.content[4].boardId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[4].title").value("제목 1"))
                .andExpect(jsonPath("$.data.boardList.content[4].views").value("1"))
                .andExpect(jsonPath("$.data.boardList.content[4].commentCount").value(1))
                .andExpect(jsonPath("$.data.boardList.content[4].user.userId").value(1))
                .andExpect(jsonPath("$.data.boardList.content[4].user.username").value("user1"))

                .andExpect(jsonPath("$.data.pageInfo.startPage").value(1))
                .andExpect(jsonPath("$.data.pageInfo.endPage").value(1))
                .andDo(MockMvcResultHandlers.print());
    }

    private User setUp_user(String email, String username, String password, UserRole role) {
        User user = User.builder()
                .email(email)
                .username(username)
                .password(password)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        return this.em.merge(user);
    }

    private Board setUp_board(User user, String title, String content, Integer views) {
        Board board = Board.builder()
                .user(user)
                .title(title)
                .content(content)
                .views(views)
                .createdAt(LocalDateTime.now())
                .build();

        return this.em.merge(board);
    }

    private void setUp_Comment(User user, Board board, String content) {
        Comment comment = Comment.builder()
                .user(user)
                .board(board)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        this.em.persist(comment);
    }
}
