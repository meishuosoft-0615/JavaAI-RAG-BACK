package com.meishuosoft.rag.auth.service.impl;

import com.meishuosoft.rag.auth.model.CurrentUser;
import com.meishuosoft.rag.auth.service.ResourcePermissionService;
import com.meishuosoft.rag.kb.mapper.KbDocumentPermissionMapper;
import com.meishuosoft.rag.kb.mapper.KbSpaceMapper;
import org.springframework.stereotype.Service;

/**
 * 基于数据库 ACL 的资源数据权限服务。
 */
@Service
public class DatabaseResourcePermissionService implements ResourcePermissionService {

    private final KbSpaceMapper spaceMapper;
    private final KbDocumentPermissionMapper documentPermissionMapper;

    public DatabaseResourcePermissionService(
            KbSpaceMapper spaceMapper,
            KbDocumentPermissionMapper documentPermissionMapper
    ) {
        this.spaceMapper = spaceMapper;
        this.documentPermissionMapper = documentPermissionMapper;
    }

    @Override
    public boolean canAccessSpace(CurrentUser currentUser, Long spaceId) {
        if (!valid(currentUser) || spaceId == null) {
            return false;
        }
        return spaceMapper.findVisibleById(currentUser, spaceId) != null;
    }

    @Override
    public boolean canManageSpace(CurrentUser currentUser, Long spaceId) {
        if (!valid(currentUser) || spaceId == null) {
            return false;
        }
        return spaceMapper.countManageable(currentUser, spaceId) > 0;
    }

    @Override
    public boolean canReadDocument(CurrentUser currentUser, Long documentId) {
        if (!valid(currentUser) || documentId == null) {
            return false;
        }
        return documentPermissionMapper.countReadable(currentUser, documentId) > 0;
    }

    @Override
    public boolean canManageDocument(CurrentUser currentUser, Long documentId) {
        if (!valid(currentUser) || documentId == null) {
            return false;
        }
        return documentPermissionMapper.countManageable(currentUser, documentId) > 0;
    }

    private boolean valid(CurrentUser currentUser) {
        return currentUser != null
                && currentUser.getTenantId() != null
                && currentUser.getUserId() != null;
    }
}
