package com.meishuosoft.rag.auth.service.impl;

import com.meishuosoft.rag.auth.mapper.SysPermissionMapper;
import com.meishuosoft.rag.auth.model.CurrentUser;
import com.meishuosoft.rag.auth.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * 基于数据库角色权限关系的系统功能权限服务。
 */
@Service
public class DatabasePermissionService implements PermissionService {

    private final SysPermissionMapper permissionMapper;

    public DatabasePermissionService(SysPermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public Set<String> listUserPermissions(CurrentUser currentUser) {
        if (currentUser == null || currentUser.getTenantId() == null || currentUser.getUserId() == null) {
            return Collections.emptySet();
        }
        return permissionMapper.listPermissionCodes(currentUser.getTenantId(), currentUser.getUserId());
    }

    @Override
    public boolean hasPermission(CurrentUser currentUser, String permissionCode) {
        if (permissionCode == null || permissionCode.trim().isEmpty()) {
            return false;
        }
        return listUserPermissions(currentUser).contains(permissionCode);
    }

    @Override
    public boolean hasAnyPermission(CurrentUser currentUser, Collection<String> permissionCodes) {
        if (permissionCodes == null || permissionCodes.isEmpty()) {
            return false;
        }
        Set<String> userPermissions = listUserPermissions(currentUser);
        for (String permissionCode : permissionCodes) {
            if (userPermissions.contains(permissionCode)) {
                return true;
            }
        }
        return false;
    }
}
