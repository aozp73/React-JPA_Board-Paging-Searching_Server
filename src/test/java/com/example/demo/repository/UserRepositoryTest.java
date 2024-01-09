package com.example.demo.repository;

import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
import com.example.demo.module.user.enums.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager em;

    @BeforeEach
    public void rollBack_AutoIncrement() {

        em.getEntityManager().createNativeQuery("ALTER TABLE user_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();
    }


    @Test
    void findById() {
        // given
        Long userId = 1L;
        setUp("abc@naver.com", "abc", "1234", UserRole.COMMON);
        em.flush();
        em.clear();

        // when
        Optional<User> user = userRepository.findById(userId);

        // then
        assertThrows(NoSuchElementException.class, () -> userRepository.findById(2L).get());
        assertThrows(NoSuchElementException.class, () -> userRepository.findById(3L).get());

        User foundUser = user.get();
        assertEquals(foundUser.getEmail(), "abc@naver.com");
        assertEquals(foundUser.getUsername(), "abc");
        assertEquals(foundUser.getPassword(), "1234");
        assertEquals(foundUser.getRole(), UserRole.COMMON);
    }

    @Test
    void update() {
        // given
        Long userId = 1L;
        setUp("abc@naver.com", "abc", "1234", UserRole.COMMON);
        em.flush();
        em.clear();

        // when
        Optional<User> user = userRepository.findById(userId);
        user.ifPresent(val -> val.setUsername("update"));
        em.flush();
        em.clear();

        Optional<User> updateUser = userRepository.findById(1L);

        // then
        assertEquals(updateUser.get().getUsername(), "update");
    }

    @Test
    void delete() {
        // given
        Long userId = 1L;
        setUp("abc@naver.com", "abc", "1234", UserRole.COMMON);
        em.flush();
        em.clear();

        // when
        userRepository.deleteById(userId);
        em.flush();
        em.clear();

        Optional<User> updateUser = userRepository.findById(1L);

        // then
        assertThrows(NoSuchElementException.class, () -> updateUser.get());
    }

    @Test
    void findAll() {
        // given
        setUp("abc@naver.com", "abc", "1234", UserRole.COMMON);
        setUp("def@naver.com", "def", "5678", UserRole.ADMIN);
        em.flush();
        em.clear();

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

    private void setUp(String email, String username, String password, UserRole role) {
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

