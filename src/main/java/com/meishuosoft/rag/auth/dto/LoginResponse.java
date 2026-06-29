package com.meishuosoft.rag.auth.dto;

import java.time.Instant;

/**
 * 登录成功响应。
 */
public class LoginResponse {

    /** Token 类型，当前固定为 Bearer。 */
    private final String tokenType;

    /** JWT 访问令牌，后续请求放入 Authorization: Bearer 头。 */
    private final String accessToken;

    /** Token 过期时间。 */
    private final Instant expiresAt;

    /** 当前用户权限上下文快照。 */
    private final CurrentUserResponse user;

    public LoginResponse(String tokenType, String accessToken, Instant expiresAt, CurrentUserResponse user) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public CurrentUserResponse getUser() {
        return user;
    }
}
