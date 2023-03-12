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
    
    // 서버 파일 경로
    private String imageAddress;
    
    // (ck Editor) http 요청경로, DB 에 저장되지 않음.
    private String imageRequestUrl;
    
    // 로컬 저장 사용할 경우 imageAddress = 로컬주소, imageRequestUrl = (변환할) http 요청주소
    //      로컬파일을 그대로 요청할 경우 크롬에서 에러남. 보안관련인듯?
    
    // 아마존 S3 사용할 경우 imageAddress = imageRequestUrl = 아마존 s3 버켓 주소

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
