package com.meishuosoft.rag.auth.entity;

import java.time.LocalDateTime;

/**
 * 系统用户实体，对应 sys_user 表。
 */
public class SysUser {

    /** 用户主键 ID。 */
    private Long id;

    /** 所属租户 ID。 */
    private Long tenantId;

    /** 登录用户名。 */
    private String username;

    /** 密码哈希，禁止保存明文密码。 */
    private String passwordHash;

    /** 用户真实姓名。 */
    private String realName;

    /** 所属部门 ID，用于部门级权限过滤。 */
    private Long departmentId;

    /** 用户状态：ENABLED 启用，DISABLED 停用。 */
    private String status;

    /** 最近登录时间。 */
    private LocalDateTime lastLoginAt;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 更新时间。 */
    private LocalDateTime updatedAt;

    /** 软删除标记：0 未删除，1 已删除。 */
    private Integer deleted;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
