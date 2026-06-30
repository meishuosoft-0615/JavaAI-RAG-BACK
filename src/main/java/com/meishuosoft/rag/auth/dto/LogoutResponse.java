package com.meishuosoft.rag.auth.dto;

import java.time.Instant;

/**
 * 注销响应。
 *
 * <p>当前系统采用无状态 JWT，服务端不保存会话。注销接口用于给前端一个统一出口，
 * 前端收到成功响应后应删除本地 accessToken。</p>
 */
public class LogoutResponse {

    /** 是否已完成注销处理。 */
    private final boolean loggedOut;

    /** 注销提示信息。 */
    private final String message;

    /** 注销接口处理时间。 */
    private final Instant logoutAt;

    public LogoutResponse(boolean loggedOut, String message, Instant logoutAt) {
        this.loggedOut = loggedOut;
        this.message = message;
        this.logoutAt = logoutAt;
    }

    public boolean isLoggedOut() {
        return loggedOut;
    }

    public String getMessage() {
        return message;
    }

    public Instant getLogoutAt() {
        return logoutAt;
    }
}
