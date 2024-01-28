package com.example.demo.module.board.out_dto;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoDTO {

    private Integer currentPage;
    private Integer startPage;
    private Integer endPage;

    private Boolean isFirst;
    private Boolean isLast;

    public PageInfoDTO(Page<BoardListDTO> boardList) {
        this.currentPage = boardList.getPageable().getPageNumber() + 1;
        this.isFirst = boardList.isFirst();
        this.isLast = boardList.isLast();

        Integer ButtonCount = 5; // 1-5, 6-10 구분
        Integer tmp = boardList.getPageable().getPageNumber() / ButtonCount;
        this.startPage = 1 + (tmp * ButtonCount);
        this.endPage = 5 + (tmp * ButtonCount);

        int totalPage = boardList.getTotalPages();
        if (totalPage < endPage) {
            this.endPage = totalPage;
        }
    }

}
