import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './layout/Layout';
import HomePage from './pages/HomePage';
import SignalementPage from './pages/SignalementPage';
import SuiviPage from './pages/SuiviPage';
import ProtectedRoute from './components/ProtectedRoute';
import PostLoginRedirect from './components/PostLoginRedirect';
import { ROLES } from './utils/roles';
import './App.css';
import MonProfilPage from './pages/admin/MonProfilPage';
 
// Dashboards
import AdminDashboard from './pages/admin/AdminDashboard';
import AgentDashboard from './pages/agent/AgentDashboard';
import SuperviseurDashboard from './pages/superviseur/SuperviseurDashboard';
 
function App() {
  return (
    <BrowserRouter>
      <PostLoginRedirect />
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/signaler" element={<SignalementPage />} />
          <Route path="/suivi" element={<SuiviPage />} />
        </Route>
 
        <Route
          path="/admin/dashboard"
          element={
            <ProtectedRoute allowedRoles={[ROLES.ADMIN]}>
              <AdminDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/profil"
          element={
            <ProtectedRoute allowedRoles={[ROLES.ADMIN, ROLES.AGENT, ROLES.SUPERVISEUR]}>
              <MonProfilPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/agent/dashboard"
          element={
            <ProtectedRoute allowedRoles={[ROLES.AGENT]}>
              <AgentDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/superviseur/dashboard"
          element={
            <ProtectedRoute allowedRoles={[ROLES.SUPERVISEUR]}>
              <SuperviseurDashboard />
            </ProtectedRoute>
          }
        />
        <Route path="/acces-refuse" element={<div className="p-10 text-center">Accès refusé.</div>} />
      </Routes>
    </BrowserRouter>
  );
}
 
export default App;








































































