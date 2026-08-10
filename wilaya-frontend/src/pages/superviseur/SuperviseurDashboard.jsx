import React, { useState } from 'react';
import { ClipboardList, Wrench, MapPin, Sparkles } from 'lucide-react';
import logoOriental from '../../assets/Logo_Region_Oriental.png';
import UserMenu from '../../components/UserMenu';
import AffectationsTab from './tabs/AffectationsTab';
import AReaffecterTab from './tabs/AReaffecterTab';
import RapportIATab from './tabs/RapportIATab';
import CarteTab from '../admin/tabs/CarteTab';
 
const TABS = [
  { key: 'affectations', label: 'Affectations', icon: ClipboardList },
  { key: 'reaffecter', label: 'À réaffecter', icon: Wrench },
  { key: 'carte', label: 'Carte', icon: MapPin },
  { key: 'rapport', label: 'Rapport IA', icon: Sparkles },
];
 
export default function SuperviseurDashboard() {
  const [activeTab, setActiveTab] = useState('affectations');
 
  return (
    <div className="min-h-screen bg-slate-50">
      <header className="bg-white/90 backdrop-blur-md shadow-sm sticky top-0 z-50">
        <div className="w-full px-6 md:px-12 h-20 flex items-center justify-between">
 
          <div className="flex items-center space-x-3.5">
            <img src={logoOriental} alt="Logo Région Oriental" className="h-14 w-auto object-contain" />
            <div>
              <div className="font-bold text-xl text-blue-900 tracking-tight leading-tight">
                Smart Wilaya Oriental
              </div>
              <span className="text-xs font-semibold text-blue-600 uppercase tracking-wider">
                Espace Superviseur
              </span>
            </div>
          </div>
 
          <nav className="flex items-center gap-6">
            {TABS.map(({ key, label, icon: Icon }) => (
              <button
                key={key}
                onClick={() => setActiveTab(key)}
                className={`flex items-center gap-2 px-4 py-2.5 rounded-lg text-sm font-semibold transition ${
                  activeTab === key
                    ? 'bg-blue-700 text-white shadow-md shadow-blue-700/20'
                    : 'text-slate-600 hover:bg-slate-100'
                }`}
              >
                <Icon className="w-4 h-4" />
                {label}
              </button>
            ))}
          </nav>
 
          <UserMenu />
 
        </div>
      </header>
 
      <main className="max-w-7xl mx-auto px-6 md:px-12 py-10">
        {activeTab === 'affectations' && <AffectationsTab />}
        {activeTab === 'reaffecter' && <AReaffecterTab />}
        {activeTab === 'carte' && <CarteTab />}
        {activeTab === 'rapport' && <RapportIATab />}
      </main>
    </div>
  );
}
 


































































