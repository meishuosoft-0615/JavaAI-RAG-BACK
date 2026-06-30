package com.meishuosoft.rag.auth.controller;

import com.meishuosoft.rag.auth.dto.CurrentUserResponse;
import com.meishuosoft.rag.auth.dto.LoginRequest;
import com.meishuosoft.rag.auth.dto.LoginResponse;
import com.meishuosoft.rag.auth.dto.LogoutResponse;
import com.meishuosoft.rag.auth.model.CurrentUser;
import com.meishuosoft.rag.auth.service.AuthService;
import com.meishuosoft.rag.common.api.ApiResponse;
import com.meishuosoft.rag.common.web.RequestIdFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * 认证接口。
 *
 * <p>负责登录和当前用户上下文查询。除登录接口外，认证模块接口都需要 Bearer Token。</p>
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证接口", description = "用户登录、注销和当前用户上下文查询")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录。
     */
    @Operation(summary = "用户登录", description = "使用租户编码、用户名和密码登录，成功后返回 Bearer JWT。")
    @SecurityRequirements
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return ApiResponse.ok(authService.login(request), requestId(httpRequest));
    }

    /**
     * 用户注销。
     *
     * <p>当前系统使用无状态 JWT，服务端不维护会话，因此注销接口不要求 token。
     * 前端调用成功后删除本地 token 即完成注销。</p>
     */
    @Operation(summary = "用户注销", description = "无状态 JWT 注销接口，前端收到成功响应后删除本地 accessToken。")
    @SecurityRequirements
    @PostMapping("/logout")
    public ApiResponse<LogoutResponse> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        LogoutResponse response = new LogoutResponse(true, "已注销，请清理本地 accessToken", Instant.now());
        return ApiResponse.ok(response, requestId(request));
    }

    /**
     * 查询当前登录用户上下文。
     */
    @Operation(summary = "查询当前用户", description = "返回当前 token 对应的租户、用户、部门和角色上下文。")
    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> currentUser(
            @AuthenticationPrincipal CurrentUser currentUser,
            HttpServletRequest request
    ) {
        return ApiResponse.ok(CurrentUserResponse.from(currentUser), requestId(request));
    }

    private String requestId(HttpServletRequest request) {
        Object requestId = request.getAttribute(RequestIdFilter.REQUEST_ID_ATTRIBUTE);
        return requestId == null ? null : requestId.toString();
    }
}
