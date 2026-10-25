package com.mall.common.api;

/**
 * 全局错误码。
 *
 * <p>编码规则（按业务段划分，方便前端与排查）：
 * <ul>
 *     <li>0        成功</li>
 *     <li>10xxx    通用/框架层</li>
 *     <li>11xxx    用户</li>
 *     <li>20xxx    商品</li>
 *     <li>25xxx    购物车</li>
 *     <li>30xxx    订单</li>
 *     <li>40xxx    库存</li>
 *     <li>50xxx    支付</li>
 *     <li>99999    系统兜底</li>
 * </ul>
 */
public enum ResultCode {

    SUCCESS(0, "成功"),

    // ---------- 通用 10xxx ----------
    PARAM_ERROR(10000, "参数错误"),
    UNAUTHORIZED(10001, "未登录或登录已过期"),
    FORBIDDEN(10003, "无访问权限"),
    NOT_FOUND(10004, "资源不存在"),
    METHOD_NOT_ALLOWED(10005, "请求方法不支持"),
    MEDIA_TYPE_NOT_SUPPORTED(10006, "请求格式不支持"),
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
    SYSTEM_ERROR(99999, "系统繁忙，请稍后再试");

    private final int code;
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

    public static ResultCode fromCode(int code) {
        for (ResultCode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return SYSTEM_ERROR;
    }
}
