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
    // index 0 == 0 , empty
    
    // DB 저장할때는 안쓰는데 testDataInit 떄문에 일단 놔둠 나중에 지워 이거랑 밑에 그냥 save 랑
    private String imageUrl;
    
    // ArgumentResolver? 가 자동으로 바인딩 할때는 빈곳에 null 을 넣어주나? 
    // @Data 로 생성된 생성자에 null 넣을수도 있겟지만 일단 따로 생성자 작성함.

    // save
    public Board(String title, String writer, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.imageUrl = imageUrl;
    }

    // imageId -> boardId 사용으로 로직변경

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
