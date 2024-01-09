package com.example.demo.module.board;

import com.example.demo.module.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "board_tb")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 40)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int views;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
