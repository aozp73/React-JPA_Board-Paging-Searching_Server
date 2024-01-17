package com.example.demo.module.board;

import com.example.demo.exception.ResponseDTO;
import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import com.example.demo.module.board.out_dto.BoardListDTO;
import com.example.demo.module.board.out_dto.BoardList_OutDTO;
import com.example.demo.module.board.out_dto.PageInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor @Slf4j
@RequestMapping("/api")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board")
    public ResponseEntity<?> list(@ModelAttribute BoardListSearch_InDTO boardListSearchInDTO,
                       @PageableDefault(size = 5) Pageable pageable) {
        log.debug("GET - 게시글 목록 페이지");

        return ResponseEntity.ok().body(new ResponseDTO<>().data(boardService.findAll(boardListSearchInDTO, pageable)));
    }
}
