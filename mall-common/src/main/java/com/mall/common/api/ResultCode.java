package com.mall.common.api;

/**
 * 全局错误码。
 *
 * <p>编码规则（按业务段划分，方便前端按区间做统一处理，也方便排查归属）：
 * <ul>
 *     <li>0        成功</li>
 *     <li>10xxx    通用/框架层（参数、鉴权、限流等）</li>
 *     <li>11xxx    用户</li>
 *     <li>20xxx    商品</li>
 *     <li>25xxx    购物车</li>
 *     <li>30xxx    订单</li>
 *     <li>40xxx    库存</li>
 *     <li>50xxx    支付</li>
 *     <li>99999    系统兜底</li>
 * </ul>
 *
 * <p>新增错误码时请遵守区段规则：同一业务段的编码保持连续，
 * 中间预留空号（如 10002、10007）方便后续插入，避免改号导致前端适配。
 */
public enum ResultCode {

    /** 成功，唯一表示业务成功的码 */
    SUCCESS(0, "成功"),

    // ---------- 通用 10xxx ----------
    /** 参数校验不通过（缺参、格式错、@Valid 失败等） */
    PARAM_ERROR(10000, "参数错误"),
    /** 未登录或 token 失效，前端应跳转登录页 */
    UNAUTHORIZED(10001, "未登录或登录已过期"),
    /** 已登录但无该资源权限 */
    FORBIDDEN(10003, "无访问权限"),
    /** 资源不存在（含接口路径不存在） */
    NOT_FOUND(10004, "资源不存在"),
    /** HTTP 方法不支持，如 GET 接口用 POST 访问 */
    METHOD_NOT_ALLOWED(10005, "请求方法不支持"),
    /** Content-Type 不被支持，如该传 JSON 却传了表单 */
    MEDIA_TYPE_NOT_SUPPORTED(10006, "请求格式不支持"),
    /** 触发限流/风控，通常配合重试提示 */
    REQUEST_TOO_FREQUENT(10029, "请求过于频繁，请稍后再试"),

    // ---------- 用户 11xxx ----------
    USER_NOT_FOUND(11001, "用户不存在"),
    USERNAME_EXISTS(11002, "用户名已存在"),
    PASSWORD_ERROR(11003, "用户名或密码错误"),

    // ---------- 商品 20xxx ----------
    PRODUCT_NOT_FOUND(20001, "商品不存在"),
    PRODUCT_OFF_SHELF(20002, "商品已下架"),
    SKU_NOT_FOUND(20003, "SKU 不存在"),

    // ---------- 购物车 25xxx ----------
    CART_ITEM_NOT_FOUND(25001, "购物车中不存在该商品"),

    // ---------- 订单 30xxx ----------
    ORDER_NOT_FOUND(30001, "订单不存在"),
    ORDER_STATUS_ILLEGAL(30002, "当前订单状态不允许该操作"),
    ORDER_AMOUNT_MISMATCH(30003, "订单金额校验失败"),
    ORDER_REPEAT_SUBMIT(30004, "请勿重复提交"),

    // ---------- 库存 40xxx ----------
    STOCK_NOT_ENOUGH(40001, "库存不足"),
    STOCK_DEDUCT_FAILED(40002, "库存扣减失败"),

    // ---------- 支付 50xxx ----------
    PAYMENT_NOT_FOUND(50001, "支付单不存在"),
    PAYMENT_ALREADY_PAID(50002, "订单已支付，请勿重复操作"),

    // ---------- 兜底 ----------
    /** 未预期异常的统一出口，真实原因只记录在服务端日志 */
    SYSTEM_ERROR(99999, "系统繁忙，请稍后再试");

    /** 业务状态码 */
    private final int code;

    /** 默认提示文案，可由 BizException 覆盖 */
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据状态码反查枚举，用于把第三方返回码或配置里的数字转成枚举。
     * <p>查不到时返回 {@link #SYSTEM_ERROR}，避免返回 null 导致调用方 NPE。
     */
    public static ResultCode fromCode(int code) {
        for (ResultCode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return SYSTEM_ERROR;
    }
}
