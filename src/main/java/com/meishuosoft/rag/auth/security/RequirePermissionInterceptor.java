package com.meishuosoft.rag.auth.security;

import com.meishuosoft.rag.auth.model.CurrentUser;
import com.meishuosoft.rag.auth.service.PermissionService;
import com.meishuosoft.rag.common.exception.BusinessException;
import com.meishuosoft.rag.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 方法级系统功能权限拦截器。
 *
 * <p>登录态由 Spring Security 保证，本拦截器只处理带 {@link RequirePermission} 的接口。</p>
 */
@Component
public class RequirePermissionInterceptor implements HandlerInterceptor {

    private final PermissionService permissionService;

    public RequirePermissionInterceptor(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (requirePermission == null) {
            return true;
        }

        CurrentUser currentUser = currentUser();
        if (!permissionService.hasPermission(currentUser, requirePermission.value())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "No permission to access this API");
        }
        return true;
    }

    private CurrentUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication == null ? null : authentication.getPrincipal();
        if (principal instanceof CurrentUser currentUser) {
            return currentUser;
        }
        throw new BusinessException(ErrorCode.UNAUTHORIZED, "Unauthorized");
    }
}
