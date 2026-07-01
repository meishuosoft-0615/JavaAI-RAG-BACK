package com.meishuosoft.rag.auth.security;

import com.meishuosoft.rag.auth.model.CurrentUser;
import com.meishuosoft.rag.auth.service.PermissionService;
import com.meishuosoft.rag.common.exception.BusinessException;
import com.meishuosoft.rag.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequirePermissionInterceptorTest {

    @Test
    void preHandleAllowsWhenCurrentUserHasRequiredPermission() throws Exception {
        CurrentUser currentUser = new CurrentUser(1L, 10L, "admin", 100L, Set.of(1L));
        PermissionService permissionService = mock(PermissionService.class);
        when(permissionService.hasPermission(currentUser, "kb:manage")).thenReturn(true);
        RequirePermissionInterceptor interceptor = new RequirePermissionInterceptor(permissionService);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, "token")
        );

        try {
            boolean allowed = interceptor.preHandle(null, null, handlerMethod());

            assertThat(allowed).isTrue();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void preHandleThrowsForbiddenWhenCurrentUserLacksRequiredPermission() throws Exception {
        CurrentUser currentUser = new CurrentUser(1L, 10L, "user", 100L, Set.of(3L));
        PermissionService permissionService = mock(PermissionService.class);
        when(permissionService.hasPermission(currentUser, "kb:manage")).thenReturn(false);
        RequirePermissionInterceptor interceptor = new RequirePermissionInterceptor(permissionService);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, "token")
        );

        try {
            assertThatThrownBy(() -> interceptor.preHandle(null, null, handlerMethod()))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> assertThat(((BusinessException) exception).errorCode())
                            .isEqualTo(ErrorCode.FORBIDDEN));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private HandlerMethod handlerMethod() throws NoSuchMethodException {
        return new HandlerMethod(new SecuredHandler(), SecuredHandler.class.getDeclaredMethod("createSpace"));
    }

    private static class SecuredHandler {

        @RequirePermission("kb:manage")
        void createSpace() {
        }
    }
}
