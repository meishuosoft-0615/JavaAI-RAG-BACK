package com.meishuosoft.rag.kb.controller;

import com.meishuosoft.rag.auth.model.CurrentUser;
import com.meishuosoft.rag.auth.security.RequirePermission;
import com.meishuosoft.rag.common.api.ApiResponse;
import com.meishuosoft.rag.common.api.PageResult;
import com.meishuosoft.rag.common.web.RequestIdFilter;
import com.meishuosoft.rag.kb.dto.CreateSpaceRequest;
import com.meishuosoft.rag.kb.dto.SpaceAclResponse;
import com.meishuosoft.rag.kb.dto.SpaceResponse;
import com.meishuosoft.rag.kb.dto.UpdateSpaceAclRequest;
import com.meishuosoft.rag.kb.dto.UpdateSpaceStatusRequest;
import com.meishuosoft.rag.kb.service.KnowledgeBaseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 知识库空间接口。
 *
 * <p>空间是文档、索引、问答的业务边界，也是权限控制的第一层边界。</p>
 */
@Validated
@RestController
@RequestMapping("/api/kb/spaces")
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    public KnowledgeBaseController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @PostMapping
    @RequirePermission("kb:manage")
    public ApiResponse<SpaceResponse> createSpace(
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody CreateSpaceRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(knowledgeBaseService.createSpace(currentUser, request), requestId(httpRequest));
    }

    @GetMapping
    public ApiResponse<PageResult<SpaceResponse>> listSpaces(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(
                knowledgeBaseService.listVisibleSpaces(currentUser, keyword, pageNo, pageSize),
                requestId(httpRequest)
        );
    }

    @GetMapping("/available")
    public ApiResponse<List<SpaceResponse>> listAvailableSpaces(
            @AuthenticationPrincipal CurrentUser currentUser,
            HttpServletRequest httpRequest
    ) {
        PageResult<SpaceResponse> page = knowledgeBaseService.listVisibleSpaces(currentUser, null, 1, 100);
        return ApiResponse.ok(page.getItems(), requestId(httpRequest));
    }

    @GetMapping("/{spaceId}")
    public ApiResponse<SpaceResponse> getSpace(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long spaceId,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(knowledgeBaseService.getVisibleSpace(currentUser, spaceId), requestId(httpRequest));
    }

    @PatchMapping("/{spaceId}/status")
    @RequirePermission("kb:manage")
    public ApiResponse<SpaceResponse> updateStatus(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long spaceId,
            @Valid @RequestBody UpdateSpaceStatusRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(knowledgeBaseService.updateStatus(currentUser, spaceId, request), requestId(httpRequest));
    }

    @GetMapping("/{spaceId}/acl")
    @RequirePermission("kb:manage")
    public ApiResponse<List<SpaceAclResponse>> listAcl(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long spaceId,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(knowledgeBaseService.listAcl(currentUser, spaceId), requestId(httpRequest));
    }

    @PutMapping("/{spaceId}/acl")
    @RequirePermission("kb:manage")
    public ApiResponse<List<SpaceAclResponse>> replaceAcl(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long spaceId,
            @Valid @RequestBody UpdateSpaceAclRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(knowledgeBaseService.replaceAcl(currentUser, spaceId, request), requestId(httpRequest));
    }

    private String requestId(HttpServletRequest request) {
        Object requestId = request.getAttribute(RequestIdFilter.REQUEST_ID_ATTRIBUTE);
        return requestId == null ? null : requestId.toString();
    }
}
