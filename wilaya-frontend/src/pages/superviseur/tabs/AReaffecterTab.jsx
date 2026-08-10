import React, { useState, useEffect, useCallback } from 'react';
import { Wrench, Loader2, RefreshCw, AlertTriangle, ArrowRight } from 'lucide-react';
import { affectationClient, ressourceClient } from '../../../api/client';
 
const COULEUR_GRAVITE = {
  HAUTE: '#dc2626',
  MOYENNE: '#eab308',
  BASSE: '#16a34a',
};

function formatLabelEquipe(eq) {
  const nom = eq.nom || `Équipe #${eq.id?.slice(0, 8)}`;
  const competences = eq.competences;
  const zone = eq.zoneCouverture;

  const details = [
    zone,
    Array.isArray(competences) ? competences.join(', ') : competences,
  ].filter(Boolean);

  return details.length ? `${nom} (${details.join(' · ')})` : nom;
}
 
export default function AReaffecterTab() {
  const [sansEquipe, setSansEquipe] = useState([]);
  const [equipes, setEquipes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selections, setSelections] = useState({});
  const [assignationEnCours, setAssignationEnCours] = useState(null);
 
  const charger = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [reponseSansEquipe, reponseEquipes] = await Promise.all([
        affectationClient.get('/affectations/sans-equipe'),
        ressourceClient.get('/equipes'),
      ]);
      setSansEquipe(reponseSansEquipe.data);
      setEquipes(reponseEquipes.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Impossible de charger les signalements sans équipe.');
    } finally {
      setLoading(false);
    }
  }, []);
 
  useEffect(() => { charger(); }, [charger]);
 
  const choisirEquipe = (idSignalement, idEquipe) => {
    setSelections((prev) => ({ ...prev, [idSignalement]: idEquipe }));
  };
 
  const assignerManuellement = async (idSignalement) => {
    const idEquipe = selections[idSignalement];
    if (!idEquipe) return;
    setAssignationEnCours(idSignalement);
    setError(null);
    try {
      await affectationClient.post(`/affectations/${idSignalement}/affecter-manuellement`, { idEquipe });
      setSansEquipe((prev) => prev.filter((s) => s.idSignalement !== idSignalement));
    } catch (err) {
      setError(err.response?.data?.message || "Impossible d'assigner cette équipe.");
    } finally {
      setAssignationEnCours(null);
    }
  };
 
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
            <Wrench className="w-5 h-5 text-blue-700" />
            Signalements à réaffecter manuellement
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
              <div key={i} className="h-28 bg-slate-50 rounded-xl animate-pulse" />
            ))}
          </div>
        ) : sansEquipe.length === 0 ? (
          <p className="text-sm text-slate-400 text-center py-14">
            Aucun signalement en attente d'affectation manuelle.
          </p>
        ) : (
          <div className="space-y-4">
            {sansEquipe.map((s) => {
              const equipeChoisie = selections[s.idSignalement] ?? '';
              const enCours = assignationEnCours === s.idSignalement;
 
              return (
                <div
                  key={s.id}
                  className="border border-slate-200 rounded-2xl p-5 flex flex-col md:flex-row md:items-center gap-4 md:gap-6"
                >
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1.5">
                      <span className="text-sm font-bold text-blue-700">
                        Signalement #{s.idSignalement?.slice(0, 8) || '—'}
                      </span>
                      {s.gravite && (
                        <span
                          className="px-2 py-0.5 rounded-full text-[11px] font-semibold"
                          style={{
                            color: COULEUR_GRAVITE[s.gravite] || '#334155',
                            backgroundColor: `${COULEUR_GRAVITE[s.gravite] || '#334155'}15`,
                          }}
                        >
                          {s.gravite}
                        </span>
                      )}
                    </div>
                    <p className="text-sm font-semibold text-slate-800 mb-2">{s.categorie || 'Type inconnu'}</p>
                    <p className="text-xs text-slate-400">{s.zone || 'Zone non précisée'}</p>
                  </div>
 
                  <div className="flex items-center gap-2 shrink-0">
                    <select
                      value={equipeChoisie}
                      onChange={(e) => choisirEquipe(s.idSignalement, e.target.value)}
                      className="border border-slate-200 rounded-xl px-3 py-2.5 text-sm text-slate-700 outline-none focus:ring-2 focus:ring-blue-600/40 focus:border-blue-600 min-w-[260px]"
                    >
                      <option value="" disabled>
                        Choisir une équipe...
                      </option>
                      {equipes.map((eq) => (
                        <option key={eq.id} value={eq.id}>
                          {formatLabelEquipe(eq)}
                        </option>
                      ))}
                    </select>
                    <button
                      onClick={() => assignerManuellement(s.idSignalement)}
                      disabled={!equipeChoisie || enCours}
                      className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl text-sm font-semibold bg-blue-700 hover:bg-blue-800 disabled:opacity-50 text-white transition"
                    >
                      {enCours ? (
                        <Loader2 className="w-4 h-4 animate-spin" />
                      ) : (
                        <ArrowRight className="w-4 h-4" />
                      )}
                      Assigner
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
 

































