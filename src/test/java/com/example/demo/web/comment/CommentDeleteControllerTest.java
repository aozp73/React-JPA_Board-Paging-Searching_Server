package com.example.demo.web.comment;

import com.example.demo.config.security.MyAuthenticationManagerConfig;
import com.example.demo.config.security.MySecurityConfig;
import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.module.comment.CommentController;
import com.example.demo.module.comment.CommentService;
import com.example.demo.module.comment.out_dto.CommentList_OutDTO;
import com.example.demo.util.TestSecurityHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({MySecurityConfig.class, MyJwtProvider.class, MyAuthenticationManagerConfig.class}) // 추가 하지 않을 경우, 기본 Security 설정 사용
@WebMvcTest(CommentController.class)
public class CommentDeleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @BeforeEach
    public void setUp() {
        TestSecurityHelper.setAuthentication();
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    public void delete_SuccessTest() throws Exception {
        // given
        Long userId = 1L;
        Long boardId = 1L;
        Long commentId = 3L;

        when(commentService.findAll(eq(boardId), eq(userId))).thenReturn(make_CommentList_OutDTOS());

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/auth/comment/" + boardId+ "/" + commentId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))

                .andExpect(jsonPath("$.data.size()").value(2))

                .andExpect(jsonPath("$.data[0].commentId").value(1))
                .andExpect(jsonPath("$.data[0].content").value("댓글 1"))
                .andExpect(jsonPath("$.data[0].editable").value(true))
                .andExpect(jsonPath("$.data[0].createdAt").value("2024.01.22 16:38:31"))
                .andExpect(jsonPath("$.data[0].user.userId").value(1))
                .andExpect(jsonPath("$.data[0].user.username").value("user1"))

                .andExpect(jsonPath("$.data[1].commentId").value(2))
                .andExpect(jsonPath("$.data[1].content").value("댓글 2"))
                .andExpect(jsonPath("$.data[1].editable").value(true))
                .andExpect(jsonPath("$.data[1].createdAt").value("2024.01.22 16:38:31"))
                .andExpect(jsonPath("$.data[1].user.userId").value(1))
                .andExpect(jsonPath("$.data[1].user.username").value("user1"))

                .andDo(MockMvcResultHandlers.print());

        verify(commentService).delete(any(Long.class), any(Long.class), any(Long.class));
        verify(commentService).findAll(any(Long.class), any(Long.class));
    }

    private List<CommentList_OutDTO> make_CommentList_OutDTOS() {
        List<CommentList_OutDTO> commentListOutDTOS = new ArrayList<>();

        commentListOutDTOS.add(make_CommentList_OutDTO(1L, "댓글 1"));
        commentListOutDTOS.add(make_CommentList_OutDTO(2L, "댓글 2"));

        return commentListOutDTOS;
    }

    private CommentList_OutDTO make_CommentList_OutDTO(Long commentId, String content) {
        CommentList_OutDTO.User user = CommentList_OutDTO.User.builder()
                .userId(1L)
                .username("user1")
                .build();

        return CommentList_OutDTO.builder()
                .commentId(commentId)
                .content(content)
                .createdAt("2024.01.22 16:38:31")
                .editable(true)
                .user(user)
                .build();
    }
}
