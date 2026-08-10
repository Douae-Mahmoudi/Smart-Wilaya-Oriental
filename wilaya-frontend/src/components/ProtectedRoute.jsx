import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ allowedRoles, children }) {
  const { authenticated, hasAnyRole } = useAuth();

  if (!authenticated) {
    return <Navigate to="/" replace />;
  }

  if (allowedRoles && !hasAnyRole(allowedRoles)) {
    return <Navigate to="/acces-refuse" replace />;
  }

  return children;
}