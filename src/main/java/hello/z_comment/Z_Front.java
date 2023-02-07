package hello.z_comment;

public class Z_Front {

    /*
    RedirectAttribute
    리다이렉트 할때 넘긴다. model 은 redirect 하면 사라짐. session 에 넣기엔 부담스러움 -> rttr 사용

    Spring 에서는 model.addAttribute() 로 담고 redirect:/url.... 하면 자동으로 redirect 요청뒤에 파라미터로
        model 에 추가된 데이터를 (primitive 만) 쿼리 파라미터로 달아 보낸다. (재요청이라 모델 전송X)
        이 기능은 SpringBoot 에서 기본 비활성화 상태이며 Configurer 로 따로 켜줘야 된다.

        .addAttribute() -> url 뒤에 파라미터로 붙여서 넘겨줌. 위의 spring 기본기능과 거의 동일한듯? primitive 만가능
        
        .addFlashAttribute() -> session 객체에 flashAttribute 로 추가해줌. 새로고침하면 지워짐. Object 가능
            이걸로 추가된 객체들을 타임리프에서 th:inline 안에 CDATA 로 사용가능한데 이 이유는 잘 모르겠음.
            특정 소속 안붙여주고 그냥 $msg 만 써도 되는데 Model 에 자동으로 추가되는건지?

     alert
     javascript 로 alert 하는거 결국 redirect:/alert 로 구현.
     /alert 용 controller 하나 만들고, GET 으로 alert.html 요청 후 해당 타임리프 자바스크립트 인라인으로
     alert 구현. RedirectDTO model 에 추가시켜서 메시지랑 url 받음. alert 후 replace 로 redirect
     */

}
