package com.example.demo.domain.comment;

import com.example.demo.module.board.Board;
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
public class CommentFindAllRepositoryTest {

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
    @DisplayName("댓글 정보 조회 성공")
    public void findAllWithCommentForDetail_successTest() {
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
