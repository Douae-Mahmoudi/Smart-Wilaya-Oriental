import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { Search, ArrowLeft, CheckCircle2, Wrench, Clock, FileText } from 'lucide-react';
 
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:7057';
 
const STATUT_STYLES = {
  SIGNALE: { icon: FileText, couleur: 'text-slate-500 bg-slate-100' },
  CLASSIFIE: { icon: FileText, couleur: 'text-blue-600 bg-blue-50' },
  EN_RECHERCHE_EQUIPE: { icon: Clock, couleur: 'text-amber-600 bg-amber-50' },
  AFFECTE: { icon: Clock, couleur: 'text-indigo-600 bg-indigo-50' },
  EN_INTERVENTION: { icon: Wrench, couleur: 'text-orange-600 bg-orange-50' },
  RESOLU: { icon: CheckCircle2, couleur: 'text-green-600 bg-green-50' },
  CLOTURE: { icon: CheckCircle2, couleur: 'text-slate-500 bg-slate-100' },
};
 
const STATUT_LABELS = {
  SIGNALE: 'Signalé',
  CLASSIFIE: 'Classifié',
  EN_RECHERCHE_EQUIPE: 'Recherche d\'une équipe',
  AFFECTE: 'Équipe affectée',
  EN_INTERVENTION: 'En intervention',
  RESOLU: 'Résolu',
  CLOTURE: 'Clôturé',
};
 
function formaterDateMaroc(isoString) {
  if (!isoString) return '—';
  const [datePart, timePart] = isoString.split('T');
  if (!datePart || !timePart) return isoString;
  const [annee, mois, jour] = datePart.split('-');
  const heureMinute = timePart.slice(0, 5);
  return `${jour}/${mois}/${annee} à ${heureMinute}`;
}
 
export default function SuiviPage() {
  const location = useLocation();
  const [numeroSuivi, setNumeroSuivi] = useState(location.state?.numeroSuivi || '');
  const [signalement, setSignalement] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
 
  const rechercher = async (numero) => {
    const valeur = (numero ?? numeroSuivi).trim();
    if (!valeur) return;
    setLoading(true);
    setError(null);
    setSignalement(null);
    try {
      const response = await fetch(`${API_BASE_URL}/signalements/${valeur}`);
      if (!response.ok) throw new Error('Aucun signalement trouve pour ce numero.');
      const data = await response.json();
      setSignalement(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (location.state?.numeroSuivi) {
      rechercher(location.state.numeroSuivi);
    }
  }, []);
 
  return (
    <section className="min-h-[80vh] bg-slate-50 py-16 px-6">
      <div className="max-w-xl mx-auto mt-24">
        <a href="/" className="inline-flex items-center gap-2 text-sm text-slate-600 hover:text-blue-700 mb-4 transition">
          <ArrowLeft className="w-4 h-4" />
          Retour a l'accueil
        </a>
 
        <div className="bg-white rounded-2xl shadow-xl p-8">
          <h1 className="text-xl font-bold text-slate-900 mb-6">Suivre mon signalement</h1>
 
          <div className="flex gap-3 mb-6">
            <input
              type="text"
              value={numeroSuivi}
              onChange={(e) => setNumeroSuivi(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && rechercher()}
              placeholder="Entrez votre numero de suivi"
              className="flex-1 border border-slate-200 rounded-xl px-4 py-2.5 text-sm focus:ring-2 focus:ring-blue-600 focus:border-transparent outline-none"
            />
            <button
              onClick={() => rechercher()}
              disabled={loading}
              className="bg-blue-700 hover:bg-blue-800 disabled:opacity-60 text-white px-5 rounded-xl flex items-center justify-center transition"
            >
              <Search className="w-4 h-4" />
            </button>
          </div>
 
          {error && <p className="text-sm text-red-600">{error}</p>}
 
          {signalement && (
            <>
              <dl className="divide-y divide-slate-100 text-sm mb-6">
                <div className="flex justify-between py-3">
                  <dt className="text-slate-500">Statut actuel</dt>
                  <dd className="text-slate-900 font-medium">
                    {STATUT_LABELS[signalement.statut] || signalement.statut}
                  </dd>
                </div>
                <div className="flex justify-between py-3">
                  <dt className="text-slate-500">Type</dt>
                  <dd className="text-slate-900 font-medium">{signalement.type}</dd>
                </div>
                <div className="flex justify-between py-3">
                  <dt className="text-slate-500">Zone</dt>
                  <dd className="text-slate-900 font-medium">{signalement.zone}</dd>
                </div>
              </dl>
 
              {signalement.historiqueStatuts?.length > 0 && (
                <div>
                  <h2 className="text-sm font-semibold text-slate-700 mb-4">
                    Historique du traitement
                  </h2>
                  <div className="space-y-4">
                    {signalement.historiqueStatuts.map((etape, index) => {
                      const style = STATUT_STYLES[etape.nouveauStatut] || STATUT_STYLES.SIGNALE;
                      const Icon = style.icon;
                      return (
                        <div key={index} className="flex gap-3">
                          <div className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 ${style.couleur}`}>
                            <Icon className="w-4 h-4" />
                          </div>
                          <div className="flex-1 pb-1">
                            <div className="flex items-center justify-between gap-2">
                              <p className="text-sm font-semibold text-slate-800">
                                {STATUT_LABELS[etape.nouveauStatut] || etape.nouveauStatut}
                              </p>
                              <span className="text-xs text-slate-400 whitespace-nowrap">
                                {formaterDateMaroc(etape.dateChangement)}
                              </span>
                            </div>
                            {etape.message && (
                              <p className="text-sm text-slate-500 mt-0.5">{etape.message}</p>
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </section>
  );
}
 








































































































































