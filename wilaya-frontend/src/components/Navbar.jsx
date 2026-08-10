import React from 'react';
import logoOriental from '../assets/Logo_Region_Oriental.png';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { authenticated, login, logout, user } = useAuth();

  return (
    <header className="bg-white/90 backdrop-blur-md shadow-sm fixed top-0 left-0 right-0 z-50">
      <div className="w-full px-6 md:px-12 h-20 flex items-center justify-between">
        
        <div className="flex items-center space-x-3.5">
          <img 
            src={logoOriental} 
            alt="Logo Région Oriental" 
            className="h-14 w-auto object-contain" 
          />
          <div className="font-bold text-xl text-blue-900 tracking-tight">
            Smart Wilaya Oriental
          </div>
        </div>

        <div>
          {authenticated ? (
            <button
              onClick={() => logout()}
              className="bg-blue-700 hover:bg-blue-800 text-white px-5 py-2.5 rounded-lg text-sm font-semibold transition shadow-md shadow-blue-700/20"
            >
              {user?.given_name ? `Bonjour, ${user.given_name}` : 'Se déconnecter'}
            </button>
          ) : (
            <button
              onClick={() => login(window.location.origin + '/')}
              className="bg-blue-700 hover:bg-blue-800 text-white px-5 py-2.5 rounded-lg text-sm font-semibold transition shadow-md shadow-blue-700/20"
            >
              Connexion
            </button>
          )}
        </div>

      </div>
    </header>
  );
}
































