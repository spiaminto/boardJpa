package hello.board.domain.criteria;

import hello.board.domain.enums.Category;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 게시판 조회 쿼리에 전달될 파라미터를 담은 클래스.
 *  SQL 전달 파라미터를 모은 클래스명으로 Criteria~ 가 쓰이는 걸로 보임.
 */

// Criteria 객체는 스프링에서 관리하지 않는다.
//@Component
@Slf4j
@Data
@AllArgsConstructor
public class Criteria {

    private Integer currentPage;     // 현재 페이지 번호
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

    // Limit 문의 시작행은 0(행) 부터 시작
    // 현패1 -> 시작행 0(1~10) / 현패2 -> 시작행 10(11~20) / 현패3 -> 시작행 20(21~30)
    public int getStartRowNum() {
        return (currentPage - 1) * contentPerPage;
    }

    public void setCategoryCode(String categoryCode) {
        if (categoryCode != null) {

            // /board/id/edit POST 요청에서, category 필드 가 criteria 와 board 에 중복되서 그런지 스프링 바인딩 할때
            // all,all 이런식으로 바인딩 되서 오류나길래 일단 이렇게 수정함.
//            log.info("setCategoryCode(), categoryCode = {}", categoryCode);
            int doSubstring = categoryCode.indexOf(',');
            if (doSubstring > 0) {
                categoryCode = categoryCode.substring(0, doSubstring);
            }

            this.categoryCode = categoryCode;
            this.category = Category.of(categoryCode);
        }
    }

}
