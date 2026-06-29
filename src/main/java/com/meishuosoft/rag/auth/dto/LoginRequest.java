package com.meishuosoft.rag.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 登录请求参数。
 *
 * <p>登录时必须携带租户编码，避免不同租户下同名用户互相混淆。</p>
 */
public class LoginRequest {

    /** 租户编码，对应 sys_tenant.code。 */
    @NotBlank
    @Size(max = 64)
    private String tenantCode;

    /** 用户名，对应 sys_user.username。 */
    @NotBlank
    @Size(max = 64)
    private String username;

    /** 登录密码，服务端只用于校验，不会保存明文。 */
    @NotBlank
    @Size(max = 128)
    private String password;

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
