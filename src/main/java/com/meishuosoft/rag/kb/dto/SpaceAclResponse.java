package com.meishuosoft.rag.kb.dto;

import com.meishuosoft.rag.kb.entity.KbSpaceAcl;

import java.time.LocalDateTime;

/**
 * 知识库空间 ACL 响应对象。
 */
public class SpaceAclResponse {

    /** ACL 主键 ID。 */
    private final Long id;

    /** 知识库空间 ID。 */
    private final Long spaceId;

    /** 授权部门 ID。 */
    private final Long departmentId;

    /** 授权角色 ID。 */
    private final Long roleId;

    /** 授权用户 ID。 */
    private final Long userId;

    /** 权限：READ 可访问，MANAGE 可管理。 */
    private final String permission;

    /** 创建时间。 */
    private final LocalDateTime createdAt;

    public SpaceAclResponse(
            Long id,
            Long spaceId,
            Long departmentId,
            Long roleId,
            Long userId,
            String permission,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.spaceId = spaceId;
        this.departmentId = departmentId;
        this.roleId = roleId;
        this.userId = userId;
        this.permission = permission;
        this.createdAt = createdAt;
    }

    public static SpaceAclResponse from(KbSpaceAcl acl) {
        return new SpaceAclResponse(
                acl.getId(),
                acl.getSpaceId(),
                acl.getDepartmentId(),
                acl.getRoleId(),
                acl.getUserId(),
                acl.getPermission(),
                acl.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPermission() {
        return permission;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
