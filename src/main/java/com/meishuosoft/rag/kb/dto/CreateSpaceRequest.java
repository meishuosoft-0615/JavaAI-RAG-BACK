package com.meishuosoft.rag.kb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建知识库空间请求。
 */
public class CreateSpaceRequest {

    /** 知识库空间名称，如“HR 制度知识库”。 */
    @NotBlank
    @Size(max = 128)
    private String name;

    /** 知识库空间说明，用于描述文档范围和业务用途。 */
    @Size(max = 512)
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
