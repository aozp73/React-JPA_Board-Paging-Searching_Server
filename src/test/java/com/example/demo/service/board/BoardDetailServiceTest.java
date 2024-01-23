package com.example.demo.service.board;

import com.example.demo.config.security.principal.MyUserDetails;
import com.example.demo.module.board.BoardRepository;
import com.example.demo.module.board.BoardService;
import com.example.demo.module.board.out_dto.*;
import com.example.demo.module.comment.CommentRepository;
import com.example.demo.module.comment.out_dto.CommentListFlatDTO;
import com.example.demo.module.comment.out_dto.CommentList_OutDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoardDetailServiceTest {

    @InjectMocks private BoardService boardService;

    @Mock private BoardRepository boardRepository;
    @Mock private CommentRepository commentRepository;

    @Test
    @DisplayName("게시글 상세 조회 성공")
    public void findDetailById_Success() {
        // given
        Long boardId = 1L;

        when(boardRepository.findBoardDetailWithUserForDetail(boardId)).thenReturn(make_BoardDetailFlatDTO());
        when(commentRepository.findAllWithCommentForDetail(boardId)).thenReturn(make_CommentListFlatDTOS());

        // when
        BoardDetail_OutDTO result = boardService.findDetailById(boardId, any(MyUserDetails.class));


        // then
        BoardDetailDTO boardDetailDTO = result.getBoardDetailDTO();
        List<CommentList_OutDTO> commentListDTOS = result.getCommentListDTOS();

        assertEquals(1L, boardDetailDTO.getBoardId());
        assertEquals("테스트 제목", boardDetailDTO.getTitle());
        assertEquals(3, boardDetailDTO.getViews());
        assertEquals("테스트 내용", boardDetailDTO.getContent());
        assertEquals(2L, boardDetailDTO.getCommentCount());
        assertEquals(1L, boardDetailDTO.getUser().getUserId());
        assertEquals("테스트 유저 1", boardDetailDTO.getUser().getUsername());

        assertNotNull(commentListDTOS);
        assertEquals(2, commentListDTOS.size());

        CommentList_OutDTO firstComment = commentListDTOS.get(0);
        assertEquals(1L, firstComment.getCommentId());
        assertEquals(2L, firstComment.getUser().getUserId());
        assertEquals("테스트 유저 2", firstComment.getUser().getUsername());
        assertEquals("테스트 댓글 1", firstComment.getContent());

        CommentList_OutDTO secondComment = commentListDTOS.get(1);
        assertEquals(2L, secondComment.getCommentId());
        assertEquals(3L, secondComment.getUser().getUserId());
        assertEquals("테스트 유저 3", secondComment.getUser().getUsername());
        assertEquals("테스트 댓글 2", secondComment.getContent());
    }

    private BoardDetailFlatDTO make_BoardDetailFlatDTO() {

        return BoardDetailFlatDTO.builder()
                .boardId(1L)
                .title("테스트 제목")
                .views(3)
                .content("테스트 내용")
                .createdAt(LocalDateTime.now())
                .commentCount(2L)
                .userId(1L)
                .username("테스트 유저 1")
                .build();
    }

    private List<CommentListFlatDTO> make_CommentListFlatDTOS() {

        CommentListFlatDTO commentListFlatDTO1 = CommentListFlatDTO.builder()
                .id(1L)
                .userId(2L)
                .username("테스트 유저 2")
                .content("테스트 댓글 1")
                .editable(null)
                .createdAt(LocalDateTime.now())
                .build();

        CommentListFlatDTO commentListFlatDTO2 = CommentListFlatDTO.builder()
                .id(2L)
                .userId(3L)
                .username("테스트 유저 3")
                .content("테스트 댓글 2")
                .editable(null)
                .createdAt(LocalDateTime.now())
                .build();

        List<CommentListFlatDTO> commentListFlatDTOS = new ArrayList<>();
        commentListFlatDTOS.add(commentListFlatDTO1);
        commentListFlatDTOS.add(commentListFlatDTO2);

        return commentListFlatDTOS;
    }

}
