package hello.board.domain.board;

import hello.board.domain.enums.Category;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @ToString @EqualsAndHashCode
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Board {

    private Long id;
    private String title;
    private String content;
    private String writer;              // username
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    private long viewCount;             // Default = 0
    private Integer commentCnt;         // Default = 0

    private Long memberId;              // username 갱신 시 필요
    private Category category;

    public boolean isToday(LocalDateTime input) {
        return input.toLocalDate().equals(LocalDate.now());
    }

}
