package com.meishuosoft.rag.auth.security;

import com.meishuosoft.rag.auth.model.CurrentUser;
import com.meishuosoft.rag.common.api.ApiResponse;
import com.meishuosoft.rag.common.exception.BusinessException;
import com.meishuosoft.rag.common.exception.ErrorCode;
import com.meishuosoft.rag.common.web.RequestIdFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = resolveBearerToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            CurrentUser currentUser = jwtTokenProvider.parse(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(currentUser, token, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (BusinessException exception) {
            SecurityContextHolder.clearContext();
            writeUnauthorized(response, request, exception.getMessage());
        }
    }

    private String resolveBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring("Bearer ".length()).trim();
    }

    private void writeUnauthorized(HttpServletResponse response, HttpServletRequest request, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Object requestId = request.getAttribute(RequestIdFilter.REQUEST_ID_ATTRIBUTE);
        ApiResponse<Void> body = ApiResponse.fail(
                ErrorCode.UNAUTHORIZED,
                message,
                requestId == null ? null : requestId.toString()
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
