package com.example.demo.module.board;

import com.example.demo.config.security.principal.MyUserDetails;
import com.example.demo.exception.ResponseDTO;
import com.example.demo.module.board.in_dto.BoardListSearch_InDTO;
import com.example.demo.module.board.in_dto.BoardSave_InDTO;
import com.example.demo.module.board.in_dto.BoardUpdate_InDTO;
import com.example.demo.module.board.out_dto.BoardDetail_OutDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor @Slf4j
@RequestMapping("/api")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board")
    public ResponseEntity<?> list(@ModelAttribute BoardListSearch_InDTO boardListSearchInDTO,
                                  @PageableDefault(size = 5) Pageable pageable) {
        log.debug("게시글 목록 - GET, Controller");
        int page = pageable.getPageNumber() - 1; // Client 편의: 첫 페이지 = 1
        Pageable adjustedPageable = PageRequest.of(Math.max(page, 0), pageable.getPageSize(), pageable.getSort());

        return ResponseEntity.ok().body(new ResponseDTO<>().data(boardService.findAll(boardListSearchInDTO, adjustedPageable)));
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<?> detail(@PathVariable Long boardId,
                                    @AuthenticationPrincipal MyUserDetails myUserDetails) {
        log.debug("게시글 상세 - GET, Controller");

        boardService.viewsCount(boardId);

        return ResponseEntity.ok().body(new ResponseDTO<>().data(boardService.findDetailById(boardId, myUserDetails)));
    }

    @PostMapping("/auth/board")
    public ResponseEntity<?> save(@RequestBody @Valid BoardSave_InDTO boardSaveInDTO,
                                  @AuthenticationPrincipal MyUserDetails myUserDetails) {
        log.debug("게시글 등록 - POST, Controller");

        return ResponseEntity.ok().body(new ResponseDTO<>().data(boardService.save(boardSaveInDTO, myUserDetails.getUser().getId())));
    }

    @GetMapping("/auth/board/{boardId}")
    public ResponseEntity<?> updateForm(@PathVariable Long boardId,
                                        @AuthenticationPrincipal MyUserDetails myUserDetails) {
        log.debug("게시글 수정 페이지 - GET, Controller");

        return ResponseEntity.ok().body(new ResponseDTO<>().data(boardService.updateForm(boardId, myUserDetails.getUser().getId())));
    }

    @PutMapping("/auth/board")
    public ResponseEntity<?> update(@RequestBody @Valid BoardUpdate_InDTO boardUpdateInDTO,
                                    @AuthenticationPrincipal MyUserDetails myUserDetails) {
        log.debug("게시글 수정 - PUT, Controller");
        boardService.update(boardUpdateInDTO, myUserDetails.getUser().getId());

        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    @DeleteMapping("/auth/board/{boardId}")
    public ResponseEntity<?> delete(@PathVariable Long boardId,
                                    @AuthenticationPrincipal MyUserDetails myUserDetails) {
        log.debug("게시글 삭제 - DELETE, Controller");
        boardService.delete(boardId, myUserDetails.getUser().getId());

        return ResponseEntity.ok().body(new ResponseDTO<>());
    }
}
