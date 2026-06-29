package com.meishuosoft.rag.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 配置项，对应 rag.security 配置前缀。
 */
@ConfigurationProperties(prefix = "rag.security")
public class JwtProperties {

    /** JWT 签名密钥，生产环境必须通过环境变量 JWT_SECRET 覆盖默认值。 */
    private String jwtSecret = "local-dev-secret-change-me-local-dev-secret";

    /** JWT 有效期，单位分钟。 */
    private long jwtExpireMinutes = 120;

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public long getJwtExpireMinutes() {
        return jwtExpireMinutes;
    }

    public void setJwtExpireMinutes(long jwtExpireMinutes) {
        this.jwtExpireMinutes = jwtExpireMinutes;
    }
}
