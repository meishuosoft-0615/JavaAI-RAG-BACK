package com.meishuosoft.rag.kb.service;

import com.meishuosoft.rag.auth.model.CurrentUser;
import com.meishuosoft.rag.common.api.PageResult;
import com.meishuosoft.rag.common.exception.BusinessException;
import com.meishuosoft.rag.common.exception.ErrorCode;
import com.meishuosoft.rag.kb.dto.CreateSpaceRequest;
import com.meishuosoft.rag.kb.dto.SpaceAclItemRequest;
import com.meishuosoft.rag.kb.dto.SpaceAclResponse;
import com.meishuosoft.rag.kb.dto.SpaceResponse;
import com.meishuosoft.rag.kb.dto.UpdateSpaceAclRequest;
import com.meishuosoft.rag.kb.dto.UpdateSpaceStatusRequest;
import com.meishuosoft.rag.kb.entity.KbSpace;
import com.meishuosoft.rag.kb.entity.KbSpaceAcl;
import com.meishuosoft.rag.kb.mapper.KbSpaceAclMapper;
import com.meishuosoft.rag.kb.mapper.KbSpaceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 知识库空间业务服务。
 *
 * <p>权限规则：
 * 创建者自动成为空间负责人；负责人默认拥有 MANAGE；空间列表只返回负责人或 ACL 命中的空间；
 * 状态修改和 ACL 替换必须具备 MANAGE 权限。</p>
 */
@Service
public class KnowledgeBaseService {

    private static final String ENABLED = "ENABLED";
    private static final String READ = "READ";
    private static final String MANAGE = "MANAGE";

    private final KbSpaceMapper spaceMapper;
    private final KbSpaceAclMapper aclMapper;

    public KnowledgeBaseService(KbSpaceMapper spaceMapper, KbSpaceAclMapper aclMapper) {
        this.spaceMapper = spaceMapper;
        this.aclMapper = aclMapper;
    }

    /**
     * 创建知识库空间。
     *
     * <p>服务端强制使用当前登录用户的 tenantId 和 userId，不接受前端传入租户或负责人。</p>
     */
    @Transactional
    public SpaceResponse createSpace(CurrentUser currentUser, CreateSpaceRequest request) {
        requireUser(currentUser);

        KbSpace space = new KbSpace();
        space.setTenantId(currentUser.getTenantId());
        space.setName(trim(request.getName()));
        space.setDescription(trimToNull(request.getDescription()));
        space.setOwnerId(currentUser.getUserId());
        space.setStatus(ENABLED);
        space.setCreatedBy(currentUser.getUserId());
        spaceMapper.insert(space);

        aclMapper.insert(ownerManageAcl(currentUser, space.getId()));
        return SpaceResponse.from(spaceMapper.findById(currentUser.getTenantId(), space.getId()));
    }

    /**
     * 分页查询当前用户可见的知识库空间。
     */
    public PageResult<SpaceResponse> listVisibleSpaces(CurrentUser currentUser, String keyword, int pageNo, int pageSize) {
        requireUser(currentUser);
        String normalizedKeyword = trimToNull(keyword);
        long total = spaceMapper.countVisible(currentUser, normalizedKeyword);
        int offset = (pageNo - 1) * pageSize;
        List<KbSpace> spaces = spaceMapper.listVisible(currentUser, normalizedKeyword, offset, pageSize);
        return PageResult.of(total, toSpaceResponses(spaces));
    }

    /**
     * 查询当前用户可见的知识库空间详情。
     */
    public SpaceResponse getVisibleSpace(CurrentUser currentUser, Long spaceId) {
        return SpaceResponse.from(requireVisibleSpace(currentUser, spaceId));
    }

    /**
     * 更新空间状态，仅 MANAGE 权限可操作。
     */
    @Transactional
    public SpaceResponse updateStatus(CurrentUser currentUser, Long spaceId, UpdateSpaceStatusRequest request) {
        requireManagePermission(currentUser, spaceId);
        int updated = spaceMapper.updateStatus(currentUser.getTenantId(), spaceId, request.getStatus());
        if (updated == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Knowledge base space not found");
        }
        return SpaceResponse.from(spaceMapper.findById(currentUser.getTenantId(), spaceId));
    }

    /**
     * 查询空间 ACL，仅 MANAGE 权限可查看完整授权列表。
     */
    public List<SpaceAclResponse> listAcl(CurrentUser currentUser, Long spaceId) {
        requireManagePermission(currentUser, spaceId);
        return toAclResponses(aclMapper.listBySpace(currentUser.getTenantId(), spaceId));
    }

    /**
     * 整体替换空间 ACL。
     *
     * <p>为了防止误删导致空间无人可管理，服务端会强制保留当前负责人 MANAGE 权限。</p>
     */
    @Transactional
    public List<SpaceAclResponse> replaceAcl(CurrentUser currentUser, Long spaceId, UpdateSpaceAclRequest request) {
        requireManagePermission(currentUser, spaceId);
        aclMapper.deleteBySpace(currentUser.getTenantId(), spaceId);
        aclMapper.insert(ownerManageAcl(currentUser, spaceId));

        Set<String> dedupe = new HashSet<>();
        dedupe.add("USER:" + currentUser.getUserId() + ":" + MANAGE);
        for (SpaceAclItemRequest item : request.getItems()) {
            KbSpaceAcl acl = toAcl(currentUser, spaceId, item);
            String key = aclKey(acl);
            if (dedupe.add(key)) {
                aclMapper.insert(acl);
            }
        }
        return toAclResponses(aclMapper.listBySpace(currentUser.getTenantId(), spaceId));
    }

    private KbSpace requireVisibleSpace(CurrentUser currentUser, Long spaceId) {
        requireUser(currentUser);
        KbSpace space = spaceMapper.findVisibleById(currentUser, spaceId);
        if (space == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Knowledge base space not found");
        }
        return space;
    }

    private void requireManagePermission(CurrentUser currentUser, Long spaceId) {
        requireUser(currentUser);
        if (spaceMapper.countManageable(currentUser, spaceId) <= 0) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "No permission to manage this knowledge base space");
        }
    }

    private void requireUser(CurrentUser currentUser) {
        if (currentUser == null || currentUser.getTenantId() == null || currentUser.getUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Unauthorized");
        }
    }

    private KbSpaceAcl ownerManageAcl(CurrentUser currentUser, Long spaceId) {
        KbSpaceAcl acl = new KbSpaceAcl();
        acl.setTenantId(currentUser.getTenantId());
        acl.setSpaceId(spaceId);
        acl.setUserId(currentUser.getUserId());
        acl.setPermission(MANAGE);
        acl.setCreatedBy(currentUser.getUserId());
        return acl;
    }

    private KbSpaceAcl toAcl(CurrentUser currentUser, Long spaceId, SpaceAclItemRequest item) {
        int targetCount = countTargets(item);
        if (targetCount != 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Exactly one ACL target is required");
        }

        KbSpaceAcl acl = new KbSpaceAcl();
        acl.setTenantId(currentUser.getTenantId());
        acl.setSpaceId(spaceId);
        acl.setDepartmentId(item.getDepartmentId());
        acl.setRoleId(item.getRoleId());
        acl.setUserId(item.getUserId());
        acl.setPermission(trimToNull(item.getPermission()) == null ? READ : item.getPermission());
        acl.setCreatedBy(currentUser.getUserId());
        return acl;
    }

    private int countTargets(SpaceAclItemRequest item) {
        int count = 0;
        if (item.getDepartmentId() != null) {
            count++;
        }
        if (item.getRoleId() != null) {
            count++;
        }
        if (item.getUserId() != null) {
            count++;
        }
        return count;
    }

    private String aclKey(KbSpaceAcl acl) {
        if (acl.getUserId() != null) {
            return "USER:" + acl.getUserId() + ":" + acl.getPermission();
        }
        if (acl.getRoleId() != null) {
            return "ROLE:" + acl.getRoleId() + ":" + acl.getPermission();
        }
        return "DEPARTMENT:" + acl.getDepartmentId() + ":" + acl.getPermission();
    }

    private List<SpaceResponse> toSpaceResponses(List<KbSpace> spaces) {
        List<SpaceResponse> responses = new ArrayList<>();
        for (KbSpace space : spaces) {
            responses.add(SpaceResponse.from(space));
        }
        return responses;
    }

    private List<SpaceAclResponse> toAclResponses(List<KbSpaceAcl> acls) {
        List<SpaceAclResponse> responses = new ArrayList<>();
        for (KbSpaceAcl acl : acls) {
            responses.add(SpaceAclResponse.from(acl));
        }
        return responses;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String trimToNull(String value) {
        String trimmed = trim(value);
        return trimmed == null || trimmed.isEmpty() ? null : trimmed;
    }
}
