package com.wilaya.affectation_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakRealmRoleConverterTest {

    private final KeycloakRealmRoleConverter converter = new KeycloakRealmRoleConverter();

    @Test
    void convert_devraitExtraireRolesEtAjouterPrefixeRole_quandRealmAccessExiste() {
        // Given
        Map<String, Object> realmAccess = Map.of("roles", List.of("AGENT", "SUPERVISEUR"));
        Map<String, Object> claims = Map.of("realm_access", realmAccess);

        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                claims
        );

        // When
        AbstractAuthenticationToken authenticationToken = converter.convert(jwt);

        // Then
        assertThat(authenticationToken).isNotNull();
        Collection<GrantedAuthority> authorities = authenticationToken.getAuthorities();

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_AGENT", "ROLE_SUPERVISEUR");
    }

    @Test
    void convert_devraitRetournerAuthoritiesVides_quandRealmAccessEstNul() {
        // Given - Utilisation d'un claim non vide car Spring Security interdit une map de claims vide
        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "user-id")
        );

        // When
        AbstractAuthenticationToken authenticationToken = converter.convert(jwt);

        // Then
        assertThat(authenticationToken).isNotNull();
        assertThat(authenticationToken.getAuthorities()).isEmpty();
    }

    @Test
    void convert_devraitRetournerAuthoritiesVides_quandRolesSontNuls() {
        // Given
        Map<String, Object> realmAccess = Map.of(); // Pas de clé "roles"
        Map<String, Object> claims = Map.of("realm_access", realmAccess);

        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                claims
        );

        // When
        AbstractAuthenticationToken authenticationToken = converter.convert(jwt);

        // Then
        assertThat(authenticationToken).isNotNull();
        assertThat(authenticationToken.getAuthorities()).isEmpty();
    }
}