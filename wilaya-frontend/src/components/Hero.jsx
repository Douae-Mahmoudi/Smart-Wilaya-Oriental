import React from 'react';
import { useNavigate } from 'react-router-dom';
import { AlertCircle } from 'lucide-react';
import heroImage from '../assets/hero-bg.jpg';
 
export default function Hero() {
  const navigate = useNavigate();
 
  return (
    <section className="relative h-screen w-full flex items-center pt-20 overflow-hidden">
      <div className="absolute inset-0 z-0">
        <img src={heroImage} alt="Vue aérienne Oujda" className="w-full h-full object-cover object-center" />
        <div className="absolute inset-0 bg-gradient-to-r from-slate-950/85 via-slate-950/50 to-transparent"></div>
      </div>
 
      <div className="relative z-10 w-full px-6 md:px-12 py-20 text-white">
        <div className="max-w-2xl">
          <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight mb-6">
            Smart Wilaya Oriental
          </h1>
          <p className="text-slate-200 text-lg mb-8 leading-relaxed">
            Plateforme Intelligente de gestion des signalements citoyens et des interventions techniques au service de la Région de l'Oriental.
          </p>
          <div className="flex flex-wrap gap-4">
            <button
              onClick={() => navigate('/signaler')}
              className="bg-blue-600 hover:bg-blue-500 text-white font-semibold px-6 py-3.5 rounded-xl shadow-lg shadow-blue-600/30 flex items-center gap-2.5 transition"
            >
              <AlertCircle className="w-5 h-5" />
              Signaler un problème
            </button>
 
            <button
              onClick={() => navigate('/suivi')}
              className="bg-white/10 hover:bg-white/20 backdrop-blur-md text-white font-semibold px-6 py-3.5 rounded-xl border border-white/20 flex items-center gap-2.5 transition"
            >
              Voir votre signalement
            </button>
          </div>
        </div>
      </div>
    </section>
  );
}
