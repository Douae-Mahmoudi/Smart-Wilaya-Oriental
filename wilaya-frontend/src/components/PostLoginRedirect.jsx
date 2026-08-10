import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getDashboardPath } from '../utils/roles';

export default function PostLoginRedirect() {
  const { authenticated, roles } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    if (authenticated && roles.length > 0 && location.pathname === '/') {
      const path = getDashboardPath(roles);
      if (path !== '/') {
        navigate(path, { replace: true });
      }
    }
  }, [authenticated, roles, navigate, location.pathname]);

  return null;
}