# board

테스트용 ID: sample / PW: sample  
[프로젝트 바로가기](http://springboard-env.eba-x3sau5v7.ap-northeast-1.elasticbeanstalk.com/boards)  

<br>

공부하면서 배운것, 알게된 것들을 확인하고 사용해보기 위해 만든 프로젝트 입니다. 

초기에는 학습 내용을 복습하는 것을 목표로 진행하였고, 이후 상용 커뮤니티 게시판에 필요한 기능은 어떤것들이 있을까 고민하며
기능들을 하나하나 붙여나갔습니다. 

프론트는 thymeleaf 와 jquery, 백은 spring(boot) 를 이용하여 작성했습니다.
RDBMS 는 mySql 을 사용하였고, mybatis 를 이용해 sql 을 처리하였습니다.  

  
<br>

## 기능 요약

<br>

* 글 작성,수정,삭제
 <img width="400" alt="APPHOME" src="https://user-images.githubusercontent.com/122969954/230583227-345c7165-5e56-453a-b832-200eb4f07bf0.png"><img width="400" alt="APPWRITE" src="https://user-images.githubusercontent.com/122969954/230583252-4d7093e5-9dd6-426e-86b8-c0b1fd91c868.png">
  * 글 목록은 기본적인 페이징, 검색, 카테고리 처리가 적용되어 있습니다.  
  * 글 쓰기에는 위지윅에디터(ckeditor5) 적용되어 있습니다.
  
* 이미지 업로드
  * ckeditor5 의 이미지 업로드 기능을 이용합니다.
  * Amazon S3 서비스를 이용하여 클라우드 스토리지에 업로드합니다.  
  
* 댓글, 대댓글 작성,수정,삭제
<img width="400" alt="APPCOMMENT" src="https://user-images.githubusercontent.com/122969954/230583219-462ba81b-6ccb-48db-9b00-2084885208fd.png">
  * ajax 를 이용해 댓글, 대댓글 기능을 구현했습니다.  
  
* 회원 가입,수정,탈퇴
<img width="400" alt="APPLOGIN" src="https://user-images.githubusercontent.com/122969954/230583236-749c8fa3-ab50-409e-a802-b0262cff2f7a.png">
  * 로그인은 spring security 라이브러리를 이용합니다.
  * oauth2-client 라이브러리를 이용한 소셜로그인/가입 기능을 포함 합니다. 가입은 별도의 form 을 통해 가입합니다.  
  * 현재 naver 를 통한 소셜가입은 불가능 합니다. (네이버에서 허용안됨)
  
* 유효성 검사
<img width="400" alt="APPVAL1" src="https://user-images.githubusercontent.com/122969954/230583240-e5706f93-273c-43a3-bc7c-7bbe184cf910.png"><img width="400" alt="APPVAL2" src="https://user-images.githubusercontent.com/122969954/230583241-c68edb70-9672-4ab7-9388-c5cfe3fa89bb.png">
  * 글, 댓글, 회원정보 에 간단한 validation 이 적용되어있습니다.
  
* 로깅
  * @Aspect 로 로그를 찍고, logback 라이브러리를 이용하여 로그 출력을 커스텀 하고, 파일로 작성합니다.
  * 파일로 작성된 로그를 Amazon cloudwatch 로 실시간 스트리밍 합니다.
    
<br>
  
## 이용한 서비스

<br>

* Amazon Elastic Beanstalk
<img width="400" alt="EBHOME" src="https://user-images.githubusercontent.com/122969954/230583282-53fe5ccd-bd6a-4c2b-aba6-b136b2e596a3.png">
  * 프로젝트를 배포, 관리하기 위해 사용했습니다.  
  * Amazon RDS 의 인스턴스에 연결하여 해당 DB 를 사용합니다.
  * Amazon CloudWatch 에 커스텀 된 로그를 스트리밍 하게 설정했습니다.
  * taillog, bundlelog 요청 시 기본 로그에 애플리케이션의 logback 에서 작성한 로그파일도 같이 보여주도록 설정하였습니다.
  
* Amazon S3
<img width="400" alt="S3HOME" src="https://user-images.githubusercontent.com/122969954/230583309-0973e9e3-e702-470e-a064-b48fa9a42453.png">
  * 이미지 파일을 클라우드 스토리지에 업로드 하기 위해 사용했습니다.  
  * 로컬 테스트는 /upload_image_test , 배포된 애플리케이션 에서는 /upload_image 로 이미지 파일을 업로드 합니다.
  
* Amazon RDS
<img width="400" alt="RDSHOME" src="https://user-images.githubusercontent.com/122969954/230583295-843f4dcf-4ad8-4150-bd3c-2f328f3debad.png">
  * 로컬 DB 와 배포된 애플리케이션의 DB 를 분리하고, 온라인에서 DB 를 이용하기 위해 사용했습니다. MySql WorkBench 를 통해 연결하여 사용했습니다.
  
* Amazon CloudWatch
<img width="400" alt="CWLOG" src="https://user-images.githubusercontent.com/122969954/230583259-9e501d97-a926-4b83-bb6a-5f94cd4345f9.png">
  * 배포된 애플리케이션의 로그를 쉽게, 실시간으로 확인하기 위해 사용했습니다.

  

