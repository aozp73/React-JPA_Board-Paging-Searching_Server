package com.example.demo.web.comment;

import com.example.demo.config.security.MyAuthenticationManagerConfig;
import com.example.demo.config.security.MySecurityConfig;
import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.module.comment.CommentController;
import com.example.demo.module.comment.CommentService;
import com.example.demo.module.comment.in_dto.CommentSave_InDTO;
import com.example.demo.module.comment.out_dto.CommentList_OutDTO;
import com.example.demo.util.TestSecurityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({MySecurityConfig.class, MyJwtProvider.class, MyAuthenticationManagerConfig.class}) // 추가 하지 않을 경우, 기본 Security 설정 사용
@WebMvcTest(CommentController.class)
public class CommentSaveControllerTest {

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
    @DisplayName("댓글 저장 성공")
    public void save_SuccessTest() throws Exception {
        // given
        Long userId = 1L;
        CommentSave_InDTO commentSaveInDTO = CommentSave_InDTO.builder()
                .boardId(27L)
                .content("테스트 댓글")
                .build();
        String content = new ObjectMapper().writeValueAsString(commentSaveInDTO);

        when(commentService.findAll(eq(commentSaveInDTO.getBoardId()), eq(userId))).thenReturn(make_CommentList_OutDTOS());

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))

                .andExpect(jsonPath("$.data[0].commentId").value(27))
                .andExpect(jsonPath("$.data[0].content").value("테스트 댓글 1"))
                .andExpect(jsonPath("$.data[0].editable").value(true))
                .andExpect(jsonPath("$.data[0].createdAt").value("2024.01.22 16:38:31"))
                .andExpect(jsonPath("$.data[0].user.userId").value(1))
                .andExpect(jsonPath("$.data[0].user.username").value("user1"))

                .andExpect(jsonPath("$.data[1].commentId").value(27))
                .andExpect(jsonPath("$.data[1].content").value("테스트 댓글 2"))
                .andExpect(jsonPath("$.data[1].editable").value(true))
                .andExpect(jsonPath("$.data[1].createdAt").value("2024.01.22 16:38:31"))
                .andExpect(jsonPath("$.data[1].user.userId").value(1))
                .andExpect(jsonPath("$.data[1].user.username").value("user1"))
                .andDo(MockMvcResultHandlers.print());

        verify(commentService).save(any(CommentSave_InDTO.class), any(Long.class));
        verify(commentService).findAll(any(Long.class), any(Long.class));
    }

    @Test
    @DisplayName("댓글 저장 실패 - 댓글 빈 값")
    public void save_contentEmpty_FailTest() throws Exception {
        // given
        Long userId = 1L;
        CommentSave_InDTO commentSaveInDTO = CommentSave_InDTO.builder()
                .boardId(27L)
                .content("")
                .build();
        String content = new ObjectMapper().writeValueAsString(commentSaveInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("입력 값 확인"))
                .andExpect(jsonPath("$.data.content").value("댓글 내용을 입력해주세요."))
                .andDo(MockMvcResultHandlers.print());

        verify(commentService, never()).save(any(CommentSave_InDTO.class), any(Long.class));
        verify(commentService, never()).findAll(any(Long.class), any(Long.class));
    }


    private List<CommentList_OutDTO> make_CommentList_OutDTOS() {
        List<CommentList_OutDTO> commentListOutDTOS = new ArrayList<>();

        commentListOutDTOS.add(make_CommentList_OutDTO("테스트 댓글 1"));
        commentListOutDTOS.add(make_CommentList_OutDTO("테스트 댓글 2"));

        return commentListOutDTOS;
    }

    private CommentList_OutDTO make_CommentList_OutDTO(String content) {
        CommentList_OutDTO.User user = CommentList_OutDTO.User.builder()
                .userId(1L)
                .username("user1")
                .build();

        return CommentList_OutDTO.builder()
                .commentId(27L)
                .content(content)
                .createdAt("2024.01.22 16:38:31")
                .editable(true)
                .user(user)
                .build();
    }
}
