package com.meishuosoft.rag.auth.entity;

import java.time.LocalDateTime;

/**
 * 系统租户实体，对应 sys_tenant 表。
 */
public class SysTenant {

    /** 租户主键 ID。 */
    private Long id;

    /** 租户名称。 */
    private String name;

    /** 租户编码，登录时用于定位租户。 */
    private String code;

    /** 租户状态：ENABLED 启用，DISABLED 停用。 */
    private String status;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
