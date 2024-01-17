package com.example.demo.domain.board;

import com.example.demo.module.board.Board;
import com.example.demo.module.board.BoardQueryRepository;
import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import com.example.demo.module.board.out_dto.BoardListDTO;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest // 단순 @Repository 빈 등록 x
@Import(BoardQueryRepository.class) // Config 클래스로 대체 가능
public class BoardListRepositoryTest {

    @Autowired
    private BoardQueryRepository boardQueryRepository;
    @Autowired
    private TestEntityManager em;

    @BeforeEach
    public void init() {
        // rollBack_AutoIncrement
        em.getEntityManager().createNativeQuery("ALTER TABLE user_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        em.getEntityManager().createNativeQuery("ALTER TABLE board_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();

        /**
         * [초기 데이터 및 Save]
         * - board Entity 7건
         * - user Entity 2건
         */
        User abc = setUp_user("abc@naver.com", "abc", "1234", UserRole.COMMON);
        User def = setUp_user("def@naver.com", "def", "5678", UserRole.ADMIN);

        setUp_board(abc, "제목 1", "내용 1", 1);
        setUp_board(abc, "제목 2", "내용 2", 2);
        setUp_board(abc, "제목 3", "내용 3", 3);
        setUp_board(abc, "제목 4", "내용 4", 4);
        setUp_board(abc, "제목 5", "내용 5", 5);

        setUp_board(def, "제목 6", "내용 6", 6);
        setUp_board(def, "제목 7", "내용 7", 7);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("첫 페이지")
    public void findAllWithUserForList_FirstPage_Test() {
        // given
        BoardListSearch_InDTO searchCond = new BoardListSearch_InDTO();
        Pageable pageable = PageRequest.of(0, 5); // 첫 페이지, 페이지 당 5개

        // when
        Page<BoardListDTO> result = boardQueryRepository.findAllWithUserForList(searchCond, pageable);

        // then - .orderBy(board.createdAt.desc());
        assertNotNull(result);

        assertEquals(5, result.getContent().size());

        assertEquals("제목 7", result.getContent().get(0).getTitle());
        assertEquals(7, result.getContent().get(0).getViews());
        assertEquals("def", result.getContent().get(0).getUser().getUsername());

        assertEquals("제목 3", result.getContent().get(4).getTitle());
        assertEquals(3, result.getContent().get(4).getViews());
        assertEquals("abc", result.getContent().get(4).getUser().getUsername());
    }

    @Test
    @DisplayName("두번째 페이지")
    public void findAllWithUserForList_SecondPage_Test() {
        // given
        BoardListSearch_InDTO searchCond = new BoardListSearch_InDTO();
        Pageable pageable = PageRequest.of(1, 5); // 두번째 페이지, 페이지 당 5개

        // when
        Page<BoardListDTO> result = boardQueryRepository.findAllWithUserForList(searchCond, pageable);

        // then - .orderBy(board.createdAt.desc());
        assertNotNull(result);

        assertEquals(2, result.getContent().size());

        assertEquals("제목 2", result.getContent().get(0).getTitle());
        assertEquals(2, result.getContent().get(0).getViews());
        assertEquals("abc", result.getContent().get(0).getUser().getUsername());

        assertEquals("제목 1", result.getContent().get(1).getTitle());
        assertEquals(1, result.getContent().get(1).getViews());
        assertEquals("abc", result.getContent().get(1).getUser().getUsername());
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

    private void setUp_board(User user, String title, String content, Integer views) {
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
