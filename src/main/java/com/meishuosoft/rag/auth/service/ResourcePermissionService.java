package com.meishuosoft.rag.auth.service;

import com.meishuosoft.rag.auth.model.CurrentUser;

/**
 * 资源数据权限服务。
 *
 * <p>用于判断当前用户能否读取或管理某个知识库空间、文档和引用来源。</p>
 */
public interface ResourcePermissionService {

    boolean canAccessSpace(CurrentUser currentUser, Long spaceId);

    boolean canManageSpace(CurrentUser currentUser, Long spaceId);

    boolean canReadDocument(CurrentUser currentUser, Long documentId);

    boolean canManageDocument(CurrentUser currentUser, Long documentId);
}
