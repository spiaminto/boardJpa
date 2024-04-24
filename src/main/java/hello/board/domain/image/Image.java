package hello.board.domain.image;

import lombok.*;

import javax.persistence.*;

@Getter @ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Image {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;
    private Long boardId;       // DEFAULT = 0
    private Long memberId;

    private String uploadImageName;     // 업로드 사진명
    private String storeImageName;      // 서버저장 사진명 (uuid.ext)
    private String imageAddress;        // 서버저장 경로 ( /path/uuid.ext )

    @Transient
    private String imageRequestUrl;     // ck Editor Http 요청경로 (DB 에 저장되지 않음)

    // 아마존 S3 사용할 경우 imageAddress = imageRequestUrl
}
