package com.wilaya.ressource_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakRealmRoleConverterTest {

    private final KeycloakRealmRoleConverter converter = new KeycloakRealmRoleConverter();

    @Test
    void shouldConvertWithRealmRoles() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "read write")
                .claim("realm_access", Map.of("roles", List.of("admin", "user")))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities).contains(
                new SimpleGrantedAuthority("SCOPE_read"),
                new SimpleGrantedAuthority("SCOPE_write"),
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
        );
    }

    @Test
    void shouldConvertWithoutRealmRoles() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "read")
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities).containsExactly(new SimpleGrantedAuthority("SCOPE_read"));
    }

    @Test
    void shouldConvertWithEmptyRealmRoles() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "read")
                .claim("realm_access", Map.of("roles", List.of()))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities).containsExactly(new SimpleGrantedAuthority("SCOPE_read"));
    }

    @Test
    void shouldConvertWithNullRealmAccessRoles() {
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", null);
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "read")
                .claim("realm_access", realmAccess)
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities).containsExactly(new SimpleGrantedAuthority("SCOPE_read"));
    }

    @Test
    void shouldConvertWithNullRealmAccess() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "read")
                .claim("realm_access", null)
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities).containsExactly(new SimpleGrantedAuthority("SCOPE_read"));
    }
}