package hello.board.domain.board;

import hello.board.domain.enums.Category;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

// #패키지 설계 로그인처리 1-2

@Getter @ToString
@Slf4j
// Mybatis 에 필요한 기본생성자는 리플렉션을 통해 사용되므로 접근제어에 영향X
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// Builder 를위해, 일반 객체 생성 제한
@AllArgsConstructor(access = AccessLevel.PROTECTED)
// 의미있는 생성자로 구분했어야했나?
@Builder
public class Board {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    private long viewCount;

    private Long memberId;
    private Category category;

    public boolean isToday(LocalDateTime input) {
        return input.toLocalDate().equals(LocalDate.now());
    }

}
