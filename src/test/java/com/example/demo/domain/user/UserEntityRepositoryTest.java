package com.example.demo.domain.user;

import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
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
public class UserEntityRepositoryTest {

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
    void findById() {
        // given
        Long userId = 1L;

        // when
        Optional<User> user = userRepository.findById(userId);

        // then
        assertTrue(user.isPresent());

        user.ifPresent(foundUser -> {
            assertEquals(foundUser.getEmail(), "abc@naver.com");
            assertEquals(foundUser.getUsername(), "abc");
            assertEquals(foundUser.getPassword(), "1234");
            assertEquals(foundUser.getRole(), UserRole.COMMON);
        });

        assertFalse(userRepository.findById(3L).isPresent());
    }

    @Test
    void findAll() {
        // given

        // when
        List<User> users = userRepository.findAll();

        // then
        assertEquals(users.size(), 2);

        User foundUser1 = users.get(0);
        assertEquals(foundUser1.getEmail(), "abc@naver.com");
        assertEquals(foundUser1.getUsername(), "abc");
        assertEquals(foundUser1.getPassword(), "1234");
        assertEquals(foundUser1.getRole(), UserRole.COMMON);

        User foundUser2 = users.get(1);
        assertEquals(foundUser2.getEmail(), "def@naver.com");
        assertEquals(foundUser2.getUsername(), "def");
        assertEquals(foundUser2.getPassword(), "5678");
        assertEquals(foundUser2.getRole(), UserRole.ADMIN);
    }

    @Test
    void update() {
        // given
        Long userId = 1L;

        // when
        Optional<User> user = userRepository.findById(userId);
        assertTrue(user.isPresent());

        user.ifPresent(val -> {
            val.setUsername("update");

            em.persist(val);
            em.flush();
            em.clear();
        });

        // then
        Optional<User> updatedUser = userRepository.findById(userId);
        assertTrue(updatedUser.isPresent());

        updatedUser.ifPresent(updated -> assertEquals(updated.getUsername(), "update"));
    }

    @Test
    void delete() {
        // given
        Long userId = 1L;

        // when
        userRepository.deleteById(userId);
        em.flush();
        em.clear();

        Optional<User> deletedUser = userRepository.findById(userId);

        // then
        assertFalse(deletedUser.isPresent());
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

