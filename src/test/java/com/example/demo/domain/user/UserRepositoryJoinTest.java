package com.example.demo.domain.user;

import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
import com.example.demo.module.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
public class UserRepositoryJoinTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager em;

    @BeforeEach
    public void init() {
        // rollBack_AutoIncrement
        em.getEntityManager().createNativeQuery("ALTER TABLE user_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();

        /**
         * [초기 데이터 및 Save]
         * - User Entity 2건
         */
        setUp_user("abc@naver.com", "abc", "1234", UserRole.COMMON);
        setUp_user("def@naver.com", "def", "5678", UserRole.ADMIN);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("findByEmail 성공")
    void findByEmail_success() {
        // given
        String email1 = "abc@naver.com";
        String email2 = "def@naver.com";

        // when
        Optional<User> user1 = userRepository.findByEmail(email1);
        Optional<User> user2 = userRepository.findByEmail(email2);

        // then
        assertTrue(user1.isPresent());
        assertTrue(user2.isPresent());

        user1.ifPresent(foundUser -> {
            assertEquals(foundUser.getId(), 1L);
            assertEquals(foundUser.getEmail(), "abc@naver.com");
            assertEquals(foundUser.getUsername(), "abc");
            assertEquals(foundUser.getPassword(), "1234");
            assertEquals(foundUser.getRole(), UserRole.COMMON);
        });

        user2.ifPresent(foundUser -> {
            assertEquals(foundUser.getId(), 2L);
            assertEquals(foundUser.getEmail(), "def@naver.com");
            assertEquals(foundUser.getUsername(), "def");
            assertEquals(foundUser.getPassword(), "5678");
            assertEquals(foundUser.getRole(), UserRole.ADMIN);
        });
    }

    @Test
    @DisplayName("findByEmail 실패")
    void findByEmail_fail() {
        // given
        String email1 = "ghi@naver.com";

        // when
        Optional<User> user1 = userRepository.findByEmail(email1);

        // then
        assertFalse(user1.isPresent());
    }

    private void setUp_user(String email, String username, String password, UserRole role) {
        User user = User.builder()
                .email(email)
                .username(username)
                .password(password)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        this.em.persist(user);
    }
}
