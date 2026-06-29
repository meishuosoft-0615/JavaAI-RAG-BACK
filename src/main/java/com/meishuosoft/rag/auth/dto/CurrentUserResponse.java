package com.meishuosoft.rag.auth.dto;

import com.meishuosoft.rag.auth.model.CurrentUser;

import java.util.Set;

/**
 * 当前用户信息响应。
 *
 * <p>供前端展示登录用户信息，也供联调时确认租户、部门、角色上下文是否正确。</p>
 */
public class CurrentUserResponse {

    /** 用户 ID。 */
    private final Long userId;

    /** 租户 ID。 */
    private final Long tenantId;

    /** 用户名。 */
    private final String username;

    /** 部门 ID。 */
    private final Long departmentId;

    /** 角色 ID 集合。 */
    private final Set<Long> roleIds;

    public CurrentUserResponse(Long userId, Long tenantId, String username, Long departmentId, Set<Long> roleIds) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.username = username;
        this.departmentId = departmentId;
        this.roleIds = roleIds;
    }

    public static CurrentUserResponse from(CurrentUser user) {
        return new CurrentUserResponse(
                user.getUserId(),
                user.getTenantId(),
                user.getUsername(),
                user.getDepartmentId(),
                user.getRoleIds()
        );
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getUsername() {
        return username;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }
}
