package hello.board.config;

import hello.board.aop.LogTraceAspect;
import hello.board.log.trace.LogTrace;
import hello.board.log.trace.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

    // LogTrace 구현체 등록
    @Bean
    public LogTrace logTrace() { return new ThreadLocalLogTrace(); }

    // @Aspect 등록
    @Bean
    public LogTraceAspect logTraceAspect(LogTrace logTrace) {
        return new LogTraceAspect(logTrace);
    }

}
