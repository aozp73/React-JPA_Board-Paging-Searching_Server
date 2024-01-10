package com.example.demo.domain.comment;

import com.example.demo.module.board.Board;
import com.example.demo.module.comment.Comment;
import com.example.demo.module.comment.CommentRepository;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
public class CommentRepositoryEntityTest {

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
         * - User Entity 1건
         * - Board Entity 1건
         * - Comment Entity 2건
         */
        User user = setUp_user("abc@naver.com", "abc", "1234", UserRole.COMMON);
        Board board = setUp_board(user, "저장 제목", "저장 내용", 1);
        setUp_comment(user, board, "저장 댓글1");
        setUp_comment(user, board, "저장 댓글2");

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
            assertEquals(foundComment.getUser().getId(), 1L);
            assertEquals(foundComment.getUser().getUsername(), "abc");
            assertEquals(foundComment.getBoard().getId(), 1L);
            assertEquals(foundComment.getBoard().getTitle(), "저장 제목");
            assertEquals(foundComment.getContent(), "저장 댓글1");
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
        assertEquals(foundComment1.getUser().getId(), 1L);
        assertEquals(foundComment1.getUser().getUsername(), "abc");
        assertEquals(foundComment1.getBoard().getId(), 1L);
        assertEquals(foundComment1.getBoard().getTitle(), "저장 제목");
        assertEquals(foundComment1.getContent(), "저장 댓글1");

        Comment foundComment2 = comments.get(1);
        assertEquals(foundComment2.getUser().getId(), 1L);
        assertEquals(foundComment2.getUser().getUsername(), "abc");
        assertEquals(foundComment2.getBoard().getId(), 1L);
        assertEquals(foundComment2.getBoard().getTitle(), "저장 제목");
        assertEquals(foundComment2.getContent(), "저장 댓글2");
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

    private Board setUp_board(User user, String title, String content, int views) {
        Board board = Board.builder()
                .user(user)
                .title(title)
                .content(content)
                .views(views)
                .createdAt(LocalDateTime.now())
                .build();

        return this.em.persist(board);
    }

    private void setUp_comment(User user, Board board, String content) {
        Comment comment = Comment.builder()
                .user(user)
                .board(board)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        this.em.persist(comment);
    }
}
