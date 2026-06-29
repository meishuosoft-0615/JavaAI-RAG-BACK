package com.meishuosoft.rag.kb.mapper;

import com.meishuosoft.rag.kb.entity.KbSpaceAcl;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 知识库空间 ACL 数据访问接口。
 */
@Mapper
public interface KbSpaceAclMapper {

    /** 新增空间授权记录。 */
    @Insert("INSERT INTO kb_space_acl (tenant_id, space_id, department_id, role_id, user_id, permission, created_by) "
            + "VALUES (#{tenantId}, #{spaceId}, #{departmentId}, #{roleId}, #{userId}, #{permission}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(KbSpaceAcl acl);

    /** 删除指定空间的全部授权记录，用于 ACL 整体替换。 */
    @Delete("DELETE FROM kb_space_acl "
            + "WHERE tenant_id = #{tenantId} "
            + "AND space_id = #{spaceId}")
    int deleteBySpace(@Param("tenantId") Long tenantId, @Param("spaceId") Long spaceId);

    /** 查询指定空间的授权列表。 */
    @Select("SELECT id, tenant_id, space_id, department_id, role_id, user_id, permission, created_by, created_at "
            + "FROM kb_space_acl "
            + "WHERE tenant_id = #{tenantId} "
            + "AND space_id = #{spaceId} "
            + "ORDER BY permission DESC, id ASC")
    List<KbSpaceAcl> listBySpace(@Param("tenantId") Long tenantId, @Param("spaceId") Long spaceId);
}
