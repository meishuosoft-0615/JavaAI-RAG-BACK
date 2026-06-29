package com.meishuosoft.rag.auth.mapper;

import com.meishuosoft.rag.auth.entity.SysTenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 租户数据访问接口。
 */
@Mapper
public interface SysTenantMapper {

    /** 根据租户编码查询未删除租户。 */
    @Select("SELECT id, name, code, status, created_at, updated_at, deleted "
            + "FROM sys_tenant "
            + "WHERE code = #{code} "
            + "AND deleted = 0 "
            + "LIMIT 1")
    SysTenant findByCode(@Param("code") String code);
}
