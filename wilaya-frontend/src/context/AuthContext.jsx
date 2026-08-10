import React, { createContext, useContext, useEffect, useState, useRef } from 'react';
import keycloak from '../keycloak';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [initialized, setInitialized] = useState(false);
  const [authenticated, setAuthenticated] = useState(false);
  const [roles, setRoles] = useState([]);
  const initStarted = useRef(false);

  useEffect(() => {
    if (initStarted.current) return;
    initStarted.current = true;

    keycloak
      .init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        pkceMethod: 'S256',
      })
      .then((auth) => {
        setAuthenticated(auth);
        if (auth) {
          setRoles(keycloak.tokenParsed?.realm_access?.roles || []);
        }
        setInitialized(true);
      })
      .catch((err) => {
        console.error('Erreur init Keycloak', err);
        setInitialized(true);
      });
  }, []);

  useEffect(() => {
    if (!authenticated) return;

    const interval = setInterval(() => {
      keycloak
        .updateToken(30)
        .then((refreshed) => {
          if (refreshed) {
            setRoles(keycloak.tokenParsed?.realm_access?.roles || []);
          }
        })
        .catch(() => {
          setAuthenticated(false);
          setRoles([]);
        });
    }, 20000);

    return () => clearInterval(interval);
  }, [authenticated]);

  const login = (redirectUri) => keycloak.login(redirectUri ? { redirectUri } : undefined);
  const logout = () => keycloak.logout({ redirectUri: window.location.origin });

  const hasRole = (role) => roles.includes(role);
  const hasAnyRole = (rolesToCheck) => rolesToCheck.some((r) => roles.includes(r));

  const value = {
    keycloak,
    authenticated,
    initialized,
    roles,
    login,
    logout,
    hasRole,
    hasAnyRole,
    token: keycloak.token,
    user: keycloak.tokenParsed,
  };

  if (!initialized) return null;

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);