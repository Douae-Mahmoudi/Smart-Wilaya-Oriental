import React from 'react';
import { useNavigate } from 'react-router-dom';
 
export default function Cta() {
  const navigate = useNavigate();
 
  return (
    <section className="bg-blue-900 text-white py-20 px-6 text-center">
      <div className="max-w-3xl mx-auto">
        <h2 className="text-3xl font-bold mb-4">Ensemble, bâtissons une Région de l'Oriental plus intelligente.</h2>
        <p className="text-blue-200 mb-8 text-sm md:text-base">Rejoignez les milliers de citoyens qui participent activement à l'amélioration de notre cadre de vie quotidien.</p>
        <div className="flex justify-center gap-4">
          <button
            onClick={() => navigate('/signaler')}
            className="bg-white text-blue-900 hover:bg-blue-50 font-semibold px-6 py-3 rounded-xl shadow transition"
          >
            Signaler
          </button>
          <button
            onClick={() => navigate('/suivi')}
            className="bg-blue-800 hover:bg-blue-700 text-white font-semibold px-6 py-3 rounded-xl border border-blue-700 transition"
          >
            Voir votre signalement
          </button>
        </div>
      </div>
    </section>
  );
}
