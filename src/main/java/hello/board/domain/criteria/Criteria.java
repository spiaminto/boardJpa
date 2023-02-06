package hello.board.domain.criteria;

import hello.board.domain.enums.Category;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 게시판 조회 쿼리에 전달될 파라미터를 담은 클래스.
 * 왜 Criteria 라는 이름을 사용하는지는 불명.
 *
 * 검색시 사용하는 파라미터도 넣는것으로 보임 -> SQL 전달 파라미터를 모은 클래스로 동작하는듯?
 *
 */
@Component
@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class Criteria {

    // 현재 페이지 번호
    private Integer currentPage;
    // 페이지 당 글 갯수
    private int boardPerPage;

    private String categoryCode;
    private Category category;

    // 검색 열 조건
    private String option;
    // 검색 키워드
    private String keyword;

    // 요청 파라미터가 없을때 (최초접속)
    public Criteria() {
        this.currentPage = 1;
        this.boardPerPage = 12;
        this.category = Category.ALL;
        this.categoryCode = Category.ALL.getCode();
    }

    // test 사용
    public Criteria(int currentPage, int boardPerPage, String option, String keyword) {
        this.currentPage = currentPage;
        this.boardPerPage = boardPerPage;
        this.option = option;
        this.keyword = keyword;
    }

    // Limit 문의 시작행은 0(행) 부터 시작
    // 현패1 -> 시작행 0(1~10) / 현패2 -> 시작행 10(11~20) / 현패3 -> 시작행 20(21~30)
    public int getStartRowNum() {
        return (currentPage - 1) * boardPerPage;
    }

    // 의미가 있나?
    public void setCurrentPage(int currentPage) {
        if (currentPage <= 0) {
            this.currentPage = 1;
        } else {
            this.currentPage = currentPage;
        }
    }

    public void setCategoryCode(String categoryCode) {
        if (categoryCode != null) {
            this.categoryCode = categoryCode;
            this.category = Category.of(categoryCode);
        }
    }

}
