package com.meishuosoft.rag.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 文档配置。
 *
 * <p>这里声明 Bearer JWT 认证方案，Swagger UI 页面右上角 Authorize 输入 token 后，
 * 可以直接调试需要登录的业务接口。</p>
 */
@Configuration
public class OpenApiConfig {

    /** Swagger 中展示的 Bearer Token 安全方案名称。 */
    public static final String BEARER_AUTH_SCHEME = "bearerAuth";

    /**
     * 构建 OpenAPI 文档基础信息和全局 JWT 认证说明。
     */
    @Bean
    public OpenAPI enterpriseRagOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("企业 RAG 智能知识库平台后端接口")
                        .version("0.0.1")
                        .description("权限安全的企业 RAG 知识库平台 API 文档")
                        .contact(new Contact().name("meishuosoft")))
                .components(new Components().addSecuritySchemes(
                        BEARER_AUTH_SCHEME,
                        new SecurityScheme()
                                .name(BEARER_AUTH_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH_SCHEME));
    }
}
