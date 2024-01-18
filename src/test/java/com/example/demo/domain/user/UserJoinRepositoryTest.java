package com.example.demo.domain.user;

import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
import com.example.demo.module.user.enums.UserRole;
import com.example.demo.util.DummyEntityHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
public class UserJoinRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void init() {
        // rollBack_AutoIncrement
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();

        /**
         * [초기 데이터 및 Save]
         * - User Entity 2건
         */
        DummyEntityHelper.setUpUser(em, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        DummyEntityHelper.setUpUser(em, "user2@naver.com", "user2", "abc2", UserRole.COMMON);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("findByEmail 성공")
    void findByEmail_success() {
        // given
        String email1 = "user1@naver.com";
        String email2 = "user2@naver.com";

        // when
        Optional<User> user1 = userRepository.findByEmail(email1);
        Optional<User> user2 = userRepository.findByEmail(email2);

        // then
        assertTrue(user1.isPresent());
        assertTrue(user2.isPresent());

        user1.ifPresent(foundUser -> {
            assertEquals(foundUser.getId(), 1L);
            assertEquals(foundUser.getEmail(), "user1@naver.com");
            assertEquals(foundUser.getUsername(), "user1");
            assertEquals(foundUser.getPassword(), "abc1");
            assertEquals(foundUser.getRole(), UserRole.COMMON);
        });

        user2.ifPresent(foundUser -> {
            assertEquals(foundUser.getId(), 2L);
            assertEquals(foundUser.getEmail(), "user2@naver.com");
            assertEquals(foundUser.getUsername(), "user2");
            assertEquals(foundUser.getPassword(), "abc2");
            assertEquals(foundUser.getRole(), UserRole.COMMON);
        });
    }

    @Test
    @DisplayName("findByEmail 실패")
    void findByEmail_fail() {
        // given
        String email1 = "wrong@naver.com";

        // when
        Optional<User> user1 = userRepository.findByEmail(email1);

        // then
        assertFalse(user1.isPresent());
    }
}
