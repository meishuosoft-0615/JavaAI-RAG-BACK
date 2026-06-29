package com.meishuosoft.rag.common.api;

import com.meishuosoft.rag.common.exception.ErrorCode;

/**
 * 统一 API 响应包装。
 *
 * <p>所有普通 JSON 接口都返回 code、message、data 和 requestId，便于前端统一处理错误，
 * 也便于通过 requestId 追踪一次请求的日志链路。</p>
 */
public class ApiResponse<T> {

    /** 业务响应码，0 表示成功。 */
    private final String code;

    /** 响应消息，失败时为错误原因。 */
    private final String message;

    /** 业务响应数据。 */
    private final T data;

    /** 请求唯一 ID，对应响应头 X-Request-Id。 */
    private final String requestId;

    public ApiResponse(String code, String message, T data, String requestId) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.requestId = requestId;
    }

    public static <T> ApiResponse<T> ok(T data, String requestId) {
        return new ApiResponse<>(ErrorCode.SUCCESS.code(), ErrorCode.SUCCESS.message(), data, requestId);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message, String requestId) {
        return new ApiResponse<>(errorCode.code(), message, null, requestId);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getRequestId() {
        return requestId;
    }
}
