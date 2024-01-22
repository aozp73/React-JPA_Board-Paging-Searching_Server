package com.example.demo.util;

import com.example.demo.module.board.Board;
import com.example.demo.module.comment.Comment;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

public class DummyEntityHelper {

    private static final LocalDateTime specificDateTime = LocalDateTime.of(2022, 2, 12, 10, 10, 10);
    public static String boardDetailTime = "2022.02.12 10:10:10";

    public static User setUpUser(EntityManager em, String email, String username, String password, UserRole role) {
        User user = User.builder()
                .email(email)
                .username(username)
                .password(password)
                .role(role)
                .createdAt(specificDateTime)
                .build();

        em.persist(user);

        return user;
    }
    public static User setUpUser(Long id, String email, String username, String password, UserRole role) {

        return User.builder()
                .id(id)
                .email(email)
                .username(username)
                .password(password)
                .role(role)
                .createdAt(specificDateTime)
                .build();
    }

    public static Board setUpBoard(EntityManager em, User user, String title, String content, Integer views) {
        Board board = Board.builder()
                .user(user)
                .title(title)
                .content(content)
                .views(views)
                .createdAt(specificDateTime)
                .updated_at(specificDateTime)
                .build();

        em.persist(board);

        return board;
    }
    public static Board setUpBoard(Long id, User user, String title, String content, Integer views) {

        return Board.builder()
                .id(id)
                .user(user)
                .title(title)
                .content(content)
                .views(views)
                .createdAt(specificDateTime)
                .updated_at(specificDateTime)
                .build();
    }

    public static Comment setUpComment(EntityManager em, User user, Board board, String content) {
        Comment comment = Comment.builder()
                .user(user)
                .board(board)
                .content(content)
                .createdAt(specificDateTime)
                .updatedAt(specificDateTime)
                .build();

        em.persist(comment);

        return comment;
    }
    public static Comment setUpComment(Long id, User user, Board board, String content) {

        return Comment.builder()
                .id(id)
                .user(user)
                .board(board)
                .content(content)
                .createdAt(specificDateTime)
                .updatedAt(specificDateTime)
                .build();
    }
}
