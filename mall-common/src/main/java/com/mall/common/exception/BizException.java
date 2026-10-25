package com.mall.common.exception;

import com.mall.common.api.ResultCode;

import java.io.Serial;

/**
 * 业务异常。
 *
 * <p>凡是「可以预期、需要提示用户」的错误都抛这个异常，由
 * {@link GlobalExceptionHandler} 统一转换为 HTTP 200 + 业务错误码。
 *
 * <p>不要用它包装系统异常（如数据库连接失败、空指针），那类错误应直接抛出，
 * 由兜底处理返回 500 + {@link ResultCode#SYSTEM_ERROR}，堆栈只落日志不外泄。
 *
 * <p>使用示例：
 * <pre>
 * if (stock &lt; quantity) {
 *     throw new BizException(ResultCode.STOCK_NOT_ENOUGH);
 * }
 * // 需要拼接动态文案时
 * throw new BizException(ResultCode.STOCK_NOT_ENOUGH, "库存仅剩 " + stock + " 件");
 * </pre>
 */
public class BizException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 业务错误码，对应 {@link ResultCode#getCode()} */
    private final int code;

    /**
     * 使用错误码的默认文案。
     *
     * @param resultCode 预定义错误码
     */
    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * 使用自定义文案覆盖默认文案。
     *
     * @param resultCode 预定义错误码
     * @param message    展示给用户的提示，例如「库存仅剩 2 件」
     */
    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    /**
     * 需要保留原始异常时使用（如第三方接口返回业务失败）。
     *
     * @param resultCode 预定义错误码
     * @param message    自定义提示
     * @param cause      原始异常，会记录在日志里
     */
    public BizException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.code = resultCode.getCode();
    }

    /**
     * 直接指定错误码，仅用于历史系统对接等无法归入枚举的场景。
     *
     * @param code    业务状态码
     * @param message 提示信息
     */
    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * 轻量异常：不填充堆栈。
     *
     * <p>业务异常是「正常流程的一部分」（如库存不足、重复提交），
     * 高频抛出时如果每次都抓取堆栈，会带来明显的 CPU 开销，
     * 因此这里直接返回自身。排查问题依赖 traceId 与日志即可。
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
