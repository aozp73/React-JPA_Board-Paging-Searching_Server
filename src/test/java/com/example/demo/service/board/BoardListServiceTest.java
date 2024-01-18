package com.example.demo.service.board;

import com.example.demo.module.board.BoardQueryRepository;
import com.example.demo.module.board.BoardService;
import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import com.example.demo.module.board.out_dto.BoardListDTO;
import com.example.demo.module.board.out_dto.BoardList_OutDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoardListServiceTest {

    @InjectMocks // 해당 파일에 있는 @Mock Bean 주입
    private BoardService boardService;

    @Mock
    private BoardQueryRepository boardQueryRepository;

    @Test
    @DisplayName("게시글 목록 조회 성공")
    public void findAllTest_Success() {
        // given
        BoardListSearch_InDTO searchDTO = new BoardListSearch_InDTO();
        Pageable pageable = PageRequest.of(0, 5);

        List<BoardListDTO> boardListContent = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            BoardListDTO boardListDTO = new BoardListDTO();
            boardListContent.add(boardListDTO);
        }
        Page<BoardListDTO> mockedPage = new PageImpl<>(boardListContent, pageable, boardListContent.size());


        when(boardQueryRepository.findAllWithUserForList(searchDTO, pageable)).thenReturn(mockedPage);

        // when
        BoardList_OutDTO result = boardService.findAll(searchDTO, pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.getPageInfo().getStartPage());
        assertEquals(2, result.getPageInfo().getEndPage());
        assertEquals(5, result.getBoardList().getSize());

        verify(boardQueryRepository).findAllWithUserForList(searchDTO, pageable);
    }
}
