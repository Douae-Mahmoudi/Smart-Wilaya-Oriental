package com.wilaya.utilisateur_service.infrastructure.identite;

import com.wilaya.utilisateur_service.domain.port.out.IdentiteProviderPort;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
public class KeycloakAdminAdapter implements IdentiteProviderPort {

    private final Keycloak keycloak;
    private final String realm;
    private final String serverUrl;
    private final String publicClientId;

    public KeycloakAdminAdapter(
            @Value("${keycloak.server-url}") String serverUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.admin-client-id}") String clientId,
            @Value("${keycloak.admin-client-secret}") String clientSecret,
            @Value("${keycloak.public-client-id}") String publicClientId) {

        this.realm = realm;
        this.serverUrl = serverUrl;
        this.publicClientId = publicClientId;


        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    private RealmResource realmResource() {
        return keycloak.realm(realm);
    }

    @Override
    public UUID creerCompte(String email, String nom, String prenom, String motDePasseTemporaire, String role) {
        UsersResource usersResource = realmResource().users();

        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(prenom);
        user.setLastName(nom);
        user.setEnabled(true);
        user.setEmailVerified(true);

        Response response = usersResource.create(user);
        if (response.getStatus() != 201) {
            throw new IllegalStateException("Échec de la création du compte Keycloak : " + response.getStatus());
        }

        String location = response.getLocation().getPath();
        UUID idKeycloak = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));


        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(motDePasseTemporaire);
        credential.setTemporary(true);

        UserResource userResource = usersResource.get(idKeycloak.toString());
        userResource.resetPassword(credential);

        RoleRepresentation roleRepresentation = realmResource().roles().get(role).toRepresentation();
        userResource.roles().realmLevel().add(List.of(roleRepresentation));

        return idKeycloak;
    }

    @Override
    public void changerMotDePasse(UUID idKeycloak, String nouveauMotDePasse, boolean temporaire) {
        UserResource userResource = realmResource().users().get(idKeycloak.toString());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(nouveauMotDePasse);
        credential.setTemporary(temporaire);

        userResource.resetPassword(credential);
    }

    @Override
    public boolean verifierAncienMotDePasse(UUID idKeycloak, String ancienMotDePasse) {
        UserRepresentation user = realmResource().users().get(idKeycloak.toString()).toRepresentation();
        String username = user.getUsername();

        Keycloak keycloakUser = null;
        try {
            keycloakUser = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.PASSWORD)
                    .clientId(publicClientId)
                    .username(username)
                    .password(ancienMotDePasse)
                    .build();

            keycloakUser.tokenManager().getAccessToken();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (keycloakUser != null) {
                keycloakUser.close();
            }
        }
    }

    @Override
    public Optional<UUID> trouverIdParEmail(String email) {
        List<UserRepresentation> users = realmResource().users().search(email, true);
        return users.stream()
                .findFirst()
                .map(u -> UUID.fromString(u.getId()));
    }
}



