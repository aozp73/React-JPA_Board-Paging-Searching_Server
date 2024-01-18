package com.example.demo.domain.user;

import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
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
public class UserEntityRepositoryTest {

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
    void findById() {
        // given
        Long userId = 1L;

        // when
        Optional<User> user = userRepository.findById(userId);

        // then
        assertTrue(user.isPresent());

        user.ifPresent(foundUser -> {
            assertEquals(foundUser.getEmail(), "user1@naver.com");
            assertEquals(foundUser.getUsername(), "user1");
            assertEquals(foundUser.getPassword(), "abc1");
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
        assertEquals(foundUser1.getEmail(), "user1@naver.com");
        assertEquals(foundUser1.getUsername(), "user1");
        assertEquals(foundUser1.getPassword(), "abc1");
        assertEquals(foundUser1.getRole(), UserRole.COMMON);

        User foundUser2 = users.get(1);
        assertEquals(foundUser2.getEmail(), "user2@naver.com");
        assertEquals(foundUser2.getUsername(), "user2");
        assertEquals(foundUser2.getPassword(), "abc2");
        assertEquals(foundUser2.getRole(), UserRole.COMMON);
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
}

