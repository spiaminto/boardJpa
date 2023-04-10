package hello.board.log.trace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


// 참고)
/*
// LogTrace 의 실행 흐름 예시는 대략 이렇다.
// Client 요청 -> << Proxy 1 >>
//                  logTrace.begin( sync null... )
//                  Controller.logic() { Service.logic() }
//                                          << Proxy2 >>
//                                            logTrace.begin( sync Yes ... )
//                                            Service.logic() { Repository.logic() }
//                                            logTrace.complete( release Prev ... )
//                  logTrace.complete( release Destroy ... )
 */

@Slf4j
@Component
public class ThreadLocalLogTrace implements LogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    // 동시성 문제해결을 위한 ThreadLocal
    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    @Override
    public TraceStatus begin(String message) {
        // traceIdHolder 초기화(동기화)
        syncTraceId();

        TraceId traceId = traceIdHolder.get();
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);

        return new TraceStatus(traceId, message);
    }

    @Override
    public void end(TraceStatus status) {
        complete(status, null, null);
    }

    @Override
    public void exception(TraceStatus status, Exception e, Object[] params) {
        complete(status, e, params);
    }


    private void complete(TraceStatus status, Exception e, Object[] params) {
        TraceId traceId = status.getTraceId();
        if (e == null) {
            log.info("[{}] {}{}", traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage());
        } else {
            // exception 발생 시 파라미터를 같이 출력
            StringBuilder sb = new StringBuilder();
            for (Object param : params) {
                sb.append(param);
            }
            log.info("[{}] {}{} [ex] = {} [params] = {}", traceId.getId(), addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), e, sb);
        }

        releaseTraceId();
    }

    // TraceId 를 동기화. traceIdHolder 에 TraceId 가 있으면 createNextId(), 없으면 새로운 TraceId 생성
    //  두 작업 모두 완료 후 ThreadLocal<TraceId> (쓰레드 로컬) 에 TraceId 를 set (저장) 한다.
    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new TraceId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    // TraceId 를 해제. 깊이가 0 이면 destroy
    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove();  //destroy
        } else {
            traceIdHolder.set(traceId.createPreviousId());
        }
    }

    // 화살표 그리기
    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }
}
