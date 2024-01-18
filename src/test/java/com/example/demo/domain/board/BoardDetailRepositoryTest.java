package com.example.demo.domain.board;

import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardRepository;
import com.example.demo.module.board.out_dto.BoardDetailFlatDTO;
import com.example.demo.module.comment.CommentRepository;
import com.example.demo.module.comment.out_dto.CommentListFlatDTO;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import com.example.demo.util.DummyEntityHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
    private EntityManager em;

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
    @DisplayName("게시글 정보 조회 성공")
    public void findBoardDetailWithUserForDetail_successTest() {
        // given
        Long boardId = 1L;

        // when
        BoardDetailFlatDTO result = boardRepository.findBoardDetailWithUserForDetail(boardId);

        // then
        assertNotNull(result);

        assertEquals(1L, result.getBoardId());
        assertEquals("제목1", result.getTitle());
        assertEquals("내용1", result.getContent());
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
        assertEquals("댓글1", firstComment.getContent());
        assertEquals(2L, firstComment.getUserId());
        assertEquals("user2", firstComment.getUsername());
        assertNull(firstComment.getEditable());

        assertEquals(2L, secondComment.getId());
        assertEquals("댓글2", secondComment.getContent());
        assertEquals(3L, secondComment.getUserId());
        assertEquals("user3", secondComment.getUsername());
        assertNull(secondComment.getEditable());
    }
}
