package com.example.demo.domain.comment;

import com.example.demo.module.board.Board;
import com.example.demo.module.comment.Comment;
import com.example.demo.module.comment.CommentRepository;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import com.example.demo.util.DummyEntityHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
public class CommentEntityRepositoryTest {

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
         * - User Entity 1건
         * - Board Entity 1건
         * - Comment Entity 2건
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
    void findById() {
        // given
        Long commentId = 1L;

        // when
        Optional<Comment> comment = commentRepository.findById(commentId);

        // then
        assertTrue(comment.isPresent());

        comment.ifPresent(foundComment -> {
            assertEquals(foundComment.getUser().getId(), 2L);
            assertEquals(foundComment.getUser().getUsername(), "user2");
            assertEquals(foundComment.getBoard().getId(), 1L);
            assertEquals(foundComment.getBoard().getTitle(), "제목1");
            assertEquals(foundComment.getContent(), "댓글1");
        });

        assertFalse(commentRepository.findById(3L).isPresent());
    }

    @Test
    void findAll() {
        // given

        // when
        List<Comment> comments = commentRepository.findAll();

        // then
        assertEquals(comments.size(), 2);

        Comment foundComment1 = comments.get(0);
        assertEquals(foundComment1.getId(), 1L);
        assertEquals(foundComment1.getUser().getId(), 2L);
        assertEquals(foundComment1.getUser().getUsername(), "user2");
        assertEquals(foundComment1.getBoard().getId(), 1L);
        assertEquals(foundComment1.getBoard().getTitle(), "제목1");
        assertEquals(foundComment1.getContent(), "댓글1");

        Comment foundComment2 = comments.get(1);
        assertEquals(foundComment2.getId(), 2L);
        assertEquals(foundComment2.getUser().getId(), 3L);
        assertEquals(foundComment2.getUser().getUsername(), "user3");
        assertEquals(foundComment2.getBoard().getId(), 1L);
        assertEquals(foundComment2.getBoard().getTitle(), "제목1");
        assertEquals(foundComment2.getContent(), "댓글2");
    }

    @Test
    void update() {
        // given
        Long commentId = 1L;

        // when
        Optional<Comment> comment = commentRepository.findById(commentId);
        assertTrue(comment.isPresent());

        comment.ifPresent(val -> {
            val.setContent("수정 댓글");

            em.persist(val);
            em.flush();
            em.clear();
        });

        // then
        Optional<Comment> updatedComment = commentRepository.findById(commentId);
        assertTrue(updatedComment.isPresent());

        updatedComment.ifPresent(updated -> assertEquals(updated.getContent(), "수정 댓글"));
    }

    @Test
    void delete() {
        // given
        Long commentId = 1L;

        // when
        commentRepository.deleteById(commentId);
        em.flush();
        em.clear();

        Optional<Comment> deletedComment = commentRepository.findById(commentId);

        // then
        assertFalse(deletedComment.isPresent());
    }
}
