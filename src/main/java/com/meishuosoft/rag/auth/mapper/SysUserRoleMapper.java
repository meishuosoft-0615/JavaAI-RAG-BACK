package com.meishuosoft.rag.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关系数据访问接口。
 */
@Mapper
public interface SysUserRoleMapper {

    /** 查询用户在租户内拥有的角色 ID 列表。 */
    @Select("SELECT role_id "
            + "FROM sys_user_role "
            + "WHERE tenant_id = #{tenantId} "
            + "AND user_id = #{userId} "
            + "ORDER BY role_id ASC")
    List<Long> findRoleIds(@Param("tenantId") Long tenantId, @Param("userId") Long userId);
}
