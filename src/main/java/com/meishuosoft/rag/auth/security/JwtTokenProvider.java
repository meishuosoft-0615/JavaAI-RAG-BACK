package com.meishuosoft.rag.auth.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meishuosoft.rag.auth.model.CurrentUser;
import com.meishuosoft.rag.common.exception.BusinessException;
import com.meishuosoft.rag.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * JWT 令牌签发与解析组件。
 *
 * <p>当前实现使用 HS256 签名，令牌中只放权限过滤需要的最小上下文：
 * tenantId、userId、departmentId、roleIds。</p>
 */
@Component
public class JwtTokenProvider {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    public JwtTokenProvider(JwtProperties jwtProperties, ObjectMapper objectMapper) {
        this.jwtProperties = jwtProperties;
        this.objectMapper = objectMapper;
    }

    public TokenIssueResult issue(CurrentUser user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(jwtProperties.getJwtExpireMinutes() * 60);

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", String.valueOf(user.getUserId()));
        payload.put("tenantId", user.getTenantId());
        payload.put("username", user.getUsername());
        payload.put("departmentId", user.getDepartmentId());
        payload.put("roleIds", user.getRoleIds());
        payload.put("iat", issuedAt.getEpochSecond());
        payload.put("exp", expiresAt.getEpochSecond());

        String headerPart = encodeJson(header);
        String payloadPart = encodeJson(payload);
        String signature = sign(headerPart + "." + payloadPart);
        return new TokenIssueResult(headerPart + "." + payloadPart + "." + signature, expiresAt);
    }

    public CurrentUser parse(String token) {
        String[] parts = token == null ? new String[0] : token.split("\\.");
        if (parts.length != 3) {
            throw unauthorized("Invalid token");
        }

        String expectedSignature = sign(parts[0] + "." + parts[1]);
        if (!constantTimeEquals(expectedSignature, parts[2])) {
            throw unauthorized("Invalid token signature");
        }

        Map<String, Object> payload = decodeJson(parts[1]);
        long expiresAt = asLong(payload.get("exp"));
        if (Instant.now().getEpochSecond() >= expiresAt) {
            throw unauthorized("Token expired");
        }

        return new CurrentUser(
                Long.valueOf(String.valueOf(payload.get("sub"))),
                asLong(payload.get("tenantId")),
                String.valueOf(payload.get("username")),
                asNullableLong(payload.get("departmentId")),
                asLongSet(payload.get("roleIds"))
        );
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to encode token", exception);
        }
    }

    private Map<String, Object> decodeJson(String value) {
        try {
            byte[] json = BASE64_URL_DECODER.decode(value);
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception exception) {
            throw unauthorized("Invalid token payload");
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret().getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return BASE64_URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to sign token", exception);
        }
    }

    private String secret() {
        String secret = jwtProperties.getJwtSecret();
        if (secret == null || secret.trim().isEmpty()) {
            return "local-dev-secret-change-me-local-dev-secret";
        }
        return secret;
    }

    private boolean constantTimeEquals(String left, String right) {
        byte[] leftBytes = left.getBytes(StandardCharsets.UTF_8);
        byte[] rightBytes = right.getBytes(StandardCharsets.UTF_8);
        if (leftBytes.length != rightBytes.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < leftBytes.length; i++) {
            result |= leftBytes[i] ^ rightBytes[i];
        }
        return result == 0;
    }

    private long asLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private Long asNullableLong(Object value) {
        if (value == null) {
            return null;
        }
        return asLong(value);
    }

    private Set<Long> asLongSet(Object value) {
        Set<Long> result = new HashSet<>();
        if (value instanceof Collection<?>) {
            for (Object item : (Collection<?>) value) {
                result.add(asLong(item));
            }
        }
        return result;
    }

    private BusinessException unauthorized(String message) {
        return new BusinessException(ErrorCode.UNAUTHORIZED, message);
    }

    public static class TokenIssueResult {
        private final String token;
        private final Instant expiresAt;

        public TokenIssueResult(String token, Instant expiresAt) {
            this.token = token;
            this.expiresAt = expiresAt;
        }

        public String getToken() {
            return token;
        }

        public Instant getExpiresAt() {
            return expiresAt;
        }
    }
}
