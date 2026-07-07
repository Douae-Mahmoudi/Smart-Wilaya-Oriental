package com.wilaya.utilisateur_service.infrastructure.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class SecurityConfigTest {

    private JwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() throws Exception {
        SecurityConfig securityConfig = new SecurityConfig();
        Method method = SecurityConfig.class.getDeclaredMethod("jwtAuthenticationConverter");
        method.setAccessible(true);
        converter = (JwtAuthenticationConverter) method.invoke(securityConfig);
    }

    private Jwt jwtAvecRealmAccess(Map<String, Object> realmAccessClaim) {
        Jwt.Builder builder = Jwt.withTokenValue("token-de-test")
                .header("alg", "none")
                .subject("11111111-1111-1111-1111-111111111111")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600));

        if (realmAccessClaim != null) {
            builder.claim("realm_access", realmAccessClaim);
        }
        return builder.build();
    }

    @Test
    void convertMappeLesRolesKeycloakEnAuthoritiesPrefixeesRole() {
        Jwt jwt = jwtAvecRealmAccess(Map.of("roles", List.of("ADMIN", "AGENT")));

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_AGENT");
    }

    @Test
    void convertAvecUnSeulRoleRenvoieUneSeuleAuthority() {
        Jwt jwt = jwtAvecRealmAccess(Map.of("roles", List.of("SUPERVISEUR")));

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_SUPERVISEUR");
    }

    @Test
    void convertSansClaimRealmAccessRenvoieAucuneAuthority() {
        Jwt jwt = jwtAvecRealmAccess(null);

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities).isEmpty();
    }

    @Test
    void convertAvecRealmAccessPresentMaisSansRolesRenvoieAucuneAuthority() {
        Jwt jwt = jwtAvecRealmAccess(Map.of("autreCle", "valeur"));

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities).isEmpty();
    }

    @Test
    void convertAvecListeDeRolesVideRenvoieAucuneAuthority() {
        Jwt jwt = jwtAvecRealmAccess(Map.of("roles", List.of()));

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities).isEmpty();
    }
}
