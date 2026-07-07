package com.wilaya.utilisateur_service.infrastructure.identite;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class KeycloakAdminAdapterTest {

    private KeycloakAdminAdapter adapter;
    private Keycloak keycloakMock;
    private RealmResource realmResourceMock;
    private UsersResource usersResourceMock;
    private UserResource userResourceMock;

    private static final String REALM = "wilaya";
    private static final String PUBLIC_CLIENT_ID = "frontend-app";
    private static final String SERVER_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() throws Exception {
        adapter = new KeycloakAdminAdapter(SERVER_URL, REALM, "admin-client", "secret", PUBLIC_CLIENT_ID);

        keycloakMock = mock(Keycloak.class);
        realmResourceMock = mock(RealmResource.class);
        usersResourceMock = mock(UsersResource.class);
        userResourceMock = mock(UserResource.class);

        when(keycloakMock.realm(REALM)).thenReturn(realmResourceMock);
        when(realmResourceMock.users()).thenReturn(usersResourceMock);

        Field field = KeycloakAdminAdapter.class.getDeclaredField("keycloak");
        field.setAccessible(true);
        field.set(adapter, keycloakMock);
    }

    @Test
    void creerCompteRenvoieLIdKeycloakExtraitDeLaLocation() {
        UUID idAttendu = UUID.randomUUID();
        Response response = Response.created(URI.create("http://localhost:8080/admin/realms/wilaya/users/" + idAttendu)).build();
        when(usersResourceMock.create(any(UserRepresentation.class))).thenReturn(response);
        when(usersResourceMock.get(idAttendu.toString())).thenReturn(userResourceMock);

        RolesResource rolesResourceMock = mock(RolesResource.class);
        RoleResource roleResourceMock = mock(RoleResource.class);
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        when(realmResourceMock.roles()).thenReturn(rolesResourceMock);
        when(rolesResourceMock.get("AGENT")).thenReturn(roleResourceMock);
        when(roleResourceMock.toRepresentation()).thenReturn(roleRepresentation);

        RoleMappingResource roleMappingResourceMock = mock(RoleMappingResource.class);
        RoleScopeResource roleScopeResourceMock = mock(RoleScopeResource.class);
        when(userResourceMock.roles()).thenReturn(roleMappingResourceMock);
        when(roleMappingResourceMock.realmLevel()).thenReturn(roleScopeResourceMock);

        UUID resultat = adapter.creerCompte("karim@example.com", "Benali", "Karim", "MotDePasseTemp123", "AGENT");

        assertThat(resultat).isEqualTo(idAttendu);
    }

    @Test
    void creerCompteEnvoieUnUserRepresentationAvecLesBonsChamps() {
        UUID idAttendu = UUID.randomUUID();
        Response response = Response.created(URI.create("http://localhost:8080/admin/realms/wilaya/users/" + idAttendu)).build();
        when(usersResourceMock.create(any(UserRepresentation.class))).thenReturn(response);
        when(usersResourceMock.get(idAttendu.toString())).thenReturn(userResourceMock);

        RolesResource rolesResourceMock = mock(RolesResource.class);
        RoleResource roleResourceMock = mock(RoleResource.class);
        when(realmResourceMock.roles()).thenReturn(rolesResourceMock);
        when(rolesResourceMock.get("AGENT")).thenReturn(roleResourceMock);
        when(roleResourceMock.toRepresentation()).thenReturn(new RoleRepresentation());

        RoleMappingResource roleMappingResourceMock = mock(RoleMappingResource.class);
        when(userResourceMock.roles()).thenReturn(roleMappingResourceMock);
        when(roleMappingResourceMock.realmLevel()).thenReturn(mock(RoleScopeResource.class));

        ArgumentCaptor<UserRepresentation> captor = ArgumentCaptor.forClass(UserRepresentation.class);

        adapter.creerCompte("karim@example.com", "Benali", "Karim", "MotDePasseTemp123", "AGENT");

        verify(usersResourceMock).create(captor.capture());
        UserRepresentation user = captor.getValue();
        assertThat(user.getUsername()).isEqualTo("karim@example.com");
        assertThat(user.getEmail()).isEqualTo("karim@example.com");
        assertThat(user.getFirstName()).isEqualTo("Karim");
        assertThat(user.getLastName()).isEqualTo("Benali");
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.isEmailVerified()).isTrue();
    }

    @Test
    void creerCompteAvecStatutDifferentDe201LanceIllegalStateException() {
        Response response = Response.status(500).build();
        when(usersResourceMock.create(any(UserRepresentation.class))).thenReturn(response);

        assertThatThrownBy(() -> adapter.creerCompte("karim@example.com", "Benali", "Karim", "MotDePasseTemp123", "AGENT"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void changerMotDePasseAppelleResetPasswordAvecLeBonMotDePasseEtLeBonFlagTemporaire() {
        UUID idKeycloak = UUID.randomUUID();
        when(usersResourceMock.get(idKeycloak.toString())).thenReturn(userResourceMock);
        ArgumentCaptor<CredentialRepresentation> captor = ArgumentCaptor.forClass(CredentialRepresentation.class);

        adapter.changerMotDePasse(idKeycloak, "nouveauMdp123", true);

        verify(userResourceMock).resetPassword(captor.capture());
        assertThat(captor.getValue().getValue()).isEqualTo("nouveauMdp123");
        assertThat(captor.getValue().isTemporary()).isTrue();
    }

    @Test
    void changerMotDePasseAvecTemporaireFalseNeMarquePasLeMotDePasseCommeTemporaire() {
        UUID idKeycloak = UUID.randomUUID();
        when(usersResourceMock.get(idKeycloak.toString())).thenReturn(userResourceMock);
        ArgumentCaptor<CredentialRepresentation> captor = ArgumentCaptor.forClass(CredentialRepresentation.class);

        adapter.changerMotDePasse(idKeycloak, "nouveauMdp123", false);

        verify(userResourceMock).resetPassword(captor.capture());
        assertThat(captor.getValue().isTemporary()).isFalse();
    }

    @Test
    void trouverIdParEmailRenvoieLIdQuandUnUtilisateurEstTrouve() {
        UUID idAttendu = UUID.randomUUID();
        UserRepresentation user = new UserRepresentation();
        user.setId(idAttendu.toString());
        when(usersResourceMock.search("karim@example.com", true)).thenReturn(List.of(user));

        Optional<UUID> resultat = adapter.trouverIdParEmail("karim@example.com");

        assertThat(resultat).contains(idAttendu);
    }

    @Test
    void trouverIdParEmailRenvoieOptionalVideQuandAucunUtilisateurTrouve() {
        when(usersResourceMock.search("inconnu@example.com", true)).thenReturn(List.of());

        Optional<UUID> resultat = adapter.trouverIdParEmail("inconnu@example.com");

        assertThat(resultat).isEmpty();
    }

    @Test
    void verifierAncienMotDePasseRenvoieTrueQuandKeycloakRenvoieUnToken() {
        UUID idKeycloak = UUID.randomUUID();
        UserRepresentation user = new UserRepresentation();
        user.setUsername("karim@example.com");
        when(usersResourceMock.get(idKeycloak.toString())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(user);

        Keycloak keycloakUserMock = mock(Keycloak.class);
        TokenManager tokenManagerMock = mock(TokenManager.class);
        when(keycloakUserMock.tokenManager()).thenReturn(tokenManagerMock);
        when(tokenManagerMock.getAccessToken()).thenReturn(mock(AccessTokenResponse.class));

        KeycloakBuilder builderMock = mock(KeycloakBuilder.class);
        when(builderMock.serverUrl(anyString())).thenReturn(builderMock);
        when(builderMock.realm(anyString())).thenReturn(builderMock);
        when(builderMock.grantType(anyString())).thenReturn(builderMock);
        when(builderMock.clientId(anyString())).thenReturn(builderMock);
        when(builderMock.username(anyString())).thenReturn(builderMock);
        when(builderMock.password(anyString())).thenReturn(builderMock);
        when(builderMock.build()).thenReturn(keycloakUserMock);

        try (MockedStatic<KeycloakBuilder> mockedStatic = mockStatic(KeycloakBuilder.class)) {
            mockedStatic.when(KeycloakBuilder::builder).thenReturn(builderMock);

            boolean resultat = adapter.verifierAncienMotDePasse(idKeycloak, "ancienMdp123");

            assertThat(resultat).isTrue();
        }

        verify(keycloakUserMock).close();
    }

    @Test
    void verifierAncienMotDePasseRenvoieFalseQuandKeycloakLanceUneException() {
        UUID idKeycloak = UUID.randomUUID();
        UserRepresentation user = new UserRepresentation();
        user.setUsername("karim@example.com");
        when(usersResourceMock.get(idKeycloak.toString())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(user);

        Keycloak keycloakUserMock = mock(Keycloak.class);
        TokenManager tokenManagerMock = mock(TokenManager.class);
        when(keycloakUserMock.tokenManager()).thenReturn(tokenManagerMock);
        when(tokenManagerMock.getAccessToken()).thenThrow(new RuntimeException("401 Unauthorized"));

        KeycloakBuilder builderMock = mock(KeycloakBuilder.class);
        when(builderMock.serverUrl(anyString())).thenReturn(builderMock);
        when(builderMock.realm(anyString())).thenReturn(builderMock);
        when(builderMock.grantType(anyString())).thenReturn(builderMock);
        when(builderMock.clientId(anyString())).thenReturn(builderMock);
        when(builderMock.username(anyString())).thenReturn(builderMock);
        when(builderMock.password(anyString())).thenReturn(builderMock);
        when(builderMock.build()).thenReturn(keycloakUserMock);

        try (MockedStatic<KeycloakBuilder> mockedStatic = mockStatic(KeycloakBuilder.class)) {
            mockedStatic.when(KeycloakBuilder::builder).thenReturn(builderMock);

            boolean resultat = adapter.verifierAncienMotDePasse(idKeycloak, "mauvaisMdp");

            assertThat(resultat).isFalse();
        }

        verify(keycloakUserMock).close();
    }
}





















