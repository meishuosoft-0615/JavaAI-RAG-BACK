package com.meishuosoft.rag.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * 系统权限数据访问接口。
 *
 * <p>系统功能权限用于控制菜单、按钮和接口操作能力，返回给前端的权限码必须和前端约定一致。</p>
 */
@Mapper
public interface SysPermissionMapper {

    /**
     * 查询启用用户在指定租户内拥有的系统功能权限码。
     */
    @Select("SELECT DISTINCT p.permission_code "
            + "FROM sys_user u "
            + "JOIN sys_user_role ur "
            + "ON ur.tenant_id = u.tenant_id "
            + "AND ur.user_id = u.id "
            + "JOIN sys_role r "
            + "ON r.tenant_id = ur.tenant_id "
            + "AND r.id = ur.role_id "
            + "AND r.status = 'ENABLED' "
            + "AND r.deleted = 0 "
            + "JOIN sys_role_permission rp "
            + "ON rp.tenant_id = r.tenant_id "
            + "AND rp.role_id = r.id "
            + "JOIN sys_permission p "
            + "ON p.id = rp.permission_id "
            + "WHERE u.tenant_id = #{tenantId} "
            + "AND u.id = #{userId} "
            + "AND u.status = 'ENABLED' "
            + "AND u.deleted = 0 "
            + "ORDER BY p.permission_code ASC")
    Set<String> listPermissionCodes(@Param("tenantId") Long tenantId, @Param("userId") Long userId);
}
