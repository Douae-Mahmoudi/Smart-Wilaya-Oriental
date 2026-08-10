import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://keycloak:8080',
  realm: 'wilaya-interventions',
  clientId: 'frontend-app',
});

export default keycloak;