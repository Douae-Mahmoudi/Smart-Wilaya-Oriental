import React, { useState, useEffect, useCallback, useMemo } from 'react';
import {
  PlusCircle, RefreshCw, Search, X, AlertTriangle, Pencil,
  Users2, MapPin, CheckCircle2, Info, LayoutGrid,
} from 'lucide-react';
import { ressourceClient as apiClient } from '../../../api/client';

const CATEGORIES_INTERVENTION = ['EAU', 'ELECTRICITE', 'VOIRIE', 'PROPRETE'];
const STATUTS_EQUIPE = ['DISPONIBLE', 'EN_INTERVENTION', 'HORS_SERVICE'];

const STATUTS_FILTRE = [
  { key: 'TOUS', label: 'Tous' },
  { key: 'DISPONIBLE', label: 'Disponibles' },
  { key: 'EN_INTERVENTION', label: 'En intervention' },
  { key: 'HORS_SERVICE', label: 'Hors service' },
];

const STATUT_BADGE_STYLES = {
  DISPONIBLE: 'bg-emerald-50 text-emerald-700 ring-1 ring-emerald-600/10',
  EN_INTERVENTION: 'bg-amber-50 text-amber-700 ring-1 ring-amber-600/10',
  HORS_SERVICE: 'bg-red-50 text-red-700 ring-1 ring-red-600/10',
};

const STATUT_LABELS = {
  DISPONIBLE: 'Disponible',
  EN_INTERVENTION: 'En intervention',
  HORS_SERVICE: 'Hors service',
};

const COMPETENCE_STYLES = {
  EAU: 'bg-sky-50 text-sky-700',
  ELECTRICITE: 'bg-amber-50 text-amber-700',
  VOIRIE: 'bg-slate-100 text-slate-600',
  PROPRETE: 'bg-emerald-50 text-emerald-700',
};

const COULEURS_AVATAR = [
  'bg-blue-100 text-blue-700',
  'bg-violet-100 text-violet-700',
  'bg-teal-100 text-teal-700',
  'bg-amber-100 text-amber-700',
  'bg-rose-100 text-rose-700',
];

function couleurAvatar(id) {
  if (!id) return COULEURS_AVATAR[0];
  const somme = String(id).split('').reduce((acc, c) => acc + c.charCodeAt(0), 0);
  return COULEURS_AVATAR[somme % COULEURS_AVATAR.length];
}

function initialesEquipe(nom) {
  if (!nom) return '?';
  const mots = nom.trim().split(/\s+/);
  return mots.length > 1
    ? (mots[0][0] + mots[1][0]).toUpperCase()
    : nom.slice(0, 2).toUpperCase();
}

export default function EquipesTab() {
  const [toutesLesEquipes, setToutesLesEquipes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [erreurListe, setErreurListe] = useState(null);
  const [recherche, setRecherche] = useState('');
  const [filtreStatut, setFiltreStatut] = useState('TOUS');

  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  const [modalCreationOuverte, setModalCreationOuverte] = useState(false);
  const [formCreation, setFormCreation] = useState({ nom: '', competences: [], zoneCouverture: '' });
  const [creating, setCreating] = useState(false);

  const [equipeASupprimer, setEquipeASupprimer] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const [equipeAModifier, setEquipeAModifier] = useState(null);
  const [statutChoisi, setStatutChoisi] = useState('');
  const [changingStatut, setChangingStatut] = useState(false);

  const chargerToutesLesEquipes = useCallback(async () => {
    setLoading(true);
    setErreurListe(null);
    try {
      const { data } = await apiClient.get('/equipes');
      setToutesLesEquipes(data);
    } catch (err) {
      if (err.response?.status === 403 || err.response?.status === 401) {
        setErreurListe("Accès réservé aux administrateurs.");
      } else {
        setErreurListe(err.response?.data?.message || 'Erreur lors du chargement des équipes.');
      }
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { chargerToutesLesEquipes(); }, [chargerToutesLesEquipes]);

  useEffect(() => {
    if (!message) return;
    const t = setTimeout(() => setMessage(null), 4000);
    return () => clearTimeout(t);
  }, [message]);

  const compteurs = useMemo(() => {
    const base = { DISPONIBLE: 0, EN_INTERVENTION: 0, HORS_SERVICE: 0 };
    toutesLesEquipes.forEach((eq) => {
      if (base[eq.statut] !== undefined) base[eq.statut]++;
    });
    return base;
  }, [toutesLesEquipes]);

  const equipesFiltrees = useMemo(() => {
    const q = recherche.trim().toLowerCase();
    return toutesLesEquipes
      .filter((eq) => filtreStatut === 'TOUS' || eq.statut === filtreStatut)
      .filter((eq) => {
        if (!q) return true;
        const competencesTexte = Array.isArray(eq.competences) ? eq.competences.join(' ') : '';
        return [eq.nom, eq.zoneCouverture, competencesTexte]
          .filter(Boolean)
          .some((v) => v.toLowerCase().includes(q));
      });
  }, [toutesLesEquipes, recherche, filtreStatut]);

  const toggleCompetence = (categorie) => {
    setFormCreation((prev) => {
      const dejaPresente = prev.competences.includes(categorie);
      return {
        ...prev,
        competences: dejaPresente
          ? prev.competences.filter((c) => c !== categorie)
          : [...prev.competences, categorie],
      };
    });
  };

  const creerEquipe = async () => {
    if (!formCreation.nom.trim() || formCreation.competences.length === 0 || !formCreation.zoneCouverture.trim()) {
      setError('Nom, au moins une compétence et zone de couverture sont requis.');
      return;
    }
    setCreating(true);
    setError(null);
    try {
      await apiClient.post('/equipes', formCreation);
      setMessage('Équipe créée avec succès.');
      setFormCreation({ nom: '', competences: [], zoneCouverture: '' });
      setModalCreationOuverte(false);
      chargerToutesLesEquipes();
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la création.');
    } finally {
      setCreating(false);
    }
  };

  const demanderSuppression = (equipe) => { setError(null); setEquipeASupprimer(equipe); };
  const annulerSuppression = () => { if (!deleting) setEquipeASupprimer(null); };

  const confirmerSuppression = async () => {
    if (!equipeASupprimer) return;
    setDeleting(true);
    setError(null);
    try {
      await apiClient.delete(`/equipes/${equipeASupprimer.id}`);
      setMessage('Équipe supprimée avec succès.');
      setEquipeASupprimer(null);
      chargerToutesLesEquipes();
    } catch (err) {
      if (err.response?.status === 403 || err.response?.status === 401) {
        setError("Accès réservé aux administrateurs.");
      } else {
        setError(err.response?.data?.message || "Erreur lors de la suppression de l'équipe.");
      }
    } finally {
      setDeleting(false);
    }
  };

  const demanderChangementStatut = (equipe) => {
    setError(null);
    setStatutChoisi(equipe.statut || '');
    setEquipeAModifier(equipe);
  };
  const annulerChangementStatut = () => {
    if (changingStatut) return;
    setEquipeAModifier(null);
    setStatutChoisi('');
  };

  const confirmerChangementStatut = async () => {
    if (!equipeAModifier || !statutChoisi) return;
    setChangingStatut(true);
    setError(null);
    try {
      await apiClient.patch(`/equipes/${equipeAModifier.id}/statut`, { statut: statutChoisi });
      setMessage("Statut de l'équipe mis à jour.");
      setEquipeAModifier(null);
      setStatutChoisi('');
      chargerToutesLesEquipes();
    } catch (err) {
      if (err.response?.status === 403 || err.response?.status === 401) {
        setError("Accès réservé aux administrateurs.");
      } else {
        setError(err.response?.data?.message || 'Erreur lors de la mise à jour du statut.');
      }
    } finally {
      setChangingStatut(false);
    }
  };

  return (
    <div className="space-y-6">

      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
        </div>
        <button
          onClick={() => setModalCreationOuverte(true)}
          className="flex items-center gap-2 bg-blue-700 hover:bg-blue-800 text-white px-4 py-2.5 rounded-xl text-sm font-semibold shadow-sm shadow-blue-700/20 transition"
        >
          <PlusCircle className="w-4 h-4" />
          Nouvelle équipe
        </button>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white rounded-2xl shadow-sm ring-1 ring-slate-900/5 p-5">
          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide">Total équipes</p>
          <p className="text-2xl font-bold text-slate-900 mt-1.5">{toutesLesEquipes.length || '—'}</p>
        </div>
        {STATUTS_FILTRE.slice(1).map(({ key, label }) => (
          <button
            key={key}
            onClick={() => setFiltreStatut(key)}
            className={`text-left bg-white rounded-2xl shadow-sm ring-1 p-5 transition ${
              filtreStatut === key ? 'ring-blue-600/30 bg-blue-50/40' : 'ring-slate-900/5 hover:ring-slate-900/10'
            }`}
          >
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide">{label}</p>
            <p className="text-2xl font-bold text-slate-900 mt-1.5">{compteurs[key]}</p>
          </button>
        ))}
      </div>

      {error && (
        <div className="flex items-center gap-2.5 bg-red-50 text-red-700 text-sm rounded-xl px-4 py-3 ring-1 ring-red-600/10">
          <AlertTriangle className="w-4 h-4 shrink-0" />
          {error}
        </div>
      )}
      {message && (
        <div className="flex items-center gap-2.5 bg-emerald-50 text-emerald-700 text-sm rounded-xl px-4 py-3 ring-1 ring-emerald-600/10">
          <CheckCircle2 className="w-4 h-4 shrink-0" />
          {message}
        </div>
      )}

      <div className="grid grid-cols-1 xl:grid-cols-12 gap-6 items-start">

        <div className="xl:col-span-8 bg-white rounded-2xl shadow-sm ring-1 ring-slate-900/5 overflow-hidden">
          <div className="flex flex-wrap items-center justify-between gap-4 px-6 pt-5 pb-3.5">
            <h2 className="text-sm font-bold text-slate-900 flex items-center gap-2">
              <LayoutGrid className="w-4 h-4 text-slate-400" />
              Liste des équipes
            </h2>
            <div className="flex items-center gap-2">
              <div className="relative w-48">
                <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none" />
                <input
                  type="text"
                  value={recherche}
                  onChange={(e) => setRecherche(e.target.value)}
                  placeholder="Rechercher..."
                  className="w-full pl-8 pr-3 py-2 text-sm border border-slate-200 rounded-lg outline-none focus:ring-2 focus:ring-blue-600/30 focus:border-blue-400 transition"
                />
              </div>
              <button
                onClick={chargerToutesLesEquipes}
                disabled={loading}
                title="Actualiser"
                className="w-9 h-9 flex items-center justify-center rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition disabled:opacity-60"
              >
                <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
              </button>
            </div>
          </div>

          <div className="border-t border-slate-100" />

          <div className="flex items-center gap-1.5 px-6 py-3 border-b border-slate-100">
            {STATUTS_FILTRE.map(({ key, label }) => (
              <button
                key={key}
                onClick={() => setFiltreStatut(key)}
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition ${
                  filtreStatut === key ? 'bg-slate-900 text-white' : 'text-slate-500 hover:bg-slate-100'
                }`}
              >
                {label}
              </button>
            ))}
            <span className="ml-auto text-xs text-slate-400 font-medium">
              {equipesFiltrees.length} résultat{equipesFiltrees.length > 1 ? 's' : ''}
            </span>
          </div>

          {erreurListe && (
            <div className="flex items-center gap-2.5 bg-red-50 text-red-700 text-sm px-6 py-3">
              <AlertTriangle className="w-4 h-4 shrink-0" />
              {erreurListe}
            </div>
          )}

          {loading ? (
            <div className="p-6 space-y-3">
              {Array.from({ length: 4 }).map((_, i) => (
                <div key={i} className="h-14 rounded-lg bg-slate-50 animate-pulse" />
              ))}
            </div>
          ) : equipesFiltrees.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-slate-400 text-[11px] font-semibold uppercase tracking-wide">
                    <th className="py-3 pl-6 pr-4">Équipe</th>
                    <th className="py-3 pr-4">Compétences</th>
                    <th className="py-3 pr-4">Statut</th>
                    <th className="py-3 pr-6 text-right">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {equipesFiltrees.map((eq) => (
                    <tr key={eq.id} className="border-t border-slate-50 hover:bg-slate-50/70 transition-colors">
                      <td className="py-3.5 pl-6 pr-4">
                        <div className="flex items-center gap-3">
                          <div className={`w-9 h-9 rounded-full flex items-center justify-center text-xs font-bold shrink-0 ${couleurAvatar(eq.id)}`}>
                            {initialesEquipe(eq.nom)}
                          </div>
                          <div className="min-w-0">
                            <p className="text-slate-900 font-medium truncate">{eq.nom}</p>
                            <p className="text-slate-500 text-xs truncate flex items-center gap-1">
                              <MapPin className="w-3 h-3 shrink-0" />
                              {eq.zoneCouverture}
                            </p>
                          </div>
                        </div>
                      </td>
                      <td className="py-3.5 pr-4">
                        <div className="flex flex-wrap gap-1.5 max-w-[220px]">
                          {Array.isArray(eq.competences) && eq.competences.map((c) => (
                            <span
                              key={c}
                              className={`px-2 py-0.5 rounded-md text-[11px] font-semibold ${COMPETENCE_STYLES[c] || 'bg-slate-100 text-slate-600'}`}
                            >
                              {c}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td className="py-3.5 pr-4">
                        <span className={`px-2.5 py-1 rounded-full text-[11px] font-semibold ${STATUT_BADGE_STYLES[eq.statut] || 'bg-slate-100 text-slate-600'}`}>
                          {STATUT_LABELS[eq.statut] || eq.statut}
                        </span>
                      </td>
                      <td className="py-3.5 pr-6 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <button
                            onClick={() => demanderChangementStatut(eq)}
                            title="Changer le statut"
                            className="w-8 h-8 inline-flex items-center justify-center rounded-lg text-slate-400 hover:text-blue-700 hover:bg-blue-50 transition"
                          >
                            <Pencil className="w-4 h-4" />
                          </button>
                          <button
                            onClick={() => demanderSuppression(eq)}
                            title="Supprimer"
                            className="w-8 h-8 inline-flex items-center justify-center rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 transition"
                          >
                            <X className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            !erreurListe && (
              <div className="flex flex-col items-center justify-center py-16 px-6 text-center">
                <div className="w-12 h-12 rounded-full bg-slate-50 flex items-center justify-center mb-3">
                  <Users2 className="w-5 h-5 text-slate-300" />
                </div>
                <p className="text-sm font-medium text-slate-600">
                  {recherche || filtreStatut !== 'TOUS' ? 'Aucun résultat pour ces critères' : "Aucune équipe pour l'instant"}
                </p>
                {!recherche && filtreStatut === 'TOUS' && (
                  <p className="text-xs text-slate-400 mt-1">
                    Cliquez sur « Nouvelle équipe » pour en créer une.
                  </p>
                )}
              </div>
            )
          )}
        </div>

        <aside className="xl:col-span-4 bg-white rounded-2xl shadow-sm ring-1 ring-slate-900/5 p-6 space-y-5">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-xl bg-blue-50 flex items-center justify-center shrink-0">
              <Info className="w-4.5 h-4.5 text-blue-700" />
            </div>
            <h2 className="text-sm font-bold text-slate-900">Statuts des équipes</h2>
          </div>

          <ul className="space-y-3">
            <li className="flex items-start gap-2.5 text-sm text-slate-600">
              <span className="w-2 h-2 rounded-full bg-emerald-500 mt-1.5 shrink-0" />
              <span><span className="font-semibold text-slate-800">Disponible</span> — l'équipe peut être affectée à une nouvelle intervention.</span>
            </li>
            <li className="flex items-start gap-2.5 text-sm text-slate-600">
              <span className="w-2 h-2 rounded-full bg-amber-500 mt-1.5 shrink-0" />
              <span><span className="font-semibold text-slate-800">En intervention</span> — l'équipe est actuellement mobilisée sur le terrain.</span>
            </li>
            <li className="flex items-start gap-2.5 text-sm text-slate-600">
              <span className="w-2 h-2 rounded-full bg-red-500 mt-1.5 shrink-0" />
              <span><span className="font-semibold text-slate-800">Hors service</span> — l'équipe est indisponible (congé, maintenance, etc.).</span>
            </li>
          </ul>

          <div className="flex items-start gap-2.5 bg-slate-50 rounded-xl px-4 py-3.5">
            <Info className="w-4 h-4 text-slate-400 mt-0.5 shrink-0" />
            <p className="text-xs text-slate-500 leading-relaxed">
              Une équipe doit couvrir au moins une compétence pour pouvoir être créée.
            </p>
          </div>
        </aside>
      </div>

      {modalCreationOuverte && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full">
            <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
              <h3 className="text-base font-bold text-slate-900">Nouvelle équipe</h3>
              <button
                onClick={() => setModalCreationOuverte(false)}
                className="w-8 h-8 flex items-center justify-center rounded-full text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="px-6 py-5 space-y-4">
              <div>
                <label className="block text-xs font-medium text-slate-500 mb-1">Nom de l'équipe</label>
                <input
                  type="text"
                  value={formCreation.nom}
                  onChange={(e) => setFormCreation({ ...formCreation, nom: e.target.value })}
                  className="w-full border border-slate-200 rounded-xl px-3.5 py-2.5 text-sm outline-none focus:ring-2 focus:ring-blue-600/30 focus:border-blue-400 transition"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-500 mb-1">Zone de couverture</label>
                <input
                  type="text"
                  value={formCreation.zoneCouverture}
                  onChange={(e) => setFormCreation({ ...formCreation, zoneCouverture: e.target.value })}
                  className="w-full border border-slate-200 rounded-xl px-3.5 py-2.5 text-sm outline-none focus:ring-2 focus:ring-blue-600/30 focus:border-blue-400 transition"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-500 mb-2">Compétences</label>
                <div className="flex flex-wrap gap-2">
                  {CATEGORIES_INTERVENTION.map((categorie) => {
                    const active = formCreation.competences.includes(categorie);
                    return (
                      <button
                        key={categorie}
                        type="button"
                        onClick={() => toggleCompetence(categorie)}
                        className={`px-3.5 py-2 rounded-lg text-xs font-semibold border transition ${
                          active
                            ? 'bg-blue-700 text-white border-blue-700'
                            : 'bg-white text-slate-600 border-slate-200 hover:bg-slate-50'
                        }`}
                      >
                        {categorie}
                      </button>
                    );
                  })}
                </div>
              </div>
            </div>

            <div className="flex justify-end gap-3 px-6 py-4 border-t border-slate-100">
              <button
                onClick={() => setModalCreationOuverte(false)}
                disabled={creating}
                className="px-4 py-2.5 rounded-xl text-sm font-semibold text-slate-600 hover:bg-slate-100 transition disabled:opacity-60"
              >
                Annuler
              </button>
              <button
                onClick={creerEquipe}
                disabled={creating}
                className="px-5 py-2.5 rounded-xl text-sm font-semibold bg-blue-700 hover:bg-blue-800 disabled:opacity-60 text-white transition"
              >
                {creating ? 'Création...' : "Créer l'équipe"}
              </button>
            </div>
          </div>
        </div>
      )}

      {equipeAModifier && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4" onClick={annulerChangementStatut}>
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
              <h3 className="text-base font-bold text-slate-900">Changer le statut</h3>
              <button
                onClick={annulerChangementStatut}
                className="w-8 h-8 flex items-center justify-center rounded-full text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="px-6 py-5">
              <p className="text-sm text-slate-500 mb-5">
                Équipe <span className="font-semibold text-slate-800">{equipeAModifier.nom}</span> · {equipeAModifier.zoneCouverture}
              </p>

              <label className="block text-xs font-medium text-slate-500 mb-2">Nouveau statut</label>
              <div className="space-y-2">
                {STATUTS_EQUIPE.map((statut) => (
                  <button
                    key={statut}
                    onClick={() => setStatutChoisi(statut)}
                    className={`w-full flex items-center justify-between px-4 py-3 rounded-xl border text-sm font-medium transition ${
                      statutChoisi === statut
                        ? 'border-blue-600 bg-blue-50/60 text-blue-800'
                        : 'border-slate-200 text-slate-600 hover:bg-slate-50'
                    }`}
                  >
                    {STATUT_LABELS[statut]}
                    {statutChoisi === statut && <CheckCircle2 className="w-4 h-4 text-blue-700" />}
                  </button>
                ))}
              </div>
            </div>

            <div className="flex justify-end gap-3 px-6 py-4 border-t border-slate-100">
              <button
                onClick={annulerChangementStatut}
                disabled={changingStatut}
                className="px-4 py-2.5 rounded-xl text-sm font-semibold text-slate-600 hover:bg-slate-100 transition disabled:opacity-60"
              >
                Annuler
              </button>
              <button
                onClick={confirmerChangementStatut}
                disabled={changingStatut || !statutChoisi || statutChoisi === equipeAModifier.statut}
                className="px-5 py-2.5 rounded-xl text-sm font-semibold text-white bg-blue-700 hover:bg-blue-800 disabled:opacity-60 transition"
              >
                {changingStatut ? 'Mise à jour...' : 'Mettre à jour'}
              </button>
            </div>
          </div>
        </div>
      )}

      {equipeASupprimer && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4" onClick={annulerSuppression}>
          <div className="bg-white rounded-2xl shadow-2xl max-w-sm w-full p-6" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center gap-3 mb-3">
              <div className="w-10 h-10 rounded-full bg-red-50 flex items-center justify-center shrink-0">
                <AlertTriangle className="w-5 h-5 text-red-600" />
              </div>
              <h3 className="text-base font-bold text-slate-900">Supprimer l'équipe</h3>
            </div>
            <p className="text-sm text-slate-600 mb-6">
              Voulez-vous vraiment supprimer <span className="font-semibold text-slate-800">{equipeASupprimer.nom}</span> ? Cette action est irréversible.
            </p>
            <div className="flex justify-end gap-3">
              <button
                onClick={annulerSuppression}
                disabled={deleting}
                className="px-4 py-2.5 rounded-xl text-sm font-semibold text-slate-600 hover:bg-slate-100 transition disabled:opacity-60"
              >
                Annuler
              </button>
              <button
                onClick={confirmerSuppression}
                disabled={deleting}
                className="px-4 py-2.5 rounded-xl text-sm font-semibold bg-red-600 hover:bg-red-700 disabled:opacity-60 text-white transition"
              >
                {deleting ? 'Suppression...' : 'Supprimer'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}