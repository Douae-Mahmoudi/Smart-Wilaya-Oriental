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
        Map<String, Object> realmAccess = Map.of("roles", List.of("AGENT", "SUPERVISEUR"));
        Map<String, Object> claims = Map.of("realm_access", realmAccess);

        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                claims
        );


        AbstractAuthenticationToken authenticationToken = converter.convert(jwt);

        assertThat(authenticationToken).isNotNull();
        Collection<GrantedAuthority> authorities = authenticationToken.getAuthorities();

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_AGENT", "ROLE_SUPERVISEUR");
    }

    @Test
    void convert_devraitRetournerAuthoritiesVides_quandRealmAccessEstNul() {
        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "user-id")
        );

        AbstractAuthenticationToken authenticationToken = converter.convert(jwt);

        assertThat(authenticationToken).isNotNull();
        assertThat(authenticationToken.getAuthorities()).isEmpty();
    }

    @Test
    void convert_devraitRetournerAuthoritiesVides_quandRolesSontNuls() {
        Map<String, Object> realmAccess = Map.of();
        Map<String, Object> claims = Map.of("realm_access", realmAccess);

        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                claims
        );

        AbstractAuthenticationToken authenticationToken = converter.convert(jwt);

        assertThat(authenticationToken).isNotNull();
        assertThat(authenticationToken.getAuthorities()).isEmpty();
    }
}