package com.meishuosoft.rag.auth.mapper;

import com.meishuosoft.rag.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户数据访问接口。
 */
@Mapper
public interface SysUserMapper {

    /** 在指定租户内按用户名查询未删除用户。 */
    @Select("SELECT id, tenant_id, username, password_hash, real_name, department_id, status, "
            + "last_login_at, created_at, updated_at, deleted "
            + "FROM sys_user "
            + "WHERE tenant_id = #{tenantId} "
            + "AND username = #{username} "
            + "AND deleted = 0 "
            + "LIMIT 1")
    SysUser findByTenantAndUsername(@Param("tenantId") Long tenantId, @Param("username") String username);

    /** 更新最近登录时间。 */
    @Update("UPDATE sys_user "
            + "SET last_login_at = NOW() "
            + "WHERE tenant_id = #{tenantId} "
            + "AND id = #{userId}")
    int updateLastLoginAt(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /** 判断指定用户是否仍为启用状态，用于 token 已签发后的二次校验。 */
    @Select("SELECT COUNT(1) "
            + "FROM sys_user "
            + "WHERE tenant_id = #{tenantId} "
            + "AND id = #{userId} "
            + "AND status = 'ENABLED' "
            + "AND deleted = 0")
    long countEnabledById(@Param("tenantId") Long tenantId, @Param("userId") Long userId);
}
