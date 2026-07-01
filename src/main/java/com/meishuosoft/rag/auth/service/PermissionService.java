package com.meishuosoft.rag.auth.service;

import com.meishuosoft.rag.auth.model.CurrentUser;

import java.util.Collection;
import java.util.Set;

/**
 * 系统功能权限服务。
 *
 * <p>用于查询用户拥有的权限码，并为方法级接口鉴权提供统一判断入口。</p>
 */
public interface PermissionService {

    /**
     * 查询当前用户拥有的系统功能权限码。
     */
    Set<String> listUserPermissions(CurrentUser currentUser);

    /**
     * 判断当前用户是否拥有指定权限码。
     */
    boolean hasPermission(CurrentUser currentUser, String permissionCode);

    /**
     * 判断当前用户是否拥有任意一个指定权限码。
     */
    boolean hasAnyPermission(CurrentUser currentUser, Collection<String> permissionCodes);
}
