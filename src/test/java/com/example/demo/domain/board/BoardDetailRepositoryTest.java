package com.example.demo.domain.board;

import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardRepository;
import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import com.example.demo.module.board.out_dto.BoardDetailFlatDTO;
import com.example.demo.module.board.out_dto.BoardListDTO;
import com.example.demo.module.comment.Comment;
import com.example.demo.module.comment.CommentRepository;
import com.example.demo.module.comment.out_dto.CommentListFlatDTO;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
public class BoardDetailRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TestEntityManager em;

    @BeforeEach
    public void init() {
        // rollBack_AutoIncrement
        em.getEntityManager().createNativeQuery("ALTER TABLE user_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        em.getEntityManager().createNativeQuery("ALTER TABLE board_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        em.getEntityManager().createNativeQuery("ALTER TABLE comment_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();

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
    @DisplayName("게시글 정보 조회 성공")
    public void findBoardDetailWithUserForDetail_successTest() {
        // given
        Long boardId = 2L;

        // when
        BoardDetailFlatDTO result = boardRepository.findBoardDetailWithUserForDetail(boardId);

        // then
        assertNotNull(result);

        assertEquals(1L, result.getBoardId());
        assertEquals("테스트 제목", result.getTitle());
        assertEquals("테스트 내용", result.getContent());
        assertEquals(10, result.getViews());
        assertEquals(2, result.getCommentCount());

        assertEquals(1L, result.getUserId());
        assertEquals("user1", result.getUsername());
    }

    @Test
    @DisplayName("Return null - 존재하지 않는 아이디")
    public void findBoardDetailWithUserForDetail_failTest() {
        /**
         * null return 시
         * - findBoardDetailWithUserForDetail() 호출 전, BoardService.viewsCount()가 호출된다.
         * - board: viewsCount()에서 게시글 존재 유무를 사전에 파악
         * - comment: null이어도 get에 의한 NullPointerException 로직이 없다.
         */

        // given
        Long boardId = 2L;

        // when
        BoardDetailFlatDTO result = boardRepository.findBoardDetailWithUserForDetail(boardId);

        // then
        assertNull(result);
    }

    @Test
    @DisplayName("댓글 정보 조회 성공")
    public void findAllWithCommentForDetail_successTest() {
        /**
         * Board Topic에서 사용되는 CommentRepository 메서드
         */

        // given
        Long boardId = 1L;

        // when
        List<CommentListFlatDTO> result = commentRepository.findAllWithCommentForDetail(boardId);

        // then
        assertNotNull(result);

        assertEquals(2, result.size());
        CommentListFlatDTO firstComment = result.get(0);
        CommentListFlatDTO secondComment = result.get(1);

        assertEquals(1L, firstComment.getId());
        assertEquals("테스트 댓글 1", firstComment.getContent());
        assertEquals(2L, firstComment.getUserId());
        assertEquals("user2", firstComment.getUsername());
        assertNull(firstComment.getEditable());

        assertEquals(2L, secondComment.getId());
        assertEquals("테스트 댓글 2", secondComment.getContent());
        assertEquals(3L, secondComment.getUserId());
        assertEquals("user3", secondComment.getUsername());
        assertNull(secondComment.getEditable());
    }

    private User setUp_user(String email, String username, String password, UserRole role) {
        User user = User.builder()
                .email(email)
                .username(username)
                .password(password)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        return this.em.persist(user);
    }

    private Board setUp_board(User user, String title, String content, Integer views) {
        Board board = Board.builder()
                .user(user)
                .title(title)
                .content(content)
                .views(views)
                .createdAt(LocalDateTime.now())
                .build();

        return this.em.persist(board);
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
