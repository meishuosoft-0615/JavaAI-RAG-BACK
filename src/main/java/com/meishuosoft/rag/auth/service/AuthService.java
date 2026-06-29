package com.meishuosoft.rag.auth.service;

import com.meishuosoft.rag.auth.dto.CurrentUserResponse;
import com.meishuosoft.rag.auth.dto.LoginRequest;
import com.meishuosoft.rag.auth.dto.LoginResponse;
import com.meishuosoft.rag.auth.entity.SysTenant;
import com.meishuosoft.rag.auth.entity.SysUser;
import com.meishuosoft.rag.auth.mapper.SysTenantMapper;
import com.meishuosoft.rag.auth.mapper.SysUserMapper;
import com.meishuosoft.rag.auth.mapper.SysUserRoleMapper;
import com.meishuosoft.rag.auth.model.CurrentUser;
import com.meishuosoft.rag.auth.security.JwtTokenProvider;
import com.meishuosoft.rag.common.exception.BusinessException;
import com.meishuosoft.rag.common.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 认证服务。
 *
 * <p>登录时先通过租户编码定位租户，再在租户内校验用户名和密码，避免多租户下同名用户串租户。</p>
 */
@Service
public class AuthService {

    private static final String ENABLED = "ENABLED";

    private final SysTenantMapper tenantMapper;
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            SysTenantMapper tenantMapper,
            SysUserMapper userMapper,
            SysUserRoleMapper userRoleMapper,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.tenantMapper = tenantMapper;
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 用户登录并签发 JWT。
     *
     * <p>JWT 中只放权限过滤需要的最小上下文，敏感信息如密码哈希不会进入令牌。</p>
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        SysTenant tenant = tenantMapper.findByCode(request.getTenantCode());
        if (tenant == null || !ENABLED.equals(tenant.getStatus())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid tenant or credentials");
        }

        SysUser user = userMapper.findByTenantAndUsername(tenant.getId(), request.getUsername());
        if (user == null || !ENABLED.equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid tenant or credentials");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid tenant or credentials");
        }

        List<Long> roleIdList = userRoleMapper.findRoleIds(tenant.getId(), user.getId());
        Set<Long> roleIds = new HashSet<>(roleIdList);
        CurrentUser currentUser = new CurrentUser(
                user.getId(),
                tenant.getId(),
                user.getUsername(),
                user.getDepartmentId(),
                roleIds
        );
        JwtTokenProvider.TokenIssueResult token = jwtTokenProvider.issue(currentUser);
        userMapper.updateLastLoginAt(tenant.getId(), user.getId());

        return new LoginResponse(
                "Bearer",
                token.getToken(),
                token.getExpiresAt(),
                CurrentUserResponse.from(currentUser)
        );
    }
}
