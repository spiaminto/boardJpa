package hello.board.form;

import hello.board.domain.enums.Category;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentSaveForm {

    private Long boardId;       // board.id 를 참조하는 fk
    private Long memberId;      // 작성자의 Member.id
    private Long targetId;      // 대댓글의 target 의 id (없으면 0)

    private String  writer;     // 글쓴이의 member.username
    private String target;      // 대댓글의 target 의 member.username (없으면 null)

    @NotBlank
    private String content;

    private Long groupId;       // 모댓글 commentId (자신이 모댓글이면 자신의 commentId)
    private Integer groupOrder;     // 대댓글일때, 대댓글 순서
    private Integer groupDepth;     // 모댓글 0, 대댓글 1

    private Category category;      // 댓글 카테고리 조회를 위한 카테고리
}
