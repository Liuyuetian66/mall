package com.mall.common.exception;

import com.mall.common.api.ResultCode;

import java.io.Serial;

/**
 * 业务异常。
 *
 * <p>凡是"可以预期、需要提示用户"的错误都抛这个异常，由
 * {@link GlobalExceptionHandler} 统一转换为 200 + 业务错误码。
 *
 * <p>不要用它包装系统异常（如数据库连接失败），那类错误应让它抛出，
 * 由兜底处理返回 500 + {@link ResultCode#SYSTEM_ERROR}。
 */
public class BizException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int code;

    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public BizException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.code = resultCode.getCode();
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /** 轻量异常，不需要堆栈，避免高频抛异常时的性能开销 */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
