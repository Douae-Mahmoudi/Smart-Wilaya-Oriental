import React, { useState, useEffect, useCallback } from 'react';
import {
  ClipboardList,
  CheckCircle2,
  XCircle,
  Loader2,
  RefreshCw,
  MapPin,
  Clock,
  AlertTriangle,
  Users,
  FileText,
  Droplet,
  Lightbulb,
  Construction,
  Trash2,
  Trees,
} from 'lucide-react';
import { affectationClient as apiClient, utilisateurClient, ressourceClient } from '../../../api/client';
 
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
 
function MotifDiscret() {
  return (
    <svg className="absolute inset-0 h-full w-full" preserveAspectRatio="xMidYMid slice" aria-hidden="true">
      <defs>
        <pattern id="motif-equipe" width="40" height="40" patternUnits="userSpaceOnUse">
          <g fill="none" stroke="#D4AF37" strokeWidth="0.6" strokeOpacity="0.3">
            <path d="M20 0 L40 20 L20 40 L0 20 Z" />
            <circle cx="20" cy="20" r="3" />
          </g>
        </pattern>
      </defs>
      <rect width="100%" height="100%" fill="url(#motif-equipe)" />
    </svg>
  );
}
 
export default function AffectationsTab() {
  const [idEquipe, setIdEquipe] = useState(null);
  const [nomEquipe, setNomEquipe] = useState(null);
  const [loadingEquipe, setLoadingEquipe] = useState(true);
  const [erreurEquipe, setErreurEquipe] = useState(null);
 
  const [affectations, setAffectations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [traitementId, setTraitementId] = useState(null);
 
  const chargerMonEquipe = useCallback(async () => {
    setLoadingEquipe(true);
    setErreurEquipe(null);
    try {
      const { data } = await utilisateurClient.get('/utilisateurs/moi/equipe');
      setIdEquipe(data.idEquipe);
 
      try {
        const { data: equipes } = await ressourceClient.get('/equipes');
        const equipe = equipes.find((e) => e.id === data.idEquipe);
        setNomEquipe(equipe ? (equipe.nom || equipe.name) : null);
      } catch (err) {
      }
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
      const { data } = await apiClient.get('/affectations/mes-affectations', {
        params: { idEquipe: equipe },
      });
      setAffectations(data);
    } catch (err) {
      setError(err.response?.data?.message || 'Impossible de charger vos affectations.');
    } finally {
      setLoading(false);
    }
  }, []);
 
  useEffect(() => { chargerMonEquipe(); }, [chargerMonEquipe]);
  useEffect(() => { if (idEquipe) charger(idEquipe); }, [idEquipe, charger]);
 
  const enAttente = affectations.filter((a) => a.statut === 'EN_ATTENTE');
 
  const repondre = async (affectation, decision) => {
    setTraitementId(affectation.id);
    setError(null);
    try {
      const chemin = decision === 'ACCEPTER' ? 'accepter' : 'refuser';
      await apiClient.post(`/affectations/${affectation.id}/${chemin}`, {
        idEquipe: affectation.idEquipeProposee,
      });
      await charger(idEquipe);
    } catch (err) {
      setError(err.response?.data?.message || "Impossible d'enregistrer votre réponse.");
    } finally {
      setTraitementId(null);
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
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,500;9..144,600&family=Inter:wght@400;500;600;700&display=swap');
        .font-display { font-family: 'Fraunces', serif; }
      `}</style>
 
      <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-[#0F1E3D] to-[#16295A] p-5 flex items-center gap-4 shadow-lg shadow-blue-900/10">
        <MotifDiscret />
        <div className="relative w-11 h-11 rounded-xl bg-white/10 border border-[#D4AF37]/40 flex items-center justify-center shrink-0">
          <Users className="w-5 h-5 text-[#E8C767]" />
        </div>
        <div className="relative">
          <p className="text-[11px] text-white/50 font-bold uppercase tracking-wider">Mon équipe</p>
          <p className="font-display text-base font-semibold text-white">{nomEquipe || idEquipe}</p>
        </div>
      </div>
 
      {error && (
        <div className="flex items-start gap-2.5 px-4 py-3 rounded-xl bg-red-50 border border-red-100">
          <AlertTriangle className="w-4 h-4 text-red-600 mt-0.5 shrink-0" />
          <p className="text-sm text-red-700 font-medium">{error}</p>
        </div>
      )}
 
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div className="bg-white rounded-2xl shadow-xl p-5 flex items-center gap-4">
          <div className="w-11 h-11 rounded-xl flex items-center justify-center text-amber-700 bg-amber-50">
            <Clock className="w-5 h-5" />
          </div>
          <div>
            <p className="text-2xl font-bold text-slate-900">{loading ? '—' : enAttente.length}</p>
            <p className="text-xs text-slate-500 font-medium">En attente de réponse</p>
          </div>
        </div>
        <div className="bg-white rounded-2xl shadow-xl p-5 flex items-center gap-4">
          <div className="w-11 h-11 rounded-xl flex items-center justify-center text-blue-700 bg-blue-50">
            <ClipboardList className="w-5 h-5" />
          </div>
          <div>
            <p className="text-2xl font-bold text-slate-900">{loading ? '—' : affectations.length}</p>
            <p className="text-xs text-slate-500 font-medium">Total reçu par votre équipe</p>
          </div>
        </div>
      </div>
 
      <div className="bg-white rounded-2xl shadow-xl p-6 md:p-8">
        <div className="flex items-center justify-between mb-5">
          <h2 className="font-display text-lg font-semibold text-slate-900 flex items-center gap-2">
            <ClipboardList className="w-5 h-5 text-blue-700" />
            Affectations en attente
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
        ) : enAttente.length === 0 ? (
          <div className="text-center py-14">
            <div className="w-12 h-12 rounded-full bg-slate-50 flex items-center justify-center mx-auto mb-3">
              <ClipboardList className="w-5 h-5 text-slate-300" />
            </div>
            <p className="text-sm text-slate-400">
              File d'attente vide. Les prochaines propositions pour votre équipe s'afficheront ici.
            </p>
          </div>
        ) : (
          <div className="space-y-4">
            {enAttente.map((a) => {
              const enTraitement = traitementId === a.id;
              const IconeCategorie = ICONE_CATEGORIE[a.categorie] || ClipboardList;
              const couleurGravite = COULEUR_GRAVITE[a.gravite] || '#94a3b8';
 
              return (
                <div
                  key={a.id}
                  className="relative flex rounded-2xl border border-slate-200 hover:border-slate-300 hover:shadow-md transition-all duration-200 overflow-hidden"
                >
                  <div className="w-1.5 shrink-0" style={{ backgroundColor: couleurGravite }} aria-hidden="true" />
 
                  <div className="flex-1 flex flex-col md:flex-row md:items-start gap-4 md:gap-6 p-5">
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 mb-2">
                        <div
                          className="w-8 h-8 rounded-lg flex items-center justify-center shrink-0"
                          style={{ color: couleurGravite, backgroundColor: `${couleurGravite}15` }}
                        >
                          <IconeCategorie className="w-4 h-4" />
                        </div>
                        <span className="text-sm font-bold text-blue-700">
                          Signalement #{a.idSignalement?.slice(0, 8) || '—'}
                        </span>
                        {a.gravite && (
                          <span
                            className="px-2 py-0.5 rounded-full text-[11px] font-semibold"
                            style={{ color: couleurGravite, backgroundColor: `${couleurGravite}15` }}
                          >
                            {a.gravite}
                          </span>
                        )}
                      </div>
 
                      <p className="text-sm font-semibold text-slate-800 mb-1 ml-10">{a.categorie || 'Type inconnu'}</p>
 
                      {a.description && (
                        <p className="flex items-start gap-1.5 text-sm text-slate-600 mb-2 ml-10 leading-relaxed">
                          <FileText className="w-3.5 h-3.5 text-slate-400 mt-0.5 shrink-0" />
                          {a.description}
                        </p>
                      )}
 
                      <div className="flex flex-wrap items-center gap-4 text-xs text-slate-400 ml-10">
                        <span className="flex items-center gap-1.5">
                          <MapPin className="w-3.5 h-3.5" />
                          {a.adresse ? `${a.adresse}, ${a.zone || ''}` : a.zone || 'Zone non précisée'}
                        </span>
                        {a.dateProposition && (
                          <span className="flex items-center gap-1.5">
                            <Clock className="w-3.5 h-3.5" />
                            Proposée le {new Date(a.dateProposition).toLocaleString('fr-FR')}
                          </span>
                        )}
                      </div>
                    </div>
 
                    <div className="flex items-center gap-2 shrink-0">
                      <button
                        onClick={() => repondre(a, 'REFUSER')}
                        disabled={enTraitement}
                        className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl text-sm font-semibold text-red-600 border border-red-200 hover:bg-red-50 disabled:opacity-50 transition"
                      >
                        <XCircle className="w-4 h-4" />
                        Refuser
                      </button>
                      <button
                        onClick={() => repondre(a, 'ACCEPTER')}
                        disabled={enTraitement}
                        className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl text-sm font-semibold bg-blue-700 hover:bg-blue-800 disabled:opacity-60 text-white transition"
                      >
                        {enTraitement ? (
                          <Loader2 className="w-4 h-4 animate-spin" />
                        ) : (
                          <CheckCircle2 className="w-4 h-4" />
                        )}
                        Accepter
                      </button>
                    </div>
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
 














































































