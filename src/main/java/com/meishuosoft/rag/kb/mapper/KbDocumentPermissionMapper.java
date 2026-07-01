package com.meishuosoft.rag.kb.mapper;

import com.meishuosoft.rag.auth.model.CurrentUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 文档资源权限数据访问接口。
 */
@Mapper
public interface KbDocumentPermissionMapper {

    /** 判断当前用户是否可读取指定文档。 */
    @Select("SELECT COUNT(1) "
            + "FROM kb_document d "
            + "JOIN kb_space s "
            + "ON s.tenant_id = d.tenant_id "
            + "AND s.id = d.space_id "
            + "AND s.deleted = 0 "
            + "WHERE d.tenant_id = #{user.tenantId} "
            + "AND d.id = #{documentId} "
            + "AND d.deleted = 0 "
            + "AND (d.created_by = #{user.userId} "
            + "OR s.owner_id = #{user.userId} "
            + "OR EXISTS ("
            + "SELECT 1 FROM kb_document_acl a "
            + "WHERE a.tenant_id = d.tenant_id "
            + "AND a.document_id = d.id "
            + "AND a.permission IN ('READ', 'MANAGE') "
            + "AND (a.user_id = #{user.userId} "
            + "OR (#{user.departmentId} IS NOT NULL AND a.department_id = #{user.departmentId}) "
            + "OR EXISTS (SELECT 1 FROM sys_user_role ur "
            + "WHERE ur.tenant_id = a.tenant_id "
            + "AND ur.user_id = #{user.userId} "
            + "AND ur.role_id = a.role_id))) "
            + "OR EXISTS ("
            + "SELECT 1 FROM kb_space_acl sa "
            + "WHERE sa.tenant_id = d.tenant_id "
            + "AND sa.space_id = d.space_id "
            + "AND sa.permission IN ('READ', 'MANAGE') "
            + "AND (sa.user_id = #{user.userId} "
            + "OR (#{user.departmentId} IS NOT NULL AND sa.department_id = #{user.departmentId}) "
            + "OR EXISTS (SELECT 1 FROM sys_user_role ur "
            + "WHERE ur.tenant_id = sa.tenant_id "
            + "AND ur.user_id = #{user.userId} "
            + "AND ur.role_id = sa.role_id))))")
    long countReadable(@Param("user") CurrentUser user, @Param("documentId") Long documentId);

    /** 判断当前用户是否可管理指定文档。 */
    @Select("SELECT COUNT(1) "
            + "FROM kb_document d "
            + "JOIN kb_space s "
            + "ON s.tenant_id = d.tenant_id "
            + "AND s.id = d.space_id "
            + "AND s.deleted = 0 "
            + "WHERE d.tenant_id = #{user.tenantId} "
            + "AND d.id = #{documentId} "
            + "AND d.deleted = 0 "
            + "AND (d.created_by = #{user.userId} "
            + "OR s.owner_id = #{user.userId} "
            + "OR EXISTS ("
            + "SELECT 1 FROM kb_document_acl a "
            + "WHERE a.tenant_id = d.tenant_id "
            + "AND a.document_id = d.id "
            + "AND a.permission = 'MANAGE' "
            + "AND (a.user_id = #{user.userId} "
            + "OR (#{user.departmentId} IS NOT NULL AND a.department_id = #{user.departmentId}) "
            + "OR EXISTS (SELECT 1 FROM sys_user_role ur "
            + "WHERE ur.tenant_id = a.tenant_id "
            + "AND ur.user_id = #{user.userId} "
            + "AND ur.role_id = a.role_id))) "
            + "OR EXISTS ("
            + "SELECT 1 FROM kb_space_acl sa "
            + "WHERE sa.tenant_id = d.tenant_id "
            + "AND sa.space_id = d.space_id "
            + "AND sa.permission = 'MANAGE' "
            + "AND (sa.user_id = #{user.userId} "
            + "OR (#{user.departmentId} IS NOT NULL AND sa.department_id = #{user.departmentId}) "
            + "OR EXISTS (SELECT 1 FROM sys_user_role ur "
            + "WHERE ur.tenant_id = sa.tenant_id "
            + "AND ur.user_id = #{user.userId} "
            + "AND ur.role_id = sa.role_id))))")
    long countManageable(@Param("user") CurrentUser user, @Param("documentId") Long documentId);
}
