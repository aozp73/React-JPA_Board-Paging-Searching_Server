package com.example.demo.module.board.out_dto;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardList_OutDTO {

    private Long boardId;
    private String title;
    private Integer views;
    private Integer commentCount;

    private User user;
    private PageInfo pageInfo;

    // user
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private Long userId;
        private String username;
        private String createdAtFormat;
    }

    // pageInfo
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageInfo {
        private Integer startPage;
        private Integer endPage;

        public void calculatePageInfo(Page<BoardList_OutDTO> boardList) {
            Integer ButtonCount = 5; // 1-5, 6-10 구분

            Integer tmp = boardList.getPageable().getPageNumber() / ButtonCount;
            this.startPage = 1 + (tmp * ButtonCount);
            this.endPage = 5 + (tmp * ButtonCount);

            int totalPage = boardList.getTotalPages();
            if (totalPage < this.endPage) {
                this.endPage = totalPage;
            }
        }
    }
}
