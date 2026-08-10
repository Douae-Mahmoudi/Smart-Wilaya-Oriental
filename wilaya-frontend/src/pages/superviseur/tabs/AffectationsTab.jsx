import React, { useState, useEffect, useCallback } from 'react';
import { ClipboardList, Loader2, RefreshCw, AlertTriangle, Clock } from 'lucide-react';
import { affectationClient, ressourceClient } from '../../../api/client';
 
const COULEUR_GRAVITE = {
  HAUTE: '#dc2626',
  MOYENNE: '#eab308',
  BASSE: '#16a34a',
};
 
const STATUT_TENTATIVE = {
  EN_ATTENTE: 'EN_ATTENTE',
  ACCEPTEE: 'ACCEPTEE',
  REFUSEE: 'REFUSEE',
  EXPIREE: 'EXPIREE',
};
 
export default function AffectationsTab() {
  const [affectations, setAffectations] = useState([]);
  const [nomsEquipes, setNomsEquipes] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
 
  const charger = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [reponseAffectations, reponseEquipes] = await Promise.all([
        affectationClient.get('/affectations/en-attente'),
        ressourceClient.get('/equipes'),
      ]);
      setAffectations(reponseAffectations.data);
      const map = {};
      reponseEquipes.data.forEach((eq) => { map[eq.id] = eq.nom || `Équipe #${eq.id?.slice(0, 8)}`; });
      setNomsEquipes(map);
    } catch (err) {
      setError(err.response?.data?.message || 'Impossible de charger les affectations en attente.');
    } finally {
      setLoading(false);
    }
  }, []);
 
  useEffect(() => { charger(); }, [charger]);
 
  return (
    <div className="space-y-6">
      {error && (
        <div className="flex items-start gap-2.5 px-4 py-3 rounded-xl bg-red-50 border border-red-100">
          <AlertTriangle className="w-4 h-4 text-red-600 mt-0.5 shrink-0" />
          <p className="text-sm text-red-700 font-medium">{error}</p>
        </div>
      )}
 
      <div className="bg-white rounded-2xl shadow-xl p-6 md:p-8">
        <div className="flex items-center justify-between mb-5">
          <h2 className="text-lg font-bold text-slate-900 flex items-center gap-2">
            <ClipboardList className="w-5 h-5 text-blue-700" />
            Affectations en attente
          </h2>
          <button
            onClick={charger}
            className="text-sm text-blue-700 hover:text-blue-900 font-semibold flex items-center gap-1.5"
          >
            <RefreshCw className="w-4 h-4" />
            Actualiser
          </button>
        </div>
 
        {loading ? (
          <div className="space-y-3">
            {Array.from({ length: 3 }).map((_, i) => (
              <div key={i} className="h-24 bg-slate-50 rounded-xl animate-pulse" />
            ))}
          </div>
        ) : affectations.length === 0 ? (
          <p className="text-sm text-slate-400 text-center py-14">
            Aucune affectation en attente pour le moment.
          </p>
        ) : (
          <div className="space-y-4">
            {affectations.map((a) => (
              <div
                key={a.id}
                className="border border-slate-200 rounded-2xl p-5 flex flex-col md:flex-row md:items-center gap-4 md:gap-6"
              >
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1.5">
                    <span className="text-sm font-bold text-blue-700">
                      Signalement #{a.idSignalement?.slice(0, 8) || '—'}
                    </span>
                    {a.gravite && (
                      <span
                        className="px-2 py-0.5 rounded-full text-[11px] font-semibold"
                        style={{
                          color: COULEUR_GRAVITE[a.gravite] || '#334155',
                          backgroundColor: `${COULEUR_GRAVITE[a.gravite] || '#334155'}15`,
                        }}
                      >
                        {a.gravite}
                      </span>
                    )}
                    {a.statut === STATUT_TENTATIVE.EN_ATTENTE && (
                      <span className="flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-semibold bg-amber-50 text-amber-700">
                        <Clock className="w-3 h-3" />
                        En attente
                      </span>
                    )}
                  </div>
                  <p className="text-sm font-semibold text-slate-800 mb-1">{a.categorie || 'Type inconnu'}</p>
                  <p className="text-xs text-slate-400">{a.zone || 'Zone non précisée'}</p>
                </div>
 
                <div className="text-right shrink-0">
                  <p className="text-xs text-slate-400 mb-0.5">Équipe proposée</p>
                  <p className="text-sm font-semibold text-slate-700">
                    {nomsEquipes[a.idEquipeProposee] || `#${a.idEquipeProposee?.slice(0, 8)}` || '—'}
                  </p>
                  {a.score != null && (
                    <p className="text-xs text-slate-400 mt-1">Score {(a.score * 100).toFixed(0)}%</p>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
