package hello.board;

public class Z_Exception {
    // 예외처리 진행시
    // WebServerCustomizer 만들고 예외종류에 따라 errorPage 추가하고 예외처리용 컨트롤러인 ErrorPageController 만듦.
    //  new ErrorPage("/error") , 상태코드와 예외를 설정하지 않으면 기본 오류 페이지로 사용된다.
    //  서블릿 밖으로 예외가 발생하거나, response.sendError(...) 가 호출되면 모든 오류는 /error 를호출하게 된다.
    // ㄴ> BasicErrorController 의 @RequestMapping 이 {.../error} 로 되어있음
    //      따라서 에러상황시 다른 설정 없으면(WebServerCustomizer) BasicErrorController 가 호출됨.

    // SpringBoot 에서 제공하는 기본 오류 메커니즘
    // BasicErrorController 라는 스프링 컨트롤러를 자동으로 등록한다.
    // 이 컨트롤러는 ErrorPage 에서 등록한 /error 를 매핑해서 처리하는 컨트롤러다

    // BasicErrorController 의 로직은 이미 개발되어있기 때문에, 개발자는 오류 페이지 화면만 BasicEController 가
    //  제공하는 룰과 우선순위에 따라 등록하면 된다.
    //  우선순위는
    //      뷰템플릿 (resources/templates/error/...)
    //      정적리소스 (resources/static(public)/error/...)
    //      뷰 이름 (resources/templates/error.html)
    //      순 이며, 템플릿이나 리소스의 이름은 HTTP 상태코드로 지정한다.
    //          ex) 404.html, 500.html, 4xx.html (400번대 오류 전체) -> 이때 더 구체적인것이 우선순위가 높다



    // API 예외처리 진행시
    // BasicErrorController 의 에러처리 메서드 (/error 맵핑) 는 다양하게 구현되어 있음.
    // 요청의 Accept Header 가 text/html 인지, application/json 인지 등등을 (produce = MediaType...) 으로 걸러서
    // 해당 요청에 맞는 반환타입을 가진 메서드를 실행한다.
    //      text/html 은 ModelAndView 반환, appliation/json 은 ResponseEntity<Map<String,Object>> 반환
    //          ResponseEntity -> body 에 직접 반환값 작성 (json 형식)

    // Spring 에서 제공하는 ExceptionResolver 구현체
    //  (우선순위 순서, null 반환시 다음 순위 Resolver 시도 )

    //  1. ExceptionHandlerExceptionResolver
    //      @ExceptionHandler 주해를 처리하며, API 예외처리는 대부분 이 기능으로 해결한다. (제일 자주쓰임)

    //  2. ResponseStatusExceptionResolver
    //      @ResponseStatus(value = HttpStatus.NOT_FOUND) 주해를 처리, HTTP 상태 코드를 지정해줌.
    //      ResponseStatusException 예외를 처리한다.

    //  3. DefaultHandlerExceptionResolver
    //      스프링 내부 기본 예외 처리


//    ResponseStatusExceptionResolver (/exception/BadRequestException.class 참고 )

//    DefaultHandlerExceptionResolver
    // 스프링 내부에서 발생하는 스프링 예외를 해결한다.
    //  대표적으로 바인딩 타입 예외인 TypeMismatchException 의 처리 등이 있다.
    //      해당 예외가 처리되지 않고, WAS 까지 올라가면 상태코드 500 으로 처리되어야하나,
    //      DefaultHandlerExceptionResolver 에 의해 상태코드 400 으로 변경된다. (유저입력오류로)

//    ExceptionHandlerExceptionResolver (@ExceptionHandler)
    // HTML 화면 오류 vs API 오류
    //  웹 브라우저에 HTML 화면을 제공할때는 오류가 발생하면 BasicErrorController 사용하는게 편하다.
    //  하지만 API 각 시스템마다 응답의 모양도 다르고, 스펙도 모두 다르다.
    //      게다가 같은 예외라고 해도, 어떤 컨트롤러에서 발생했는가에 따라 다른 응답을 해야 할때도 있다.
    //      상품API 와 주문API 는 오류가 발생했을때 응답의 모양이 전혀 다를 수 있다.
    //  BasicErrorController 나 HandlerExceptionResolver 를 직접 구현하는 방식으로 API 예외를 다루기는 어렵다.

    // API 예외 처리의 어려운 점 (UserHandlerExceptionResolver 참고)
    //  HandlerExceptionResolver 구현시, 필요없는 ModelAndView 를 굳이 반환하는점.
    //  HttpServletResponse (response) 객체에 직접 응답 데이터를 넣어야 하는 점.
    //  다른 컨트롤러에서 같은 예외가 발생할 경우 서로다른 방식으로 처리하기 어렵다는 점.

    // @ExceptionHandler
    //  Spring 에서 제공하는 API 예외처리 resolver 인 ExceptionHandlerExceptionResolver 사용
    //  실무에서도 대부분의 API 예외처리는 해당 방법을 사용
    //  /exhandler/ErrorResult.class, /api/ApiExceptionV2Controller.class 참고



}
