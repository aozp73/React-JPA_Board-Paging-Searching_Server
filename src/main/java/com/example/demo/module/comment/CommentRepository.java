package com.example.demo.module.comment;

import com.example.demo.module.comment.out_dto.CommentListFlatDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT new com.example.demo.module.comment.out_dto.CommentListFlatDTO(c.id, c.user.id, c.user.username, c.content, " +
            "c.createdAt) " +
            "FROM Comment c " +
            "WHERE c.board.id = :boardId")
    List<CommentListFlatDTO> findAllWithCommentForDetail(@Param("boardId") Long boardId);
}
