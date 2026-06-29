package com.meishuosoft.rag.auth.model;

import java.util.Set;

/**
 * 当前登录用户上下文。
 *
 * <p>该对象由 JWT 解析后写入 Spring Security 上下文，后续业务接口必须从这里获取
 * 租户、用户、部门和角色信息，不能信任前端传入的权限字段。</p>
 */
public class CurrentUser {

    /** 用户 ID，对应 sys_user.id。 */
    private final Long userId;

    /** 租户 ID，对应 sys_tenant.id，是所有业务数据隔离的第一过滤条件。 */
    private final Long tenantId;

    /** 登录用户名，对应 sys_user.username。 */
    private final String username;

    /** 用户所属部门 ID，用于空间、文档和检索阶段的部门权限过滤。 */
    private final Long departmentId;

    /** 用户拥有的角色 ID 集合，用于角色级授权判断。 */
    private final Set<Long> roleIds;

    public CurrentUser(Long userId, Long tenantId, String username, Long departmentId, Set<Long> roleIds) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.username = username;
        this.departmentId = departmentId;
        this.roleIds = roleIds;
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
