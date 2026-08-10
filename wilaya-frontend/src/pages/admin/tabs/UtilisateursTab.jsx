import React, { useState, useEffect, useCallback, useMemo } from 'react';
import {
  UserPlus, Trash2, Search, X, CheckCircle2, AlertCircle,
  Users2, ShieldCheck, Eye, ShieldQuestion, Info,
} from 'lucide-react';
import { utilisateurClient as apiClient, ressourceClient } from '../../../api/client';

const SOUS_ONGLETS = [
  { key: 'agents', label: 'Agents', icon: Users2 },
  { key: 'superviseurs', label: 'Superviseurs', icon: Eye },
  { key: 'admins', label: 'Admins', icon: ShieldCheck },
];

const LABEL_SINGULIER = { agents: 'agent', superviseurs: 'superviseur', admins: 'admin' };
const LABEL_ARTICLE = { agents: 'un agent', superviseurs: 'un superviseur', admins: 'un admin' };

const ROLE_INFOS = {
  agents: {
    titre: 'Agents',
    description:
      "Les agents sont rattachés à une équipe et interviennent sur le terrain. Ils peuvent consulter et mettre à jour le statut des signalements qui leur sont affectés.",
    permissions: ['Voir les signalements de leur équipe', 'Changer le statut d’une intervention', 'Mettre à jour leur profil'],
  },
  superviseurs: {
    titre: 'Superviseurs',
    description:
      "Les superviseurs pilotent une ou plusieurs équipes. Ils suivent l'ensemble des signalements et coordonnent les interventions des agents.",
    permissions: ['Voir tous les signalements', 'Affecter des équipes', 'Lister les agents et superviseurs'],
  },
  admins: {
    titre: 'Admins',
    description:
      "Les administrateurs gèrent la plateforme dans son ensemble : comptes, équipes, matériel et supervision globale des activités.",
    permissions: ['Gérer tous les comptes utilisateurs', 'Gérer les équipes et le matériel', 'Accès complet au tableau de bord'],
  },
};

const STATUTS_FILTRE = [
  { key: 'TOUS', label: 'Tous' },
  { key: 'ACTIF', label: 'Actifs' },
  { key: 'INACTIF', label: 'Inactifs' },
];

const STATUT_STYLES = {
  ACTIF: 'bg-emerald-50 text-emerald-700 ring-1 ring-emerald-600/10',
  INACTIF: 'bg-slate-100 text-slate-600 ring-1 ring-slate-500/10',
};

const COULEURS_AVATAR = [
  'bg-blue-100 text-blue-700',
  'bg-violet-100 text-violet-700',
  'bg-teal-100 text-teal-700',
  'bg-amber-100 text-amber-700',
  'bg-rose-100 text-rose-700',
];

function initiales(nom, prenom) {
  return `${prenom?.charAt(0) || ''}${nom?.charAt(0) || ''}`.toUpperCase() || '?';
}

function couleurAvatar(idProfil) {
  if (!idProfil) return COULEURS_AVATAR[0];
  const somme = idProfil.split('').reduce((acc, c) => acc + c.charCodeAt(0), 0);
  return COULEURS_AVATAR[somme % COULEURS_AVATAR.length];
}

export default function UtilisateursTab() {
  const [sousOnglet, setSousOnglet] = useState('agents');
  const [utilisateurs, setUtilisateurs] = useState([]);
  const [equipes, setEquipes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);
  const [recherche, setRecherche] = useState('');
  const [filtreStatut, setFiltreStatut] = useState('TOUS');

  const [compteurs, setCompteurs] = useState({ agents: null, superviseurs: null, admins: null });

  const [modalOuverte, setModalOuverte] = useState(false);
  const [form, setForm] = useState({ nom: '', prenom: '', email: '', telephone: '', idEquipe: '' });
  const [creating, setCreating] = useState(false);

  const [aSupprimer, setASupprimer] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const chargerEquipes = useCallback(async () => {
    try {
      const { data } = await ressourceClient.get('/equipes');
      setEquipes(data);
    } catch (err) {
    }
  }, []);

  const chargerListe = useCallback(async (onglet) => {
    setError(null);
    setLoading(true);
    try {
      const { data } = await apiClient.get(`/utilisateurs/${onglet}`);
      setUtilisateurs(data);
      setCompteurs((prev) => ({ ...prev, [onglet]: data.length }));
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors du chargement.');
    } finally {
      setLoading(false);
    }
  }, []);

  const chargerTousLesCompteurs = useCallback(async () => {
    const resultats = await Promise.allSettled(
      SOUS_ONGLETS.map(({ key }) => apiClient.get(`/utilisateurs/${key}`))
    );
    const next = {};
    resultats.forEach((res, i) => {
      const key = SOUS_ONGLETS[i].key;
      next[key] = res.status === 'fulfilled' ? res.value.data.length : null;
    });
    setCompteurs(next);
  }, []);

  useEffect(() => { chargerEquipes(); }, [chargerEquipes]);
  useEffect(() => { chargerTousLesCompteurs(); }, [chargerTousLesCompteurs]);
  useEffect(() => { chargerListe(sousOnglet); setRecherche(''); setFiltreStatut('TOUS'); }, [sousOnglet, chargerListe]);

  useEffect(() => {
    if (!message) return;
    const t = setTimeout(() => setMessage(null), 4000);
    return () => clearTimeout(t);
  }, [message]);

  const nomEquipe = (id) => {
    if (!id) return '—';
    const equipe = equipes.find((e) => e.id === id);
    return equipe ? (equipe.nom || equipe.name) : id;
  };

  const utilisateursFiltres = useMemo(() => {
    const q = recherche.trim().toLowerCase();
    return utilisateurs
      .filter((u) => filtreStatut === 'TOUS' || u.statut === filtreStatut)
      .filter((u) =>
        !q || [u.nom, u.prenom, u.email].filter(Boolean).some((v) => v.toLowerCase().includes(q))
      );
  }, [utilisateurs, recherche, filtreStatut]);

  const totalGeneral = (compteurs.agents ?? 0) + (compteurs.superviseurs ?? 0) + (compteurs.admins ?? 0);

  const creerUtilisateur = async () => {
    setCreating(true);
    setError(null);
    try {
      const payload = {
        nom: form.nom,
        prenom: form.prenom,
        email: form.email,
        telephone: form.telephone,
        ...(sousOnglet === 'agents' ? { idEquipe: form.idEquipe } : {}),
      };
      await apiClient.post(`/utilisateurs/${sousOnglet}`, payload);
      setMessage(`${LABEL_SINGULIER[sousOnglet].charAt(0).toUpperCase() + LABEL_SINGULIER[sousOnglet].slice(1)} créé avec succès.`);
      setForm({ nom: '', prenom: '', email: '', telephone: '', idEquipe: '' });
      setModalOuverte(false);
      chargerListe(sousOnglet);
      chargerTousLesCompteurs();
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la création.');
    } finally {
      setCreating(false);
    }
  };

  const confirmerSuppression = async () => {
    if (!aSupprimer) return;
    setDeleting(true);
    setError(null);
    try {
      await apiClient.delete(`/utilisateurs/${aSupprimer.idProfil}`);
      setUtilisateurs((prev) => prev.filter((u) => u.idProfil !== aSupprimer.idProfil));
      setCompteurs((prev) => ({ ...prev, [sousOnglet]: Math.max(0, (prev[sousOnglet] ?? 1) - 1) }));
      setMessage('Utilisateur supprimé.');
      setASupprimer(null);
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la suppression.');
    } finally {
      setDeleting(false);
    }
  };

  const roleActuel = ROLE_INFOS[sousOnglet];

  return (
    <div className="space-y-6">

      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
          
        </div>
        <button
          onClick={() => setModalOuverte(true)}
          className="flex items-center gap-2 bg-blue-700 hover:bg-blue-800 text-white px-4 py-2.5 rounded-xl text-sm font-semibold shadow-sm shadow-blue-700/20 transition"
        >
          <UserPlus className="w-4 h-4" />
          Nouveau {LABEL_SINGULIER[sousOnglet]}
        </button>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white rounded-2xl shadow-sm ring-1 ring-slate-900/5 p-5">
          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide">Total comptes</p>
          <p className="text-2xl font-bold text-slate-900 mt-1.5">{totalGeneral || '—'}</p>
        </div>
        {SOUS_ONGLETS.map(({ key, label, icon: Icon }) => (
          <button
            key={key}
            onClick={() => setSousOnglet(key)}
            className={`text-left bg-white rounded-2xl shadow-sm ring-1 p-5 transition ${
              sousOnglet === key ? 'ring-blue-600/30 bg-blue-50/40' : 'ring-slate-900/5 hover:ring-slate-900/10'
            }`}
          >
            <div className="flex items-center justify-between">
              <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide">{label}</p>
              <Icon className={`w-4 h-4 ${sousOnglet === key ? 'text-blue-700' : 'text-slate-300'}`} />
            </div>
            <p className="text-2xl font-bold text-slate-900 mt-1.5">
              {compteurs[key] === null ? '—' : compteurs[key]}
            </p>
          </button>
        ))}
      </div>

      {error && (
        <div className="flex items-center gap-2.5 bg-red-50 text-red-700 text-sm rounded-xl px-4 py-3 ring-1 ring-red-600/10">
          <AlertCircle className="w-4 h-4 shrink-0" />
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
            <nav className="flex items-center gap-6 self-stretch">
              {SOUS_ONGLETS.map(({ key, label, icon: Icon }) => {
                const isActive = sousOnglet === key;
                return (
                  <button
                    key={key}
                    onClick={() => setSousOnglet(key)}
                    className="relative flex items-center gap-2 text-[13.5px] font-medium transition-colors"
                  >
                    <Icon className={`w-[15px] h-[15px] ${isActive ? 'text-blue-700' : 'text-slate-400'}`} />
                    <span className={isActive ? 'text-slate-900' : 'text-slate-500 hover:text-slate-800'}>
                      {label}
                    </span>
                    <span
                      className={`absolute left-0 right-0 -bottom-3.5 h-[2px] rounded-full transition-opacity ${
                        isActive ? 'bg-blue-700 opacity-100' : 'opacity-0'
                      }`}
                    />
                  </button>
                );
              })}
            </nav>

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
          </div>

          <div className="border-t border-slate-100" />

          <div className="flex items-center gap-1.5 px-6 py-3 border-b border-slate-100">
            {STATUTS_FILTRE.map(({ key, label }) => (
              <button
                key={key}
                onClick={() => setFiltreStatut(key)}
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition ${
                  filtreStatut === key
                    ? 'bg-slate-900 text-white'
                    : 'text-slate-500 hover:bg-slate-100'
                }`}
              >
                {label}
              </button>
            ))}
            <span className="ml-auto text-xs text-slate-400 font-medium">
              {utilisateursFiltres.length} résultat{utilisateursFiltres.length > 1 ? 's' : ''}
            </span>
          </div>

          {loading ? (
            <div className="p-6 space-y-3">
              {Array.from({ length: 5 }).map((_, i) => (
                <div key={i} className="h-12 rounded-lg bg-slate-50 animate-pulse" />
              ))}
            </div>
          ) : utilisateursFiltres.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-slate-400 text-[11px] font-semibold uppercase tracking-wide">
                    <th className="py-3 pl-6 pr-4">Utilisateur</th>
                    <th className="py-3 pr-4">Équipe</th>
                    <th className="py-3 pr-4">Statut</th>
                    <th className="py-3 pr-6 text-right">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {utilisateursFiltres.map((u) => (
                    <tr key={u.idProfil} className="border-t border-slate-50 hover:bg-slate-50/70 transition-colors">
                      <td className="py-3.5 pl-6 pr-4">
                        <div className="flex items-center gap-3">
                          <div className={`w-9 h-9 rounded-full flex items-center justify-center text-xs font-bold shrink-0 ${couleurAvatar(u.idProfil)}`}>
                            {initiales(u.nom, u.prenom)}
                          </div>
                          <div className="min-w-0">
                            <p className="text-slate-900 font-medium truncate">{u.prenom} {u.nom}</p>
                            <p className="text-slate-500 text-xs truncate">{u.email}</p>
                          </div>
                        </div>
                      </td>
                      <td className="py-3.5 pr-4 text-slate-600">{nomEquipe(u.idEquipe)}</td>
                      <td className="py-3.5 pr-4">
                        <span className={`px-2.5 py-1 rounded-full text-[11px] font-semibold ${STATUT_STYLES[u.statut] || 'bg-slate-100 text-slate-600'}`}>
                          {u.statut}
                        </span>
                      </td>
                      <td className="py-3.5 pr-6 text-right">
                        <button
                          onClick={() => setASupprimer(u)}
                          className="w-8 h-8 inline-flex items-center justify-center rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 transition"
                          title="Supprimer"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center py-16 px-6 text-center">
              <div className="w-12 h-12 rounded-full bg-slate-50 flex items-center justify-center mb-3">
                <Users2 className="w-5 h-5 text-slate-300" />
              </div>
              <p className="text-sm font-medium text-slate-600">
                {recherche || filtreStatut !== 'TOUS'
                  ? 'Aucun résultat pour ces critères'
                  : `Aucun ${LABEL_SINGULIER[sousOnglet]} pour l'instant`}
              </p>
              {!recherche && filtreStatut === 'TOUS' && (
                <p className="text-xs text-slate-400 mt-1">
                  Cliquez sur « Nouveau {LABEL_SINGULIER[sousOnglet]} » pour en ajouter un.
                </p>
              )}
            </div>
          )}
        </div>

        <aside className="xl:col-span-4 bg-white rounded-2xl shadow-sm ring-1 ring-slate-900/5 p-6 space-y-5">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-xl bg-blue-50 flex items-center justify-center shrink-0">
              <ShieldQuestion className="w-4.5 h-4.5 text-blue-700" />
            </div>
            <h2 className="text-sm font-bold text-slate-900">À propos des {roleActuel.titre.toLowerCase()}</h2>
          </div>

          <p className="text-sm text-slate-600 leading-relaxed">{roleActuel.description}</p>

          <div>
            <p className="text-[11px] font-semibold text-slate-400 uppercase tracking-wide mb-2.5">
              Permissions principales
            </p>
            <ul className="space-y-2">
              {roleActuel.permissions.map((p) => (
                <li key={p} className="flex items-start gap-2.5 text-sm text-slate-700">
                  <CheckCircle2 className="w-4 h-4 text-emerald-500 mt-0.5 shrink-0" />
                  {p}
                </li>
              ))}
            </ul>
          </div>

          <div className="flex items-start gap-2.5 bg-slate-50 rounded-xl px-4 py-3.5">
            <Info className="w-4 h-4 text-slate-400 mt-0.5 shrink-0" />
            <p className="text-xs text-slate-500 leading-relaxed">
              La suppression d'un compte révoque immédiatement son accès à la plateforme et à Keycloak.
            </p>
          </div>
        </aside>
      </div>

      {modalOuverte && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full">
            <div className="flex items-center justify-between px-6 py-5 border-b border-slate-100">
              <h3 className="text-base font-bold text-slate-900">
                Nouveau {LABEL_ARTICLE[sousOnglet]}
              </h3>
              <button
                onClick={() => setModalOuverte(false)}
                className="w-8 h-8 flex items-center justify-center rounded-full text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="px-6 py-5 space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-slate-500 mb-1">Nom</label>
                  <input
                    type="text"
                    value={form.nom}
                    onChange={(e) => setForm({ ...form, nom: e.target.value })}
                    className="w-full border border-slate-200 rounded-xl px-3.5 py-2.5 text-sm outline-none focus:ring-2 focus:ring-blue-600/30 focus:border-blue-400 transition"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-500 mb-1">Prénom</label>
                  <input
                    type="text"
                    value={form.prenom}
                    onChange={(e) => setForm({ ...form, prenom: e.target.value })}
                    className="w-full border border-slate-200 rounded-xl px-3.5 py-2.5 text-sm outline-none focus:ring-2 focus:ring-blue-600/30 focus:border-blue-400 transition"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-500 mb-1">Email</label>
                <input
                  type="email"
                  value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })}
                  className="w-full border border-slate-200 rounded-xl px-3.5 py-2.5 text-sm outline-none focus:ring-2 focus:ring-blue-600/30 focus:border-blue-400 transition"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-500 mb-1">Téléphone</label>
                <input
                  type="text"
                  value={form.telephone}
                  onChange={(e) => setForm({ ...form, telephone: e.target.value })}
                  className="w-full border border-slate-200 rounded-xl px-3.5 py-2.5 text-sm outline-none focus:ring-2 focus:ring-blue-600/30 focus:border-blue-400 transition"
                />
              </div>

              {sousOnglet === 'agents' && (
                <div>
                  <label className="block text-xs font-medium text-slate-500 mb-1">Équipe</label>
                  <select
                    value={form.idEquipe}
                    onChange={(e) => setForm({ ...form, idEquipe: e.target.value })}
                    className="w-full border border-slate-200 rounded-xl px-3.5 py-2.5 text-sm outline-none focus:ring-2 focus:ring-blue-600/30 focus:border-blue-400 transition"
                  >
                    <option value="">Sélectionner une équipe</option>
                    {equipes.map((equipe) => (
                      <option key={equipe.id} value={equipe.id}>
                        {equipe.nom || equipe.name}
                      </option>
                    ))}
                  </select>
                </div>
              )}
            </div>

            <div className="flex justify-end gap-3 px-6 py-4 border-t border-slate-100">
              <button
                onClick={() => setModalOuverte(false)}
                disabled={creating}
                className="px-4 py-2.5 rounded-xl text-sm font-semibold text-slate-600 hover:bg-slate-100 transition disabled:opacity-60"
              >
                Annuler
              </button>
              <button
                onClick={creerUtilisateur}
                disabled={creating}
                className="px-5 py-2.5 rounded-xl text-sm font-semibold bg-blue-700 hover:bg-blue-800 disabled:opacity-60 text-white transition"
              >
                {creating ? 'Création...' : 'Créer'}
              </button>
            </div>
          </div>
        </div>
      )}

      {aSupprimer && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-sm w-full p-6">
            <div className="flex items-center gap-3 mb-3">
              <div className="w-10 h-10 rounded-full bg-red-50 flex items-center justify-center shrink-0">
                <AlertCircle className="w-5 h-5 text-red-600" />
              </div>
              <h3 className="text-base font-bold text-slate-900">Confirmer la suppression</h3>
            </div>
            <p className="text-sm text-slate-600 mb-6">
              Voulez-vous vraiment supprimer{' '}
              <span className="font-semibold text-slate-800">{aSupprimer.prenom} {aSupprimer.nom}</span> ?
              Cette action est irréversible.
            </p>
            <div className="flex justify-end gap-3">
              <button
                onClick={() => setASupprimer(null)}
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