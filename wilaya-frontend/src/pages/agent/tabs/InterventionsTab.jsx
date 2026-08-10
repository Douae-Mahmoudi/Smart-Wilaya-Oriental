import React, { useState, useEffect, useCallback } from 'react';
import {
  Wrench,
  Loader2,
  RefreshCw,
  MapPin,
  AlertTriangle,
  ArrowRight,
  CheckCircle2,
  ChevronDown,
  ImageOff,
  Droplet,
  Lightbulb,
  Construction,
  Trash2,
  Trees,
  ClipboardList,
} from 'lucide-react';
import { affectationClient, signalementClient, utilisateurClient } from '../../../api/client';

const COULEUR_GRAVITE = {
  HAUTE: '#dc2626',
  MOYENNE: '#eab308',
  BASSE: '#16a34a',
};

const ICONE_CATEGORIE = {
  EAU: Droplet,
  ELECTRICITE: Lightbulb,
  VOIRIE: Construction,
  PROPRETE: Trash2,
  ESPACES_VERTS: Trees,
};

const STATUT_TENTATIVE = {
  EN_ATTENTE: 'EN_ATTENTE',
  ACCEPTEE: 'ACCEPTEE',
  REFUSEE: 'REFUSEE',
  EXPIREE: 'EXPIREE',
};

const STATUTS_AGENT = [
  { value: 'EN_INTERVENTION', label: 'En intervention' },
  { value: 'RESOLU', label: 'Résolu' },
];

export default function InterventionsTab() {
  const [idEquipe, setIdEquipe] = useState(null);
  const [loadingEquipe, setLoadingEquipe] = useState(true);
  const [erreurEquipe, setErreurEquipe] = useState(null);

  const [affectations, setAffectations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [selections, setSelections] = useState({});
  const [messages, setMessages] = useState({}); // 👈 État pour les messages
  const [majEnCours, setMajEnCours] = useState(null);
  const [statutsAppliques, setStatutsAppliques] = useState({});
 
  const [ouvert, setOuvert] = useState(null);
  const [details, setDetails] = useState({});
  const [chargementDetails, setChargementDetails] = useState(null);

  const chargerMonEquipe = useCallback(async () => {
    setLoadingEquipe(true);
    setErreurEquipe(null);
    try {
      const { data } = await utilisateurClient.get('/utilisateurs/moi/equipe');
      setIdEquipe(data.idEquipe);
    } catch (err) {
      setErreurEquipe(
        err.response?.data?.message || "Impossible de récupérer votre équipe. Contactez votre superviseur."
      );
    } finally {
      setLoadingEquipe(false);
    }
  }, []);

  const charger = useCallback(async (equipe) => {
    if (!equipe) return;
    setLoading(true);
    setError(null);
    try {
      const { data } = await affectationClient.get('/affectations/mes-affectations', {
        params: { idEquipe: equipe },
      });
      const accepteesUniquement = data.filter((a) => a.statut === STATUT_TENTATIVE.ACCEPTEE);
      setAffectations(accepteesUniquement);

      const statuts = await Promise.all(
        accepteesUniquement.map(async (a) => {
          try {
            const res = await signalementClient.get(`/signalements/${a.idSignalement}/statut`);
            return [a.idSignalement, res.data.statut];
          } catch {
            return [a.idSignalement, null];
          }
        })
      );
      setStatutsAppliques((prev) => ({
        ...prev,
        ...Object.fromEntries(statuts.filter(([, statut]) => statut)),
      }));
    } catch (err) {
      setError(err.response?.data?.message || 'Impossible de charger vos interventions.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { chargerMonEquipe(); }, [chargerMonEquipe]);
  useEffect(() => { if (idEquipe) charger(idEquipe); }, [idEquipe, charger]);

  const choisirStatut = (idSignalement, statut) => {
    setSelections((prev) => ({ ...prev, [idSignalement]: statut }));
  };

  const changerMessage = (idSignalement, msg) => {
    setMessages((prev) => ({ ...prev, [idSignalement]: msg }));
  };

  const basculerDetails = async (idSignalement) => {
    if (ouvert === idSignalement) {
      setOuvert(null);
      return;
    }
    setOuvert(idSignalement);
    if (details[idSignalement]) return; 

    setChargementDetails(idSignalement);
    try {
      const { data } = await signalementClient.get(`/signalements/${idSignalement}/details`);
      setDetails((prev) => ({ ...prev, [idSignalement]: data }));
    } catch (err) {
      setDetails((prev) => ({ ...prev, [idSignalement]: { erreur: true } }));
    } finally {
      setChargementDetails(null);
    }
  };

  const changerStatut = async (idSignalement) => {
    const statut = selections[idSignalement];
    const message = messages[idSignalement]?.trim();
    if (!statut) return;
    if (!message) {
      setError("Veuillez fournir un message explicatif pour ce changement.");
      return;
    }
    setMajEnCours(idSignalement);
    setError(null);
    try {
      await signalementClient.patch(`/signalements/${idSignalement}/statut`, { statut, message });
      setStatutsAppliques((prev) => ({ ...prev, [idSignalement]: statut }));
      setSelections((prev) => ({ ...prev, [idSignalement]: '' }));
      setMessages((prev) => ({ ...prev, [idSignalement]: '' }));
    } catch (err) {
      setError(err.response?.data?.message || 'Impossible de mettre à jour le statut.');
    } finally {
      setMajEnCours(null);
    }
  };

  if (loadingEquipe) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-5 h-5 animate-spin text-slate-400" />
      </div>
    );
  }

  if (erreurEquipe) {
    return (
      <div className="flex items-start gap-2.5 px-4 py-3 rounded-xl bg-red-50 border border-red-100">
        <AlertTriangle className="w-4 h-4 text-red-600 mt-0.5 shrink-0" />
        <p className="text-sm text-red-700 font-medium">{erreurEquipe}</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {error && (
        <div className="flex items-start gap-2.5 px-4 py-3 rounded-xl bg-red-50 border border-red-100">
          <AlertTriangle className="w-4 h-4 text-red-600 mt-0.5 shrink-0" />
          <p className="text-sm text-red-700 font-medium">{error}</p>
        </div>
      )}

      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,500;9..144,600&family=Inter:wght@400;500;600;700&display=swap');
        .font-display { font-family: 'Fraunces', serif; }
      `}</style>

      <div className="bg-white rounded-2xl shadow-xl p-6 md:p-8">
        <div className="flex items-center justify-between mb-5">
          <h2 className="font-display text-lg font-semibold text-slate-900 flex items-center gap-2">
            <Wrench className="w-5 h-5 text-blue-700" />
            Mes interventions en cours
          </h2>
          <button
            onClick={() => charger(idEquipe)}
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
        ) : affectations.length === 0 ? (
          <div className="text-center py-14">
            <div className="w-12 h-12 rounded-full bg-slate-50 flex items-center justify-center mx-auto mb-3">
              <Wrench className="w-5 h-5 text-slate-300" />
            </div>
            <p className="text-sm text-slate-400">
              Aucune intervention active. Acceptez une affectation pour la voir apparaître ici.
            </p>
          </div>
        ) : (
          <div className="space-y-4">
            {affectations.map((a) => {
              const statutChoisi = selections[a.idSignalement] ?? '';
              const messageSaisi = messages[a.idSignalement] ?? '';
              const enCours = majEnCours === a.idSignalement;
              const statutApplique = statutsAppliques[a.idSignalement];
              const estResolu = statutApplique === 'RESOLU';
              const optionsDisponibles = STATUTS_AGENT.filter((opt) => opt.value !== statutApplique);
              const IconeCategorie = ICONE_CATEGORIE[a.categorie] || ClipboardList;
              const couleurGravite = COULEUR_GRAVITE[a.gravite] || '#94a3b8';

              return (
                <div
                  key={a.id}
                  className="relative flex rounded-2xl border border-slate-200 hover:border-slate-300 hover:shadow-md transition-all duration-200 overflow-hidden"
                >
                  <div className="w-1.5 shrink-0" style={{ backgroundColor: couleurGravite }} aria-hidden="true" />

                  <div className="flex-1 p-5">
                    <div className="flex flex-col md:flex-row md:items-start gap-4 md:gap-6">
                      <div className="flex-1 min-w-0">
                        <button
                          onClick={() => basculerDetails(a.idSignalement)}
                          className="flex items-center gap-2 mb-1.5 group"
                        >
                          <div
                            className="w-8 h-8 rounded-lg flex items-center justify-center shrink-0"
                            style={{ color: couleurGravite, backgroundColor: `${couleurGravite}15` }}
                          >
                            <IconeCategorie className="w-4 h-4" />
                          </div>
                          <span className="text-sm font-bold text-blue-700 group-hover:underline">
                            Signalement #{a.idSignalement?.slice(0, 8) || '—'}
                          </span>
                          <ChevronDown
                            className={`w-3.5 h-3.5 text-slate-400 transition-transform ${
                              ouvert === a.idSignalement ? 'rotate-180' : ''
                            }`}
                          />
                          {a.gravite && (
                            <span
                              className="px-2 py-0.5 rounded-full text-[11px] font-semibold"
                              style={{ color: couleurGravite, backgroundColor: `${couleurGravite}15` }}
                            >
                              {a.gravite}
                            </span>
                          )}
                          {STATUTS_AGENT.find((o) => o.value === statutApplique) && (
                            <span className="px-2 py-0.5 rounded-full text-[11px] font-semibold bg-blue-50 text-blue-700">
                              {STATUTS_AGENT.find((o) => o.value === statutApplique)?.label}
                            </span>
                          )}
                        </button>
                        <p className="text-sm font-semibold text-slate-800 mb-2 ml-10">{a.categorie || 'Type inconnu'}</p>
                        <span className="flex items-center gap-1.5 text-xs text-slate-400 ml-10">
                          <MapPin className="w-3.5 h-3.5" />
                          {a.zone || 'Zone non précisée'}
                        </span>
                      </div>

                      {!estResolu && (
                        <div className="flex flex-col items-end gap-2 shrink-0 w-full md:w-auto">
                          <div className="flex flex-col sm:flex-row items-end sm:items-center gap-2 w-full">
                            <select
                              value={statutChoisi}
                              onChange={(e) => choisirStatut(a.idSignalement, e.target.value)}
                              className="w-full sm:w-auto border border-slate-200 rounded-xl px-3 py-2.5 text-sm text-slate-700 outline-none focus:ring-2 focus:ring-blue-600/40 focus:border-blue-600"
                            >
                              <option value="" disabled>
                                Nouveau statut...
                              </option>
                              {optionsDisponibles.map((opt) => (
                                <option key={opt.value} value={opt.value}>
                                  {opt.label}
                                </option>
                              ))}
                            </select>
                            <button
                              onClick={() => changerStatut(a.idSignalement)}
                              disabled={!statutChoisi || enCours || !messageSaisi.trim()}
                              className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl text-sm font-semibold bg-blue-700 hover:bg-blue-800 disabled:opacity-50 text-white transition"
                            >
                              {enCours ? (
                                <Loader2 className="w-4 h-4 animate-spin" />
                              ) : (
                                <ArrowRight className="w-4 h-4" />
                              )}
                              Mettre à jour
                            </button>
                          </div>
                          {statutChoisi && (
                            <div className="w-full sm:w-auto">
                              <input
                                type="text"
                                value={messageSaisi}
                                onChange={(e) => changerMessage(a.idSignalement, e.target.value)}
                                placeholder="Message explicatif (obligatoire)"
                                className="w-full sm:w-64 border border-slate-200 rounded-xl px-3 py-2 text-sm focus:ring-2 focus:ring-blue-600/40 focus:border-blue-600 outline-none"
                              />
                            </div>
                          )}
                        </div>
                      )}

                      {estResolu && (
                        <span className="flex items-center gap-1.5 text-sm font-semibold text-green-600 shrink-0">
                          <CheckCircle2 className="w-4 h-4" />
                          Terminé
                        </span>
                      )}
                    </div>

                    {ouvert === a.idSignalement && (
                      <div className="mt-4 pt-4 border-t border-slate-100">
                        {chargementDetails === a.idSignalement ? (
                          <div className="flex items-center gap-2 text-sm text-slate-400 py-2">
                            <Loader2 className="w-4 h-4 animate-spin" />
                            Chargement des détails...
                          </div>
                        ) : details[a.idSignalement]?.erreur ? (
                          <p className="text-sm text-red-600">Impossible de charger les détails.</p>
                        ) : (
                          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <div>
                              <h4 className="text-xs font-semibold text-slate-500 uppercase mb-1.5">
                                Description
                              </h4>
                              <p className="text-sm text-slate-700 whitespace-pre-wrap">
                                {details[a.idSignalement]?.description || 'Aucune description fournie.'}
                              </p>

                              <h4 className="text-xs font-semibold text-slate-500 uppercase mt-4 mb-1.5">
                                Adresse
                              </h4>
                              <p className="text-sm text-slate-700">
                                {details[a.idSignalement]?.adresse || 'Non précisée'}
                              </p>
                              {details[a.idSignalement]?.latitude && details[a.idSignalement]?.longitude && (
                                <a
                                  href={`https://www.google.com/maps?q=${details[a.idSignalement].latitude},${details[a.idSignalement].longitude}`}
                                  target="_blank"
                                  rel="noreferrer"
                                  className="inline-flex items-center gap-1 text-xs text-blue-700 hover:underline mt-1"
                                >
                                  <MapPin className="w-3.5 h-3.5" />
                                  Voir sur la carte
                                </a>
                              )}
                            </div>

                            <div>
                              <h4 className="text-xs font-semibold text-slate-500 uppercase mb-1.5">
                                Photo {!details[a.idSignalement]?.photoUrl && '(non fournie)'}
                              </h4>
                              {details[a.idSignalement]?.photoUrl ? (
                                <img
                                  src={
                                    details[a.idSignalement].photoUrl.startsWith('http')
                                      ? details[a.idSignalement].photoUrl
                                      : `${signalementClient.defaults.baseURL}/signalements/photos/${details[a.idSignalement].photoUrl.split('/').filter(Boolean).pop()}`
                                  }
                                  alt="Photo du signalement"
                                  className="w-full max-h-48 object-cover rounded-xl border border-slate-100"
                                />
                              ) : (
                                <div className="flex items-center justify-center h-32 bg-slate-50 rounded-xl text-slate-300">
                                  <ImageOff className="w-8 h-8" />
                                </div>
                              )}
                            </div>
                          </div>
                        )}
                      </div>
                    )}
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