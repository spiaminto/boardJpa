# board

__테스트용 ID: sample / PW: sample__
### 링크 : [프로젝트 바로가기](http://springboard-env.eba-x3sau5v7.ap-northeast-1.elasticbeanstalk.com/boards)  


공부하면서 배운것, 알게된  것들을 확인하고 사용해보기 위해 만든 프로젝트 입니다. 

초기에는 학습 내용을 복습하는 것을 목표로 진행하였고, 이후 상용 커뮤니티 게시판에 필요한 기능은 어떤것들이 있을까 고민하며
기능들을 하나하나 붙여나갔습니다. 

프론트는 thymeleaf 와 jquery, 백은 spring(boot) 를 이용하여 작성했습니다.  
RDBMS 는 mySql 을 사용하였고, mybatis 를 이용해 sql 을 처리하였습니다.  

학습하면서 작성한 코드라, 주석이 많이 달려있습니다. 해당 주석은 다른곳에 정리한 뒤, 지워나가도록 하겠습니다. 불편을 드려 죄송합니다.  

아래의 이미지들은 클릭하면 커집니다.
<br>

## ERD
<img width="75%" alt="스크린샷 2023-04-10 184713" src="https://user-images.githubusercontent.com/122969954/230877990-0eb65d6a-a88f-45ee-85ba-5780c0d0c0e7.png">


## 기능 

### ㅇ 게시글 관리

<img width="47%" alt="APPWRITE" src="https://user-images.githubusercontent.com/122969954/230583252-4d7093e5-9dd6-426e-86b8-c0b1fd91c868.png"> <img width="47%" alt="APPHOME" src="https://user-images.githubusercontent.com/122969954/230583227-345c7165-5e56-453a-b832-200eb4f07bf0.png">

글 목록은 전체 글 목록, 내가 쓴 글 목록 을 카테고리 별로 확인 할 수 있으며, 페이징과 검색 기능도 구현하였습니다.  
글 쓰기에는 위지윅에디터(ckeditor5) 적용되어 있습니다.

  
### ㅇ 이미지 업로드
<img width="65%" alt="APPIMAGE" src="https://user-images.githubusercontent.com/122969954/230591439-e8c95f69-3304-4066-9c41-ea92ddc72cb0.png">

 ckeditor5 의 이미지 업로드 기능을 이용합니다.  
 업로드 요청된 이미지 파일을 Amazon S3 클라우드 스토리지에 업로드하고, 업로드한 이미지의 정보를 DB 에 저장합니다.  
 이미지 파일의 용량은 파일당 3MB 로 제한했으며, 5장을 초과하여 업로드 할 수 없도록 하였습니다.

### ㅇ 댓글 관리
<img width="47%" alt="APPCOMMENT" src="https://user-images.githubusercontent.com/122969954/230583219-462ba81b-6ccb-48db-9b00-2084885208fd.png"><img width="47%" alt="APPMYCOMMENT" src="https://user-images.githubusercontent.com/122969954/230873920-2fc7b354-955a-48a2-a990-6e90bb4753a7.png">

 ajax 를 이용해 댓글, 대댓글(댓글의 댓글) 기능을 구현했습니다.  
 대댓글은 대댓글의 대상이 되는 댓글의 닉네임이 표시되도록 하였습니다.  
 내가 쓴 댓글 목록을 확인 할 수 있으며, 해당 댓글의 글로 바로 이동하고, 댓글을 누를 경우 페이지 이동후 해당 댓글까지 자동으로 스크롤 합니다.

### ㅇ 회원 관리
<img width="47%" alt="APPLOGIN" src="https://user-images.githubusercontent.com/122969954/230583236-749c8fa3-ab50-409e-a802-b0262cff2f7a.png"><img width="47%" alt="APPOAUTHSIGNIN" src="https://user-images.githubusercontent.com/122969954/230873923-aad91c47-bed3-4560-90d8-233842dbb46c.png">

 로그인은 spring security 라이브러리를 이용합니다.  
oauth2-client 라이브러리를 이용한 소셜로그인/가입 기능을 포함 합니다.  
소셜가입은, 각 서비스에서 사용정보 동의를 받으면, 해당 정보를 바탕으로 별도의 회원가입 폼으로 이동하여 가입을 진행합니다.  
네이버 로그인 서비스는 검수를 받지 않으면 개발자 계정 외의 가입이 불가능하여, 현재는 사용할 수 없습니다. (개발자 본인의 계정으로 테스트는 하였습니다.)  
다음 로그인 서비스는 검수를 받지 않으면 카카오 계정 제공을 필수로 설정할 수 없어, 일단 주의 메시지를 띄우도록 하였습니다.

### ㅇ 유효성 검사
<img width="47%" alt="APPVAL2" src="https://user-images.githubusercontent.com/122969954/230583241-c68edb70-9672-4ab7-9388-c5cfe3fa89bb.png"><img width="47%" alt="APPVAL1" src="https://user-images.githubusercontent.com/122969954/230583240-e5706f93-273c-43a3-bc7c-7bbe184cf910.png">

 beanvalidation, 자바스크립트 등을 이용하여 글, 댓글, 회원정보 에 간단한 validation 이 적용되어있습니다.

### ㅇ 로깅
<img width="65%" alt="스크린샷 2023-04-10 183227" src="https://user-images.githubusercontent.com/122969954/230875569-12e1815f-c496-4a45-b646-4afc37fa887c.png">

 @Aspect 등 으로 로그를 찍고, logback 라이브러리를 이용하여 로그 출력을 커스텀 하고, 파일로 작성합니다.  
 파일은 전체로그, 에러로그, 요청요약 로그 등으로 세분화 하여 작성 해 보았습니다.  
 파일로 작성된 로그를 Amazon cloudwatch 로 실시간 스트리밍 합니다. 아래의 cloudwatch 사용 부분을 참고해 주세요.
  
## 이용한 서비스

### ㅇ Amazon Elastic Beanstalk
<img width="65%" alt="EBHOME" src="https://user-images.githubusercontent.com/122969954/230583282-53fe5ccd-bd6a-4c2b-aba6-b136b2e596a3.png">

 프로젝트를 배포, 관리하기 위해 사용했습니다. Amazon RDS 의 인스턴스에 연결하여 해당 DB 를 사용합니다.   
Amazon CloudWatch 에 커스텀 된 로그를 스트리밍 하게 설정했습니다.  
taillog, bundlelog 요청 시 기본 로그에 애플리케이션의 logback 에서 작성한 로그파일도 같이 보여주도록 설정하였습니다.

### ㅇ Amazon S3
<img width="65%" alt="S3STOREAGE" src="https://user-images.githubusercontent.com/122969954/230873925-d63186ff-fefe-4176-87c1-7643423c8c79.png">

 이미지 파일을 클라우드 스토리지에 업로드 하기 위해 사용했습니다.  
 로컬 테스트는 /upload_image_test , 배포된 애플리케이션 에서는 /upload_image 로 이미지 파일을 업로드 합니다.  


### ㅇ Amazon RDS
<img width="65%" alt="RDSHOME" src="https://user-images.githubusercontent.com/122969954/230583295-843f4dcf-4ad8-4150-bd3c-2f328f3debad.png">

 로컬 DB 와 배포된 애플리케이션의 DB 를 분리하고, 온라인에서 DB 를 이용하기 위해 사용했습니다. MySql WorkBench 를 통해 연결하여 사용했습니다.


### ㅇ Amazon CloudWatch
<img width="65%" alt="CWLOG" src="https://user-images.githubusercontent.com/122969954/230583259-9e501d97-a926-4b83-bb6a-5f94cd4345f9.png">
 배포된 애플리케이션의 로그를 쉽게, 실시간으로 확인하기 위해 사용했습니다.
 
