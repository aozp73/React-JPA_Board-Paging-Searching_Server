package com.example.demo.web.board;

import com.example.demo.config.security.MyAuthenticationManagerConfig;
import com.example.demo.config.security.MySecurityConfig;
import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.module.board.BoardController;
import com.example.demo.module.board.BoardService;
import com.example.demo.module.board.out_dto.BoardDetailDTO;
import com.example.demo.module.board.out_dto.BoardDetail_OutDTO;
import com.example.demo.module.comment.out_dto.CommentList_OutDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({MySecurityConfig.class, MyJwtProvider.class, MyAuthenticationManagerConfig.class}) // 추가 하지 않을 경우, 기본 Security 설정 사용
@WebMvcTest(BoardController.class)
public class BoardDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BoardService boardService;

    @Test
    @DisplayName("게시글 상세조회 성공")
    public void detail_SuccessTest() throws Exception {
        // given
        Long boardId = 1L;
        when(boardService.findDetailById(anyLong())).thenReturn(make_BoardDetail_OutDTO());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/board/" + boardId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))

                .andExpect(jsonPath("$.data.boardDetailDTO.boardId").value(1L))
                .andExpect(jsonPath("$.data.boardDetailDTO.title").value("테스트 제목"))
                .andExpect(jsonPath("$.data.boardDetailDTO.views").value(3))
                .andExpect(jsonPath("$.data.boardDetailDTO.content").value("테스트 내용"))
                .andExpect(jsonPath("$.data.boardDetailDTO.createdAt").value("2024.03.11 10:23:41"))
                .andExpect(jsonPath("$.data.boardDetailDTO.commentCount").value(2L))
                .andExpect(jsonPath("$.data.boardDetailDTO.user.userId").value(1L))
                .andExpect(jsonPath("$.data.boardDetailDTO.user.username").value("테스트 유저 1"))

                .andExpect(jsonPath("$.data.commentListDTOS[0].commentId").value(1L))
                .andExpect(jsonPath("$.data.commentListDTOS[0].content").value("테스트 댓글 1"))
                .andExpect(jsonPath("$.data.commentListDTOS[0].user.userId").value(2L))
                .andExpect(jsonPath("$.data.commentListDTOS[0].user.username").value("테스트 유저 2"))
                .andExpect(jsonPath("$.data.commentListDTOS[0].createdAt").value("2024.03.13 11:13:41"))

                .andExpect(jsonPath("$.data.commentListDTOS[1].commentId").value(2L))
                .andExpect(jsonPath("$.data.commentListDTOS[1].content").value("테스트 댓글 2"))
                .andExpect(jsonPath("$.data.commentListDTOS[1].user.userId").value(3L))
                .andExpect(jsonPath("$.data.commentListDTOS[1].user.username").value("테스트 유저 3"))
                .andExpect(jsonPath("$.data.commentListDTOS[1].createdAt").value("2024.03.13 13:13:41"))
                .andDo(MockMvcResultHandlers.print());

        verify(boardService).viewsCount(anyLong());
        verify(boardService).findDetailById(anyLong());
    }

    private BoardDetail_OutDTO make_BoardDetail_OutDTO() {
        BoardDetailDTO boardDetailDTO = BoardDetailDTO.builder()
                .boardId(1L)
                .title("테스트 제목")
                .views(3)
                .content("테스트 내용")
                .createdAt("2024.03.11 10:23:41")
                .commentCount(2L)
                .user(new BoardDetailDTO.User(1L, "테스트 유저 1"))
                .build();

        CommentList_OutDTO commentListDTO1 = CommentList_OutDTO.builder()
                .commentId(1L)
                .content("테스트 댓글 1")
                .createdAt("2024.03.13 11:13:41")
                .editable(null)
                .user(new CommentList_OutDTO.User(2L, "테스트 유저 2"))
                .build();
        CommentList_OutDTO commentListDTO2 = CommentList_OutDTO.builder()
                .commentId(2L)
                .content("테스트 댓글 2")
                .createdAt("2024.03.13 13:13:41")
                .editable(null)
                .user(new CommentList_OutDTO.User(3L, "테스트 유저 3"))
                .build();
        List<CommentList_OutDTO> commentListDTOS = new ArrayList<>();
        commentListDTOS.add(commentListDTO1);
        commentListDTOS.add(commentListDTO2);

        return new BoardDetail_OutDTO(boardDetailDTO, commentListDTOS);
    }

}
