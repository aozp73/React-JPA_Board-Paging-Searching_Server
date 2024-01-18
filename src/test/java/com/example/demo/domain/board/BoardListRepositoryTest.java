package com.example.demo.domain.board;

import com.example.demo.module.board.BoardQueryRepository;
import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import com.example.demo.module.board.out_dto.BoardListDTO;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import com.example.demo.util.DummyEntityHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest // 단순 @Repository 빈 등록 x
@Import(BoardQueryRepository.class) // Config 클래스로 대체 가능
public class    BoardListRepositoryTest {

    @Autowired
    private BoardQueryRepository boardQueryRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void init() {
        // rollBack_AutoIncrement
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE board_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();

        /**
         * [초기 데이터 및 Save]
         * - board Entity 7건
         * - user Entity 2건
         */
        User user1 = DummyEntityHelper.setUpUser(em, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        User user2 = DummyEntityHelper.setUpUser(em, "user2@naver.com", "user2", "abc2", UserRole.COMMON);

        DummyEntityHelper.setUpBoard(em, user1, "제목1", "내용1", 10);
        DummyEntityHelper.setUpBoard(em, user1, "제목2", "내용2", 20);
        DummyEntityHelper.setUpBoard(em, user1, "제목3", "내용3", 30);
        DummyEntityHelper.setUpBoard(em, user1, "제목4", "내용4", 40);
        DummyEntityHelper.setUpBoard(em, user1, "제목5", "내용5", 50);

        DummyEntityHelper.setUpBoard(em, user2, "제목6", "내용6", 60);
        DummyEntityHelper.setUpBoard(em, user2, "제목7", "내용7", 70);

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

        assertEquals("제목7", result.getContent().get(0).getTitle());
        assertEquals(70, result.getContent().get(0).getViews());
        assertEquals("user2", result.getContent().get(0).getUser().getUsername());

        assertEquals("제목3", result.getContent().get(4).getTitle());
        assertEquals(30, result.getContent().get(4).getViews());
        assertEquals("user1", result.getContent().get(4).getUser().getUsername());
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

        assertEquals("제목2", result.getContent().get(0).getTitle());
        assertEquals(20, result.getContent().get(0).getViews());
        assertEquals("user1", result.getContent().get(0).getUser().getUsername());

        assertEquals("제목1", result.getContent().get(1).getTitle());
        assertEquals(10, result.getContent().get(1).getViews());
        assertEquals("user1", result.getContent().get(1).getUser().getUsername());
    }

}
