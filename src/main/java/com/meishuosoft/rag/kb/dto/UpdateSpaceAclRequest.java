package com.meishuosoft.rag.kb.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 替换知识库空间 ACL 请求。
 *
 * <p>提交后会整体替换空间 ACL，但空间负责人会被服务端强制保留 MANAGE 权限。</p>
 */
public class UpdateSpaceAclRequest {

    /** 新的 ACL 授权项列表。 */
    @Valid
    @NotNull
    @Size(max = 200)
    private List<SpaceAclItemRequest> items;

    public List<SpaceAclItemRequest> getItems() {
        return items;
    }

    public void setItems(List<SpaceAclItemRequest> items) {
        this.items = items;
    }
}
