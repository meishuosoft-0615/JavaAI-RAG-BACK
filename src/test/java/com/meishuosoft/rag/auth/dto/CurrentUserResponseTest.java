package com.meishuosoft.rag.auth.dto;

import com.meishuosoft.rag.auth.model.CurrentUser;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentUserResponseTest {

    @Test
    void fromIncludesFrontendPermissionCodes() {
        CurrentUser currentUser = new CurrentUser(1L, 10L, "admin", 100L, Set.of(2L, 1L));
        Set<String> permissions = Set.of("kb:manage", "document:manage", "audit:read");

        CurrentUserResponse response = CurrentUserResponse.from(currentUser, permissions);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getTenantId()).isEqualTo(10L);
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getDepartmentId()).isEqualTo(100L);
        assertThat(response.getRoleIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(response.getPermissions()).containsExactlyInAnyOrder(
                "kb:manage",
                "document:manage",
                "audit:read"
        );
    }
}
