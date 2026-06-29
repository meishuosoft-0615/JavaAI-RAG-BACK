package com.meishuosoft.rag.kb.mapper;

import com.meishuosoft.rag.auth.model.CurrentUser;
import com.meishuosoft.rag.kb.entity.KbSpace;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 知识库空间数据访问接口。
 */
@Mapper
public interface KbSpaceMapper {

    /** 可见空间 ACL 条件：用户、部门或角色命中 READ/MANAGE 即可访问。 */
    String VISIBLE_ACL_EXISTS_SQL = "SELECT 1 FROM kb_space_acl a "
            + "WHERE a.tenant_id = s.tenant_id "
            + "AND a.space_id = s.id "
            + "AND a.permission IN ('READ', 'MANAGE') "
            + "AND (a.user_id = #{user.userId} "
            + "OR (#{user.departmentId} IS NOT NULL AND a.department_id = #{user.departmentId}) "
            + "OR EXISTS (SELECT 1 FROM sys_user_role ur "
            + "WHERE ur.tenant_id = a.tenant_id "
            + "AND ur.user_id = #{user.userId} "
            + "AND ur.role_id = a.role_id))";

    /** 可管理空间 ACL 条件：用户、部门或角色命中 MANAGE 才可管理。 */
    String MANAGEABLE_ACL_EXISTS_SQL = "SELECT 1 FROM kb_space_acl a "
            + "WHERE a.tenant_id = s.tenant_id "
            + "AND a.space_id = s.id "
            + "AND a.permission = 'MANAGE' "
            + "AND (a.user_id = #{user.userId} "
            + "OR (#{user.departmentId} IS NOT NULL AND a.department_id = #{user.departmentId}) "
            + "OR EXISTS (SELECT 1 FROM sys_user_role ur "
            + "WHERE ur.tenant_id = a.tenant_id "
            + "AND ur.user_id = #{user.userId} "
            + "AND ur.role_id = a.role_id))";

    /** 新增知识库空间。 */
    @Insert("INSERT INTO kb_space (tenant_id, name, description, owner_id, status, created_by) "
            + "VALUES (#{tenantId}, #{name}, #{description}, #{ownerId}, #{status}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(KbSpace space);

    /** 按租户和空间 ID 查询未删除空间。 */
    @Select("SELECT id, tenant_id, name, description, owner_id, status, created_by, created_at, updated_at, deleted "
            + "FROM kb_space "
            + "WHERE tenant_id = #{tenantId} "
            + "AND id = #{spaceId} "
            + "AND deleted = 0 "
            + "LIMIT 1")
    KbSpace findById(@Param("tenantId") Long tenantId, @Param("spaceId") Long spaceId);

    /** 统计当前用户可见的空间数量。 */
    @Select("SELECT COUNT(1) "
            + "FROM kb_space s "
            + "WHERE s.tenant_id = #{user.tenantId} "
            + "AND s.deleted = 0 "
            + "AND (#{keyword} IS NULL OR #{keyword} = '' OR s.name LIKE CONCAT('%', #{keyword}, '%')) "
            + "AND (s.owner_id = #{user.userId} OR EXISTS ("
            + VISIBLE_ACL_EXISTS_SQL
            + "))")
    long countVisible(@Param("user") CurrentUser user, @Param("keyword") String keyword);

    /** 分页查询当前用户可见的空间列表。 */
    @Select("SELECT s.id, s.tenant_id, s.name, s.description, s.owner_id, s.status, s.created_by, s.created_at, s.updated_at, s.deleted "
            + "FROM kb_space s "
            + "WHERE s.tenant_id = #{user.tenantId} "
            + "AND s.deleted = 0 "
            + "AND (#{keyword} IS NULL OR #{keyword} = '' OR s.name LIKE CONCAT('%', #{keyword}, '%')) "
            + "AND (s.owner_id = #{user.userId} OR EXISTS ("
            + VISIBLE_ACL_EXISTS_SQL
            + ")) "
            + "ORDER BY s.updated_at DESC, s.id DESC "
            + "LIMIT #{limit} OFFSET #{offset}")
    List<KbSpace> listVisible(
            @Param("user") CurrentUser user,
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    /** 查询当前用户可见的空间详情。 */
    @Select("SELECT s.id, s.tenant_id, s.name, s.description, s.owner_id, s.status, s.created_by, s.created_at, s.updated_at, s.deleted "
            + "FROM kb_space s "
            + "WHERE s.tenant_id = #{user.tenantId} "
            + "AND s.id = #{spaceId} "
            + "AND s.deleted = 0 "
            + "AND (s.owner_id = #{user.userId} OR EXISTS ("
            + VISIBLE_ACL_EXISTS_SQL
            + ")) "
            + "LIMIT 1")
    KbSpace findVisibleById(@Param("user") CurrentUser user, @Param("spaceId") Long spaceId);

    /** 判断当前用户是否拥有空间管理权限。 */
    @Select("SELECT COUNT(1) "
            + "FROM kb_space s "
            + "WHERE s.tenant_id = #{user.tenantId} "
            + "AND s.id = #{spaceId} "
            + "AND s.deleted = 0 "
            + "AND (s.owner_id = #{user.userId} OR EXISTS ("
            + MANAGEABLE_ACL_EXISTS_SQL
            + "))")
    long countManageable(@Param("user") CurrentUser user, @Param("spaceId") Long spaceId);

    /** 更新知识库空间状态。 */
    @Update("UPDATE kb_space "
            + "SET status = #{status}, updated_at = NOW() "
            + "WHERE tenant_id = #{tenantId} "
            + "AND id = #{spaceId} "
            + "AND deleted = 0")
    int updateStatus(@Param("tenantId") Long tenantId, @Param("spaceId") Long spaceId, @Param("status") String status);

}
