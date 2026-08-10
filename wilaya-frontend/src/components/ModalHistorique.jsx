import React from 'react';
import { X } from 'lucide-react';

const STATUT_COLORS = {
  SIGNALE: 'bg-slate-100 text-slate-600',
  CLASSIFIE: 'bg-blue-50 text-blue-700',
  EN_RECHERCHE_EQUIPE: 'bg-amber-50 text-amber-700',
  AFFECTE: 'bg-indigo-50 text-indigo-700',
  EN_INTERVENTION: 'bg-orange-50 text-orange-700',
  RESOLU: 'bg-green-50 text-green-700',
  CLOTURE: 'bg-slate-100 text-slate-500',
};

export default function ModalHistorique({ isOpen, onClose, historique, numeroSuivi }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/30 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[80vh] flex flex-col">
        <div className="flex items-center justify-between p-6 border-b border-slate-200">
          <h3 className="font-semibold text-slate-900">
            Historique des statuts – {numeroSuivi}
          </h3>
          <button
            onClick={onClose}
            className="p-1 rounded-lg hover:bg-slate-100 transition"
          >
            <X className="w-5 h-5 text-slate-500" />
          </button>
        </div>
        <div className="p-6 overflow-y-auto flex-1">
          {historique?.length === 0 ? (
            <p className="text-sm text-slate-400 text-center">Aucun changement enregistré.</p>
          ) : (
            <ul className="space-y-4">
              {historique?.map((h, idx) => (
                <li key={idx} className="flex items-start gap-4 text-sm">
                  <span className="shrink-0 text-slate-400 font-mono">
                    {new Date(h.dateChangement).toLocaleString('fr-FR')}
                  </span>
                  <span
                    className={`px-2.5 py-0.5 rounded-full text-xs font-semibold ${
                      STATUT_COLORS[h.nouveauStatut] || 'bg-slate-100'
                    }`}
                  >
                    {h.nouveauStatut}
                  </span>
                  <span className="text-slate-700 flex-1">{h.message}</span>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}