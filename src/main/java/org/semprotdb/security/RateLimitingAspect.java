package org.semprotdb.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

///https://www.innoq.com/en/blog/2024/02/rate-limiting-with-spring-boot/
///https://levelup.gitconnected.com/rate-limiting-in-spring-boot-52220ba272c6

@Aspect
@Component
public class RateLimitingAspect {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingAspect.class);
    private static final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private static final int REQUEST_LIMIT_BY_CLIENT = 1;

    @Before("@annotation(org.semprotdb.security.RateLimitingAspect.RateLimited)")
    public void beforeRequest() {
        handle("ALL");
    }

    @Before("@annotation(org.semprotdb.security.RateLimitingAspect.RateLimitedByIP)")
    public void beforeIPRequest() {
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final String key = requestAttributes.getRequest().getRemoteAddr();
        handle(key);
    }

    private void handle(final String clientId) {
        AtomicInteger count = requestCounts.computeIfAbsent(clientId, k -> new AtomicInteger(0));
        if (count.incrementAndGet() > REQUEST_LIMIT_BY_CLIENT) {
            String msg = "As requisicoes excederam o limite de " + REQUEST_LIMIT_BY_CLIENT + " para o cliente " + clientId;
            log.warn(msg);
            throw new RateLimitExceededException(msg);
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    private void resetRequestCounts() {
        requestCounts.clear();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface RateLimited {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface RateLimitedByIP {
    }
}
