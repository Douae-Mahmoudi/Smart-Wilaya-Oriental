import axios from 'axios';
import keycloak from '../keycloak';
 
const attachAuth = (instance) => {
  instance.interceptors.request.use(async (config) => {
    if (keycloak.token) {
      try {
        await keycloak.updateToken(30);
      } catch {
        keycloak.login();
      }
      config.headers.Authorization = `Bearer ${keycloak.token}`;
    }
    return config;
  });
  return instance;
};
 
export const ressourceClient = attachAuth(
  axios.create({
    baseURL: process.env.REACT_APP_RESSOURCE_API_URL || 'http://localhost:7056',
  })
);
 
export const signalementClient = attachAuth(
  axios.create({
    baseURL: process.env.REACT_APP_SIGNALEMENT_API_URL || 'http://localhost:7057',
  })
);
 
export const affectationClient = attachAuth(
  axios.create({
    baseURL: process.env.REACT_APP_AFFECTATION_API_URL || 'http://localhost:7058',
  })
);
 
export const utilisateurClient = attachAuth(
  axios.create({
    baseURL: process.env.REACT_APP_UTILISATEUR_API_URL || 'http://localhost:7055',
  })
);
 
export default signalementClient;
