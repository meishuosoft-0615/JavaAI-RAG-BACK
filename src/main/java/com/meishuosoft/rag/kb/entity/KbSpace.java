package com.meishuosoft.rag.kb.entity;

import java.time.LocalDateTime;

/**
 * 知识库空间实体，对应 kb_space 表。
 */
public class KbSpace {

    /** 知识库空间主键 ID。 */
    private Long id;

    /** 所属租户 ID。 */
    private Long tenantId;

    /** 知识库空间名称。 */
    private String name;

    /** 知识库空间描述。 */
    private String description;

    /** 空间负责人用户 ID。 */
    private Long ownerId;

    /** 空间状态：ENABLED 启用，DISABLED 停用。 */
    private String status;

    /** 创建人用户 ID。 */
    private Long createdBy;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
