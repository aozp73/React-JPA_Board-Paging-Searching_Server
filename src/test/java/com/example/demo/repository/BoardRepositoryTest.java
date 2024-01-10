package com.example.demo.repository;

import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardRepository;
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
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private TestEntityManager em;

    @BeforeEach
    public void init() {
        // rollBack_AutoIncrement
        em.getEntityManager().createNativeQuery("ALTER TABLE user_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        em.getEntityManager().createNativeQuery("ALTER TABLE board_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();

        /**
         * [초기 데이터 및 Save]
         * - User Entity 1건
         * - Board Entity 2건
         */
        User user = setUp_user("abc@naver.com", "abc", "1234", UserRole.COMMON);
        setUp_board(user, "저장 제목1", "저장 내용1", 1);
        setUp_board(user, "저장 제목2", "저장 내용2", 2);

        em.flush();
        em.clear();
    }

    @Test
    void findById() {
        // given
        Long boardId = 1L;

        // when
        Optional<Board> board = boardRepository.findById(boardId);

        // then
        assertTrue(board.isPresent());

        board.ifPresent(foundBoard -> {
            assertEquals(foundBoard.getTitle(), "저장 제목1");
            assertEquals(foundBoard.getContent(), "저장 내용1");
            assertEquals(foundBoard.getUser().getId(), 1L);
            assertEquals(foundBoard.getUser().getUsername(), "abc");
            assertEquals(foundBoard.getViews(), 1);
        });

        assertFalse(boardRepository.findById(3L).isPresent());
    }

    @Test
    void findAll() {
        // given

        // when
        List<Board> boards = boardRepository.findAll();

        // then
        assertEquals(boards.size(), 2);

        Board foundBoard1 = boards.get(0);
        assertEquals(foundBoard1.getTitle(), "저장 제목1");
        assertEquals(foundBoard1.getContent(), "저장 내용1");
        assertEquals(foundBoard1.getUser().getId(), 1L);
        assertEquals(foundBoard1.getUser().getUsername(), "abc");
        assertEquals(foundBoard1.getViews(), 1);

        Board foundBoard2 = boards.get(1);
        assertEquals(foundBoard2.getTitle(), "저장 제목2");
        assertEquals(foundBoard2.getContent(), "저장 내용2");
        assertEquals(foundBoard2.getUser().getId(), 1L);
        assertEquals(foundBoard2.getUser().getUsername(), "abc");
        assertEquals(foundBoard2.getViews(), 2);
    }

    @Test
    void update() {
        // given
        Long boardId = 1L;

        // when
        Optional<Board> board = boardRepository.findById(boardId);
        assertTrue(board.isPresent());

        board.ifPresent(val -> {
            val.setTitle("수정 제목");
            val.setContent("수정 내용");
            
            em.persist(val);
            em.flush();
            em.clear();
        });

        // then
        Optional<Board> updatedBoard = boardRepository.findById(boardId);
        assertTrue(updatedBoard.isPresent());

        updatedBoard.ifPresent(updated -> assertEquals(updated.getTitle(), "수정 제목"));
        updatedBoard.ifPresent(updated -> assertEquals(updated.getContent(), "수정 내용"));
    }

    @Test
    void delete() {
        // given
        Long boardId = 1L;

        // when
        boardRepository.deleteById(boardId);
        em.flush();
        em.clear();

        Optional<Board> deletedBoard = boardRepository.findById(boardId);

        // then
        assertFalse(deletedBoard.isPresent());
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

    private void setUp_board(User user, String title, String content, int views) {
        Board board = Board.builder()
                .user(user)
                .title(title)
                .content(content)
                .views(views)
                .createdAt(LocalDateTime.now())
                .build();

        this.em.persist(board);
    }
}
