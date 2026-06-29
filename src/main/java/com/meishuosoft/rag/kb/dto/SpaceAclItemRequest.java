package com.meishuosoft.rag.kb.dto;

import jakarta.validation.constraints.Pattern;

/**
 * 知识库空间 ACL 授权项。
 *
 * <p>departmentId、roleId、userId 必须且只能填写一个，表示按部门、角色或用户授权。</p>
 */
public class SpaceAclItemRequest {

    /** 授权部门 ID，填写后表示部门级授权。 */
    private Long departmentId;

    /** 授权角色 ID，填写后表示角色级授权。 */
    private Long roleId;

    /** 授权用户 ID，填写后表示用户级授权。 */
    private Long userId;

    /** 授权权限：READ 可访问，MANAGE 可管理。 */
    @Pattern(regexp = "READ|MANAGE")
    private String permission = "READ";

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
}
