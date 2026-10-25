package com.mall.common.web;

import com.mall.common.util.IdUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * traceId 过滤器：一次请求生成/继承一个 traceId，写入 MDC 与响应头。
 *
 * <p>执行顺序必须是最高优先级，保证后续所有过滤器、拦截器、业务日志都能拿到 traceId。
 */
public class TraceIdFilter extends OncePerRequestFilter implements Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 优先继承上游（网关/浏览器/SkyWalking）传来的 traceId，保证全链路一致
        String traceId = request.getHeader(TraceContext.HEADER);
        if (!TraceContext.isValid(traceId)) {
            traceId = IdUtils.newTraceId();
        }

        TraceContext.set(traceId);
        response.setHeader(TraceContext.HEADER, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            // 必须清理，否则线程池复用会导致串号
            TraceContext.clear();
        }
    }
}
