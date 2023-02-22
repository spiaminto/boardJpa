package hello.board.domain.board;

import hello.board.domain.enums.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// #패키지 설계 로그인처리 1-2

@Data
@NoArgsConstructor
public class Board {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime regedate;
    private LocalDateTime updateDate;

    private long viewCount;

    private Long memberId;
    private Category category;

    // save(write)
    public Board(String title, String writer, Long memberId, String content, Category category, LocalDateTime now) {
        this.title = title;
        this.writer = writer;
        this.memberId = memberId;
        this.content = content;
        this.category = category;
        this.regedate = now;
        this.updateDate = now;
    }

    // update(edit)
    public Board(String title, String writer, String content, LocalDateTime updateDate, Category category) {
        this.title = title;
        this.writer = writer;
        this.content = content;
        this.updateDate = updateDate;
        this.category = category;
    }

    public boolean isToday(LocalDateTime input) {
        return input.toLocalDate().equals(LocalDate.now());
    }



}
