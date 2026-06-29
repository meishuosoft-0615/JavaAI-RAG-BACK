package com.meishuosoft.rag.kb.dto;

import com.meishuosoft.rag.kb.entity.KbSpace;

import java.time.LocalDateTime;

/**
 * 知识库空间响应对象。
 */
public class SpaceResponse {

    /** 知识库空间 ID。 */
    private final Long id;

    /** 所属租户 ID。 */
    private final Long tenantId;

    /** 知识库空间名称。 */
    private final String name;

    /** 知识库空间描述。 */
    private final String description;

    /** 空间负责人用户 ID。 */
    private final Long ownerId;

    /** 空间状态：ENABLED 启用，DISABLED 停用。 */
    private final String status;

    /** 创建时间。 */
    private final LocalDateTime createdAt;

    /** 更新时间。 */
    private final LocalDateTime updatedAt;

    public SpaceResponse(
            Long id,
            Long tenantId,
            String name,
            String description,
            Long ownerId,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SpaceResponse from(KbSpace space) {
        return new SpaceResponse(
                space.getId(),
                space.getTenantId(),
                space.getName(),
                space.getDescription(),
                space.getOwnerId(),
                space.getStatus(),
                space.getCreatedAt(),
                space.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
