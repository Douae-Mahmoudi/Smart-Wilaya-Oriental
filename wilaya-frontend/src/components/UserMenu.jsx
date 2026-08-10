import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { User, LogOut, ChevronDown } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function UserMenu() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [ouvert, setOuvert] = useState(false);
  const menuRef = useRef(null);

  const prenom = user?.given_name || '';
  const nom = user?.family_name || '';
  const initiales = `${prenom.charAt(0)}${nom.charAt(0)}`.toUpperCase() || 'U';

  useEffect(() => {
    const fermerSiExterieur = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setOuvert(false);
      }
    };
    document.addEventListener('mousedown', fermerSiExterieur);
    return () => document.removeEventListener('mousedown', fermerSiExterieur);
  }, []);

  return (
    <div className="relative" ref={menuRef}>
      <button
        onClick={() => setOuvert((v) => !v)}
        className="flex items-center gap-2 pl-1.5 pr-3 py-1.5 rounded-full hover:bg-slate-100 transition"
      >
        <div className="w-9 h-9 rounded-full bg-blue-700 text-white flex items-center justify-center text-sm font-bold flex-shrink-0">
          {initiales}
        </div>
        <span className="text-sm font-medium text-slate-700 hidden sm:block">
          {prenom} {nom}
        </span>
        <ChevronDown className={`w-4 h-4 text-slate-400 transition-transform ${ouvert ? 'rotate-180' : ''}`} />
      </button>

      {ouvert && (
        <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-xl border border-slate-100 py-1.5 z-50">
          <div className="px-4 py-2.5 border-b border-slate-100">
            <p className="text-sm font-semibold text-slate-900 truncate">{prenom} {nom}</p>
            <p className="text-xs text-slate-500 truncate">{user?.email}</p>
          </div>

          <button
            onClick={() => {
              setOuvert(false);
              navigate('/admin/profil');
            }}
            className="w-full flex items-center gap-2.5 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition"
          >
            <User className="w-4 h-4 text-slate-400" />
            Mon profil
          </button>

          <button
            onClick={() => logout()}
            className="w-full flex items-center gap-2.5 px-4 py-2.5 text-sm text-red-600 hover:bg-red-50 transition"
          >
            <LogOut className="w-4 h-4" />
            Déconnexion
          </button>
        </div>
      )}
    </div>
  );
}