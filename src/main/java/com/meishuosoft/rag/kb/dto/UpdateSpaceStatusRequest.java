package com.meishuosoft.rag.kb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 更新知识库空间状态请求。
 */
public class UpdateSpaceStatusRequest {

    /** 空间状态：ENABLED 启用，DISABLED 停用。 */
    @NotBlank
    @Pattern(regexp = "ENABLED|DISABLED")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
