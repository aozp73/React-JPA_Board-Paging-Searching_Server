package com.example.demo.module.board;

import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import com.example.demo.module.board.out_dto.BoardListDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.demo.module.board.QBoard.board;
import static com.example.demo.module.comment.QComment.comment;
import static com.example.demo.module.user.QUser.user;

@Repository
public class BoardQueryRepository {

    private final JPAQueryFactory query;

    public BoardQueryRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Transactional(readOnly = true)
    public Page<BoardListDTO> findAllWithUserForList(BoardListSearch_InDTO searchCond, Pageable pageable) {

        JPAQuery<BoardListDTO> jpaQuery = query.select(Projections.constructor(BoardListDTO.class,
                        board.id, board.title, board.views,
                        Expressions.stringTemplate("CONCAT(YEAR({0}), '/', LPAD(MONTH({0}), 2, '0'), '/', LPAD(DAY({0}), 2, '0'))", board.createdAt),
                        Expressions.numberTemplate(Integer.class, "COUNT({0})", comment.id),
                        Projections.fields(BoardListDTO.User.class, board.user.id.as("userId"), user.username.as("username"))
                ))
                .from(board)
                .innerJoin(board.user, user)
                .leftJoin(comment).on(board.id.eq(comment.board.id))
                .where(likeTitle(searchCond.getSearchKeyword(), searchCond.getSearchType()),
                        likeAuthor(searchCond.getSearchKeyword(), searchCond.getSearchType()))
                .groupBy(board.id)
                .orderBy(board.id.desc());

        long total = jpaQuery.fetchCount();
        List<BoardListDTO> content = jpaQuery
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset()) // Client가 page param 넘기면, 자동계산 (page * size)
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression likeTitle(String title, String searchType) {
        if ("title".equals(searchType) && StringUtils.hasText(title)) {
            return board.title.like("%" + title + "%");
        }
        return null;
    }

    private BooleanExpression likeAuthor(String username, String searchType) {
        if ("author".equals(searchType) && StringUtils.hasText(username)) {
            return user.username.like("%" + username + "%");
        }
        return null;
    }
}
