package hello.board.domain.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
// 마이바티스 이름기반 파라미터 매칭을 위한 기본생성자
@NoArgsConstructor
public class Image {

    private Long imageId;
    // boardId
    private Long boardId;
    private Long memberId;
    // 업로드 사진명
    private String uploadImageName;
    // 서버저장 사진명
    private String storeImageName;
    // 경로
    private String imageAddress;
    // ck Editor 요청경로
    private String imageRequestUrl;

    // 업로드 진행중인 Image 는 boardId = null 과 구분하기위해 0L 로 설정.
    //  게시글 등록하면 boardId = 0 인 Image 를 찾아 수정
    public Image(String uploadImageName, String storeImageName, String imageAddress, String imageRequestUrl, Long memberId) {
        this.uploadImageName = uploadImageName;
        this.storeImageName = storeImageName;
        this.imageAddress = imageAddress;
        this.imageRequestUrl = imageRequestUrl;
        this.memberId = memberId;
        this.boardId = 0L;
    }

    public Image(Long boardId, String uploadImageName, String storeImageName, String imageAddress) {
        this.boardId = boardId;
        this.uploadImageName = uploadImageName;
        this.storeImageName = storeImageName;
        this.imageAddress = imageAddress;
    }

}
