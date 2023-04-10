package hello.board.log.trace;

// 참고 : 인프런 스프링핵심원리 고급편
public interface LogTrace {

    TraceStatus begin(String message);
    void end(TraceStatus status);
    void exception(TraceStatus status, Exception e, Object[] params);
}
