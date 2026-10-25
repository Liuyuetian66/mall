package com.mall.common.web;

/**
 * 链路追踪上下文。基于 SLF4J MDC 实现，日志模板里用 {@code %X{traceId}} 输出。
 *
 * <p>注意：MDC 是 ThreadLocal 实现，线程池/异步任务中会丢失，
 * 需要手动透传（见 {@link #wrap(Runnable)}）。
 */
public final class TraceContext {

    /** MDC 中的 key，与 logback 的 %X{traceId} 保持一致 */
    public static final String MDC_KEY = "traceId";

    /** 上游传下来的请求头 */
    public static final String HEADER = "X-Trace-Id";

    private TraceContext() {
    }

    public static void set(String traceId) {
        org.slf4j.MDC.put(MDC_KEY, traceId);
    }

    public static String getTraceId() {
        return org.slf4j.MDC.get(MDC_KEY);
    }

    public static void clear() {
        org.slf4j.MDC.remove(MDC_KEY);
    }

    /**
     * 包装 Runnable，让异步线程也能带上当前 traceId。
     * <p>用法：{@code executor.submit(TraceContext.wrap(() -> doSomething()))}
     */
    public static Runnable wrap(Runnable task) {
        String traceId = getTraceId();
        return () -> {
            String previous = getTraceId();
            set(traceId);
            try {
                task.run();
            } finally {
                if (previous == null) {
                    clear();
                } else {
                    set(previous);
                }
            }
        };
    }

    /**
     * 包装 Callable，让异步线程也能带上当前 traceId。
     */
    public static <T> java.util.concurrent.Callable<T> wrapCallable(java.util.concurrent.Callable<T> task) {
        String traceId = getTraceId();
        return () -> {
            String previous = getTraceId();
            set(traceId);
            try {
                return task.call();
            } finally {
                if (previous == null) {
                    clear();
                } else {
                    set(previous);
                }
            }
        };
    }

    /** 兜底判断：traceId 是否合法（防止上游传入超长/恶意字符串） */
    public static boolean isValid(String traceId) {
        if (traceId == null) {
            return false;
        }
        int len = traceId.length();
        if (len < 8 || len > 64) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char c = traceId.charAt(i);
            boolean ok = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z') || c == '-' || c == '_';
            if (!ok) {
                return false;
            }
        }
        return true;
    }
}
