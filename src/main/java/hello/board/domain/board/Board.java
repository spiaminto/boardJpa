package hello.board.domain.board;

import hello.board.domain.comment.Comment;
import hello.board.domain.enums.Category;
import hello.board.domain.image.Image;
import hello.board.domain.member.Member;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.persistence.GenerationType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;
import static org.springframework.util.StringUtils.hasText;

@Getter @ToString @EqualsAndHashCode(of = {"writer", "regDate"})
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;
    private String title;
    private String content;
    private String writer;              // username
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    private long viewCount;             // Default = 0
    private long commentCnt;         // Default = 0

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // username 갱신 시 필요

//    @Transient
//    private Long memberId;

    @Enumerated(EnumType.STRING)
    private Category category;

    @BatchSize(size = 50) // 한번에 로딩할 최대 댓글갯수 (fetchJoin 페이징)
    // board + comment 를 읽기위해 양방향 읽기
    @OneToMany(mappedBy = "board")
    private List<Comment> commentList = new ArrayList<>();

    public void updateBoard(Board updateParam) {
        this.title = hasText(updateParam.getTitle()) ? updateParam.getTitle() : title;
        this.writer = hasText(updateParam.getWriter()) ? updateParam.getWriter() : writer;
        this.content = hasText(updateParam.getContent()) ? updateParam.getContent() : content;
        this.category = updateParam.getCategory() != null ? updateParam.getCategory() : category;
        this.updateDate = updateParam.getUpdateDate() != null ? updateParam.getUpdateDate() : updateDate;
    }

    public boolean isToday(LocalDateTime input) {
        return input.toLocalDate().equals(LocalDate.now());
    }


}
