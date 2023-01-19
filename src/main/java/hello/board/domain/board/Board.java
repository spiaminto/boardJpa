package hello.board.domain.board;

import lombok.Data;
import lombok.NoArgsConstructor;

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

    // save
    public Board(String title, String writer, String content) {
        this.title = title;
        this.writer = writer;
        this.content = content;
    }

    // update
    public Board(String title, String writer, String content, LocalDateTime updateDate) {
        this.title = title;
        this.writer = writer;
        this.content = content;
        this.updateDate = updateDate;
    }

}
