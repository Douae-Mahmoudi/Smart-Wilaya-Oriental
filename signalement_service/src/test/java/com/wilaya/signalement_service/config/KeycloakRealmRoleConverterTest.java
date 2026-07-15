package com.wilaya.signalement_service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KeycloakRealmRoleConverterTest {

    private KeycloakRealmRoleConverter converter;

    @BeforeEach
    void setUp() {
        converter = new KeycloakRealmRoleConverter();
    }

    @Test
    void convert_ShouldExtractRealmRolesAndAddRolePrefix() {
        Jwt jwt = mock(Jwt.class);
        Map<String, Object> realmAccess = Map.of("roles", List.of("agent", "superviseur"));

        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);
        when(jwt.getClaim("scope")).thenReturn("");

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities).hasSize(2);
        assertThat(authorities.stream().map(GrantedAuthority::getAuthority))
                .containsExactlyInAnyOrder("ROLE_AGENT", "ROLE_SUPERVISEUR");
    }

    @Test
    void convert_ShouldReturnEmptyWhenRealmAccessIsNull() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("realm_access")).thenReturn(null);

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities).isEmpty();
    }
}