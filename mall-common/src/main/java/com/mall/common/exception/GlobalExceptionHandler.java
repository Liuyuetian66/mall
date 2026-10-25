package com.mall.common.exception;

import com.mall.common.api.Result;
import com.mall.common.api.ResultCode;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.stream.Collectors;

/**
 * 全局异常处理。目标：任何异常都不会把堆栈泄露给前端，且响应结构永远统一。
 *
 * <p>处理策略：
 * <ul>
 *     <li>{@link BizException} → HTTP 200 + 业务错误码（业务失败不是 HTTP 失败）</li>
 *     <li>参数校验类 → HTTP 400 + 10000</li>
 *     <li>404/405/415 等 → 对应 HTTP 状态码 + 语义化错误码</li>
 *     <li>未知异常 → HTTP 500 + 99999，日志记录完整堆栈</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------------------------------------------------------------- 业务异常

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> handleBizException(BizException ex) {
        log.warn("业务异常 | code={} | message={}", ex.getCode(), ex.getMessage());
        return ResponseEntity.ok(Result.fail(ex.getCode(), ex.getMessage()));
    }

    // ------------------------------------------------------ 方法参数校验（@Validated）

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations() == null || ex.getConstraintViolations().isEmpty()
                ? ResultCode.PARAM_ERROR.getMessage()
                : ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败 | {}", message);
        return ResponseEntity.badRequest()
                .body(Result.fail(ResultCode.PARAM_ERROR.getCode(), message));
    }

    // ------------------------------------------------------------ 请求体校验（@Valid）

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        if (message.isEmpty()) {
            message = ResultCode.PARAM_ERROR.getMessage();
        }
        log.warn("请求体校验失败 | {}", message);
        return new ResponseEntity<>(Result.fail(ResultCode.PARAM_ERROR.getCode(), message), headers, status);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + " " + error.getDefaultMessage();
    }

    // --------------------------------------------------------------- 兜底异常

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleUncaught(Exception ex) {
        log.error("系统异常 | type={}", ex.getClass().getName(), ex);
        return ResponseEntity.internalServerError().body(Result.fail(ResultCode.SYSTEM_ERROR));
    }

    // ------------------------------------------ Spring MVC 内置异常统一包装

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {
        if (statusCode.is5xxServerError()) {
            log.error("服务端异常 | status={}", statusCode.value(), ex);
            return new ResponseEntity<>(Result.fail(ResultCode.SYSTEM_ERROR), headers, statusCode);
        }

        String message = resolveMessage(ex);
        log.warn("请求异常 | status={} | type={} | message={}",
                statusCode.value(), ex.getClass().getSimpleName(), message);
        return new ResponseEntity<>(Result.fail(resolveCode(ex).getCode(), message), headers, statusCode);
    }

    private ResultCode resolveCode(Exception ex) {
        if (ex instanceof NoResourceFoundException || ex instanceof NoHandlerFoundException) {
            return ResultCode.NOT_FOUND;
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return ResultCode.METHOD_NOT_ALLOWED;
        }
        if (ex instanceof HttpMediaTypeNotSupportedException
                || ex instanceof HttpMediaTypeNotAcceptableException) {
            return ResultCode.MEDIA_TYPE_NOT_SUPPORTED;
        }
        return ResultCode.PARAM_ERROR;
    }

    private String resolveMessage(Exception ex) {
        if (ex instanceof NoResourceFoundException resourceEx) {
            return "接口不存在: " + resourceEx.getResourcePath();
        }
        if (ex instanceof NoHandlerFoundException handlerEx) {
            return "接口不存在: " + handlerEx.getRequestURL();
        }
        if (ex instanceof HttpRequestMethodNotSupportedException methodEx) {
            return "请求方法不支持: " + methodEx.getMethod();
        }
        if (ex instanceof MissingServletRequestParameterException paramEx) {
            return "缺少必填参数: " + paramEx.getParameterName();
        }
        if (ex instanceof HttpMessageNotReadableException) {
            return "请求体为空或格式错误";
        }
        if (ex instanceof TypeMismatchException typeEx) {
            return "参数类型不匹配: " + typeEx.getPropertyName();
        }
        if (ex instanceof ServletRequestBindingException) {
            return "请求参数绑定失败";
        }
        return ex.getMessage() == null ? ResultCode.PARAM_ERROR.getMessage() : ex.getMessage();
    }
}
