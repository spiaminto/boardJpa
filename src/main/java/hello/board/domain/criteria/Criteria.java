package hello.board.domain.criteria;

import hello.board.domain.enums.Category;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 게시판 조회 쿼리에 전달될 파라미터를 담은 클래스.
 */

@Slf4j
@Data
@AllArgsConstructor
public class Criteria {

    private Integer currentPage;    // 현재 페이지 번호
    private int contentPerPage;     // 페이지 당 글 갯수

    private String categoryCode;        // 카테고리 url 용
    private Category category;          // 내부에서 사용하는 카테고리

    private String option;      // 검색 조건
    private String keyword;     // 검색 키워드

    // 요청 파라미터가 없을때 (최초접속, /boards)
    public Criteria() {
        this.currentPage = 1;
        this.contentPerPage = 12;
        this.category = Category.ALL;
        this.categoryCode = Category.ALL.getCode();
    }

    // test 사용
    public Criteria(int currentPage, int contentPerPage, String option, String keyword) {
        this.currentPage = currentPage;
        this.contentPerPage = contentPerPage;
        this.option = option;
        this.keyword = keyword;
    }

    // LIMIT 문의 시작 파라미터
    public int getStartRowNum() {
        return (currentPage - 1) * contentPerPage;
    }

    public void setCategoryCode(String categoryCode) {
        // POST /board/id/edit 요청에서, category 가 Criteria, Board 중복이라 "all,all" 로 바인딩 되는것 수정
        if (categoryCode != null) {
            int doSubstring = categoryCode.indexOf(',');
            if (doSubstring > 0) {
                categoryCode = categoryCode.substring(0, doSubstring);
            }
            this.categoryCode = categoryCode;
            this.category = Category.of(categoryCode);
        }
    }

}
