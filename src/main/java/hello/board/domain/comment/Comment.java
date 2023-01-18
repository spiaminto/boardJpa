package hello.board.domain.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    private Long commentId;

    private Long boardId;

    private String  writer;

    private String content;

    private LocalDateTime regDate;

    // 계층 답글
    private Long groupId;
    private Integer groupOrder;
    private Integer groupDepth;

    // 답글 타겟
    private String target;

    // test 에서만 쓰임
    public Comment(Long boardId, String writer, String content) {
        this.boardId = boardId;
        this.writer = writer;
        this.content = content;
        // 밀리세컨드 절삭
        regDate = LocalDateTime.now().withNano(0);
    }

    // test 에서만 쓰임
    public Comment(Long boardId, String writer, String content, Long groupId, Integer groupOrder, Integer groupDepth, String target) {
        this.boardId = boardId;
        this.writer = writer;
        this.content = content;
        this.groupId = groupId;
        this.groupOrder = groupOrder;
        this.groupDepth = groupDepth;
        this.target = target;
        // 밀리세컨드 절삭
        regDate = LocalDateTime.now().withNano(0);
    }

    // for updateParam
    /*
    public Comment(Long commentId, String content) {
        this.commentId = commentId;
        this.content = content;
        regDate = LocalDateTime.now().withNano(0);
    }
     */

}
