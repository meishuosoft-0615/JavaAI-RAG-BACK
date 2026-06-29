package com.meishuosoft.rag.auth.model;

import java.util.Set;

/**
 * 权限过滤上下文。
 *
 * <p>RAG 平台必须在检索阶段过滤租户、部门、角色和用户权限，该对象用于把
 * CurrentUser 转换成检索和数据访问层更稳定的权限条件。</p>
 */
public class PermissionContext {

    /** 当前租户 ID。 */
    private final Long tenantId;

    /** 当前用户 ID。 */
    private final Long userId;

    /** 当前用户部门 ID。 */
    private final Long departmentId;

    /** 当前用户角色 ID 集合。 */
    private final Set<Long> roleIds;

    public PermissionContext(Long tenantId, Long userId, Long departmentId, Set<Long> roleIds) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.departmentId = departmentId;
        this.roleIds = roleIds;
    }

    public static PermissionContext from(CurrentUser user) {
        return new PermissionContext(
                user.getTenantId(),
                user.getUserId(),
                user.getDepartmentId(),
                user.getRoleIds()
        );
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }
}
