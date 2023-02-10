package hello.board.domain.comment;

import hello.board.domain.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    private Long commentId;

    private Long boardId;       // board 를 참조하는 fk

    private Long memberId;      // member 를 참조하는 fk, 글쓴이의 id

    private Long targetId;      // 대댓글의 target 의 id (없으면 0)

    private String  writer;     // 글쓴이의 member.username

    private String target;      // 대댓글의 target 의 member.username (없으면 null)

    private String content;

    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    // 계층 답글
    private Long groupId;       // 모댓글 commentId (자신이 모댓글이면 자신의 commentId)
    private Integer groupOrder;     // 대댓글일때, 대댓글 순서
    private Integer groupDepth;     // 모댓글 0, 대댓글 1


    private Category category;      // 댓글 카테고리 조회를 위한 카테고리

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

    // test 에서만 쓰임
    public Comment(Long boardId, Long memberId, String writer, String content, Long groupId, Integer groupOrder, Integer groupDepth, Category category) {
        this.boardId = boardId;
        this.memberId = memberId;
        this.writer = writer;
        this.content = content;
        this.groupId = groupId;
        this.groupOrder = groupOrder;
        this.groupDepth = groupDepth;
        this.category = category;
        // 밀리세컨드 절삭
        regDate = LocalDateTime.now().withNano(0);
    }

    // 이 코드 문제인게 내가 나한테 쓰는 답글은 target 이 나 임.
    // findPaged...WithMemberId 할때 target 이 자신이면 null 로 (모댓글)
//    public void setTarget(String param) {
//        if (param != null) {
//            this.target = param.equals(this.writer) ? null : param;
//        }
//    }

    // 얜 왜안돼지?
    public void setComment_id(Long id) {
        setCommentId(id);
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
