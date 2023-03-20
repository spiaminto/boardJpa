package hello.board.domain.image;

import lombok.*;

@Getter @ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Image {

    private Long imageId;
    private Long boardId;       // DEFAULT = 0
    private Long memberId;

    private String uploadImageName;     // 업로드 사진명
    private String storeImageName;      // 서버저장 사진명 (uuid.ext)
    private String imageAddress;        // 서버저장 경로 ( /path/uuid.ext )
    private String imageRequestUrl;     // ck Editor Http 요청경로 (DB 에 저장되지 않음)
    
    // 로컬 저장 사용할 경우 imageAddress = 로컬주소, imageRequestUrl = (변환할) http 요청주소
    //      로컬파일을 그대로 요청할 경우 크롬에서 에러남. 보안관련인듯?
    
    // 아마존 S3 사용할 경우 imageAddress = imageRequestUrl = 아마존 s3 버켓 주소

    //  게시글 등록하면 boardId = 0 인 Image 를 찾아 수정


}
