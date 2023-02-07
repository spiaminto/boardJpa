package hello.z_comment;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class Z_Login {

    /* 쿠키 사용에 따른보안문제*/
    // 문제
    // 쿠기값은 클라이언트 단에서 임의로 변경 가능하며, 쿠키에 보관된 정보를 훔쳐갈 수 있다.
    // 뿐만아니라 쿠키 값은 유효기간이 따로 없으므로, 무제한으로 요청이 가능하다.
    // 대안
    // 쿠키에 중요 값을 노출하지 않고, 예측불가능한 임의의 토큰(랜덤값)을 노출하고, 서버에서
    //      토큰과 사용자 id를 매핑해서 인식한다. 토큰은 서버에서 관리한다.
    // 토큰에 만료시간을 두어 토큰이 털려도 시간이 지나면 사용불가능 하도록 한다.
    // 서버 세션을 이용한다.


    // 세션 만들어 보기 sessionManager ===================================================
    /**
     * 세션 생성
     *  sessionId 생성 (임의의 추정 불가능 한 랜덤 값)
     *  세션 저장소에 sessionId와 보관할 값 저장
     *  sessionId로 응답 쿠키를 생성하여 클라이언트에 전달
     * @param value: 등록 할 멤버 객체
     * */
    /*
                                                여러개 스레드 동시처리를 위해
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    public void createSession(Object value, HttpServletResponse response) {

        // 세션 id 를 생성하고, 값을 세션에 전달 (UUID 사용, java.util)
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);

        // 생성된 세션id 를 "mySessionId" 라는 이름의 쿠키에 담아 응답
        Cookie mySessionCookie = new Cookie("mySessionId", sessionId);
        response.addCookie(mySessionCookie);
    }

    결과적으로 sessionId 가 담긴 쿠키만 응답되게 된다. <- HttpSession 보면 uuid 담긴 쿠키만 보이는 이유.
    세션은 사용자가 브라우저 끌때까지 서버에서 유지되므로 지속적으로 연계되어있음.
    */


    // application.properties 파일에 session timeout 글로벌 설정 있음
    /* application.properties =====================================================

    # tracking-mode 를 cookie 로 설정.
    # 첫 로그인시 url 뒤에 파라미터로 붙는걸 해제할 수 있음.
    server.servlet.session.tracking-modes=cookie

    # HttpSession 의 생명주기 관리
    # HttpSession 은 사용자의 요청이 들어올때 마다 세션의 유효기간을 30분으로 재설정함.
    # 해당 코드를 통해 유효기간을 바꿀 수 있음.

    # 글로벌 세션 타임아웃 설정 (분단위로만 설정가능, 단위는 초 60, 120, ...)
    # 해당 타임아웃 시간이 지나면 WAS 가 내부에서 해당 세션을 제거한다.
    server.servlet.session.timeout=1800

    # 세션마다 타임아웃 개별설정
    # 개별설정은 해당 세션을 자바코드로 생성할때 session.setMaxInactiveInterval(1800) 을 통해 설정, 단위는 초
    */


    // 실무에서 세션에는 최소한의 데이터만 보관해야한다.
    // 메모리 사용량이 급격하게 늘기 때문에 예제처럼 member 객체 전체를 보관하기 보단 member.id 만 보관하는등
    // 최소한으로 이용해야한다.


    // Login 에서 ArgumentResolver 통해 애노테이션 만들고 수정하고 그랬음. 해당내용은 login 프로젝트 참고.
}
