package com.gymcrm.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class TransactionLoggingFilter extends OncePerRequestFilter {

    private static final String TRANSACTION_ID_KEY = "transactionId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String transactionId = UUID.randomUUID().toString();
        MDC.put(TRANSACTION_ID_KEY, transactionId);

        String method = request.getMethod();
        String uri = request.getRequestURI();

        long start = System.currentTimeMillis();

        try {
            log.info("Incoming request: [{}] {} | transactionId={}", method, uri, transactionId);
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            int status = response.getStatus();
            log.info("Completed request: [{}] {} | status={} | duration={}ms | transactionId={}",
                    method, uri, status, duration, transactionId);
            MDC.remove(TRANSACTION_ID_KEY);
        }
    }
}
