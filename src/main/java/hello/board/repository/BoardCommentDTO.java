package hello.board.repository;

import hello.board.domain.enums.Category;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardCommentDTO {

    // Board

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

    // ==========================================================================
    // Comment

    private Long commentId;
    private Long boardId;       // board.id 를 참조하는 fk
    //  private Long memberId;      // board.memberId 를 참조, 글쓴이의 id -> Board 와 중복
    private Long targetId;      // 대댓글의 target 의 id (없으면 0)

    private String commentWriter;     // 글쓴이의 member.username
    private String target;      // 대댓글의 target 의 member.username (없으면 null)

    private String commentContent;

    private LocalDateTime commentRegDate;
    private LocalDateTime commentUpdateDate;

    // 계층 답글
    private Long groupId;       // 모댓글은 자기자신, 대댓글은 모댓글, 대-대댓글은 모댓글의 commentId
    private Integer groupOrder;     // 대댓글일때, 대댓글 순서
    private Integer groupDepth;     // 모댓글 0, 대댓글 1

    private Category commentCategory;      // 댓글 카테고리 조회를 위한 카테고리

    // ==========================================================================

}
