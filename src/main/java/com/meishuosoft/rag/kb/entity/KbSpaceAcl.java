package com.meishuosoft.rag.kb.entity;

import java.time.LocalDateTime;

/**
 * 知识库空间 ACL 实体，对应 kb_space_acl 表。
 */
public class KbSpaceAcl {

    /** ACL 主键 ID。 */
    private Long id;

    /** 所属租户 ID。 */
    private Long tenantId;

    /** 知识库空间 ID。 */
    private Long spaceId;

    /** 授权部门 ID，为空表示不是部门授权。 */
    private Long departmentId;

    /** 授权角色 ID，为空表示不是角色授权。 */
    private Long roleId;

    /** 授权用户 ID，为空表示不是用户授权。 */
    private Long userId;

    /** 权限：READ 可访问，MANAGE 可管理。 */
    private String permission;

    /** 创建人用户 ID。 */
    private Long createdBy;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
