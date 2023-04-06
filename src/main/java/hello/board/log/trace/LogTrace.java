package hello.board.log.trace;

// 다른 형태의 LogTrace 사용을 대비해 인터페이스로 생성
public interface LogTrace {

    TraceStatus begin(String message);
    void end(TraceStatus status);
    void exception(TraceStatus status, Exception e, Object[] params);
}
