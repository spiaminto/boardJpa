# board

테스트용  
loginId: testid  
password: test   
[프로젝트 바로가기](http://springboard-env.eba-x3sau5v7.ap-northeast-1.elasticbeanstalk.com/board/list/all)  

<br>

공부하면서 배운것, 알게된 것들을 확인하고 사용해보기 위해 만든 프로젝트 입니다.  


프론트는 thymeleaf 와 jquery, 백은 springboot 라이브러리를 이용하여 작성했습니다.
RDBMS 는 mySql 을 사용하였고, mybatis 를 이용해 sql 을 처리하였습니다.  
  
<br>

## 이 프로젝트는 다음의 기능을 구현하고 있습니다.  

<br>

* 글 작성,수정,삭제
  * 글 쓰기에는 위지윅에디터(ckeditor5) 적용되어 있습니다.
  * 글 목록은 기본적인 페이징, 검색, 카테고리 처리가 적용되어 있습니다.  
  
* 이미지 업로드
  * ckeditor5 의 이미지 업로드 기능을 이용합니다.
  * Amazon S3 서비스를 이용하여 클라우드 스토리지에 업로드합니다.  
  
* 댓글, 대댓글 작성,수정,삭제
  * ajax 를 이용해 댓글, 대댓글 기능을 구현했습니다.  
  
* 회원 가입,수정,삭제
  * 로그인은 spring security 라이브러리를 이용합니다.
  * oauth2-client 라이브러리를 이용한 소셜로그인/가입 기능을 포함 합니다.  
  
* 유효성 검사
  * 글, 댓글, 회원정보 에 간단한 validation 이 적용되어있습니다.  
    
<br>
  
## 또한, 이 프로젝트는 다음의 서비스를 이용합니다.  

<br>

* Amazon Elastic Beanstalk
  * 프로젝트를 배포, 관리하기 위해 사용했습니다.  
  
* Amazon S3
  * 이미지 파일을 클라우드 스토리지에 업로드 하기 위해 사용했습니다.  
  
* Amazon RDS
  * 배포된 프로젝트에서 온라인 DB 를 이용하기 위해 사용했습니다.

  

