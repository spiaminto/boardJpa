package hello.board.domain.paging;

import hello.board.domain.criteria.Criteria;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 페이징 계산 및 사용자에게 보여줄 데이터를 만드는 PageMaker 클래스.
 * 페이징 버튼 & 보여줄 페이지 번호계산 등을 담당
 *
 * Criteria 클래스를 통해 설정된 파라미터를 쿼리에 넣어 반환된 결과인 boardList 를 받아 사용한다.
 * Controller (Service) -> Criteria -> Repository -> PageMaker
 */

@Component
@Slf4j
@Data
@NoArgsConstructor
public class PageMaker{

    // 현재페이지, 페이지별 글갯수
    private Criteria criteria;

    private String categoryCode;

    // 현재페이지 (프론트용)
    private int currentPage;

    // 전체글 / 페이지 갯수
    private int totalCount;

    // 시작 페이지 번호 / 끝 페이지 번호
    private int startPageNum;
    private int endPageNum;

    // 보여줄 페이지 번호 개수
    private int displayPageNum = 5;

    // next/prev 버튼 유무
    private boolean isNextBt;
    private boolean isPrevBt;

    // 글 데이터
    // 글 데이터가 꼭 같이있어야 될지는 의문. 분리하는게 나은지도 잘모르겠긴함.
    // => 글 데이터가 같이 있으면 나중에 꺼낼때 문제 생기므로 삭제
//    private List<Board> boardList;

    public PageMaker(Criteria criteria, Integer totalCount) {
        this.criteria = criteria;
        this.totalCount = totalCount;
        this.currentPage = criteria.getCurrentPage();
        this.categoryCode = criteria.getCategoryCode();

        calcPage();
    }

    public void calcPage() {

        // 현패1 엔패5 / 현패3 엔패5 / 현패17 엔페20
        // (double) displayPageNum 으로 나누므로 소숫점 까지 나옴 -> 을 올림 후 계산
        endPageNum = (int) Math.ceil(criteria.getCurrentPage() / (double) displayPageNum) * displayPageNum;
//      endPageNum = ((criteria.getCurrentPage() - 1) / displayPageNum) + 1;

        // 엔페10 스페6 -> 6 7 8 9 10
        startPageNum = endPageNum - displayPageNum + 1;

        // 토탈71 퍼페10 라페8 / 토탈70 퍼페10 라페7
        int lastPage =(int) Math.ceil(totalCount / (double)criteria.getContentPerPage());
//      int lastPage = (totalBoard - 1) / criteria.getBoardPerPage() + 1;

        // 끝 페이지 번호가 마지막 페이지보다 높으면 마지막 페이지를 끝페이지 번호로.
        // 이 작업을 startPageNum 계산 전에 하면, startPageNum 계산에 문제 발생.
        if (endPageNum > lastPage) {
            endPageNum = lastPage;
        }

        isPrevBt = startPageNum == 1 ? false : true;

        isNextBt = endPageNum == lastPage ? false : true;
//      isNextBt = endPageNum * criteria.getBoardPerPage() < totalBoard ? true : false;

    }


}
