package com.meishuosoft.rag.auth.controller;

import com.meishuosoft.rag.auth.dto.CurrentUserResponse;
import com.meishuosoft.rag.auth.dto.LoginRequest;
import com.meishuosoft.rag.auth.dto.LoginResponse;
import com.meishuosoft.rag.auth.model.CurrentUser;
import com.meishuosoft.rag.auth.service.AuthService;
import com.meishuosoft.rag.common.api.ApiResponse;
import com.meishuosoft.rag.common.web.RequestIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口。
 *
 * <p>负责登录和当前用户上下文查询。除登录接口外，认证模块接口都需要 Bearer Token。</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return ApiResponse.ok(authService.login(request), requestId(httpRequest));
    }

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
