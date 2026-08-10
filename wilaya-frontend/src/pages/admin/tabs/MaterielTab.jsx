import React, { useState, useEffect, useCallback, useMemo } from 'react';
import {
  PlusCircle, RefreshCw, Search, X, AlertTriangle, Pencil,
  Wrench, CheckCircle2, Info, ListChecks, MapPin,
} from 'lucide-react';
import { ressourceClient as apiClient } from '../../../api/client';

const STATUTS_MATERIEL = ['DISPONIBLE', 'EN_USAGE', 'EN_MAINTENANCE'];

const STATUTS_FILTRE = [
  { key: 'TOUS', label: 'Tous' },
  { key: 'DISPONIBLE', label: 'Disponibles' },
  { key: 'EN_USAGE', label: 'En usage' },
  { key: 'EN_MAINTENANCE', label: 'En maintenance' },
];

const STATUT_STYLES = {
  DISPONIBLE: 'bg-emerald-50 text-emerald-700 ring-1 ring-emerald-600/10',
  EN_USAGE: 'bg-blue-50 text-blue-700 ring-1 ring-blue-600/10',
  EN_MAINTENANCE: 'bg-amber-50 text-amber-700 ring-1 ring-amber-600/10',
};

const STATUT_LABELS = {
  DISPONIBLE: 'Disponible',
  EN_USAGE: 'En usage',
  EN_MAINTENANCE: 'En maintenance',
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

export default function MaterielTab() {
  const [equipes, setEquipes] = useState([]);
  const [loadingEquipes, setLoadingEquipes] = useState(true);

  const [materiels, setMateriels] = useState([]);
  const [loadingMateriels, setLoadingMateriels] = useState(true);
  const [recherche, setRecherche] = useState('');
  const [filtreStatut, setFiltreStatut] = useState('TOUS');

  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  const [modalAjoutOuverte, setModalAjoutOuverte] = useState(false);
  const [form, setForm] = useState({ type: '', idEquipeAssociee: '' });
  const [creating, setCreating] = useState(false);

  const [materielAModifier, setMaterielAModifier] = useState(null);
  const [statutChoisi, setStatutChoisi] = useState('');
  const [changing, setChanging] = useState(false);

  const [materielASupprimer, setMaterielASupprimer] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const chargerEquipes = useCallback(async () => {
    setLoadingEquipes(true);
    try {
      const res = await apiClient.get('/equipes');
      setEquipes(res.data);
    } catch (err) {
      setError("Impossible de charger la liste des équipes.");
    } finally {
      setLoadingEquipes(false);
    }
  }, []);

  const chargerMateriels = useCallback(async () => {
    setLoadingMateriels(true);
    try {
      const res = await apiClient.get('/materiels');
      setMateriels(res.data);
    } catch (err) {
      setError("Impossible de charger la liste des matériels.");
    } finally {
      setLoadingMateriels(false);
    }
  }, []);

  useEffect(() => { chargerEquipes(); chargerMateriels(); }, [chargerEquipes, chargerMateriels]);

  useEffect(() => {
    if (!message) return;
    const t = setTimeout(() => setMessage(null), 4000);
    return () => clearTimeout(t);
  }, [message]);

  const nomEquipe = (id) => {
    const equipe = equipes.find((e) => e.id === id);
    return equipe ? (equipe.nom || equipe.name) : id;
  };

  const compteurs = useMemo(() => {
    const base = { DISPONIBLE: 0, EN_USAGE: 0, EN_MAINTENANCE: 0 };
    materiels.forEach((m) => { if (base[m.statut] !== undefined) base[m.statut]++; });
    return base;
  }, [materiels]);

  const materielsFiltres = useMemo(() => {
    const q = recherche.trim().toLowerCase();
    return materiels
      .filter((m) => filtreStatut === 'TOUS' || m.statut === filtreStatut)
      .filter((m) => {
        if (!q) return true;
        return [m.type, nomEquipe(m.idEquipeAssociee)]
          .filter(Boolean)
          .some((v) => v.toLowerCase().includes(q));
      });
  }, [materiels, recherche, filtreStatut, equipes]);

  const ajouterMateriel = async () => {
    if (!form.type.trim() || !form.idEquipeAssociee) {
      setError("Type et équipe associée sont requis.");
      return;
    }
    setCreating(true);
    setError(null);
    try {
      await apiClient.post('/materiels', form);
      setMessage('Matériel ajouté avec succès.');
      setForm({ type: '', idEquipeAssociee: '' });
      setModalAjoutOuverte(false);
      chargerMateriels();
    } catch (err) {
      setError(err.response?.data?.message || "Erreur lors de l'ajout.");
    } finally {
      setCreating(false);
    }
  };

  const demanderChangementStatut = (m) => {
    setError(null);
    setStatutChoisi(m.statut || '');
    setMaterielAModifier(m);
  };
  const annulerChangementStatut = () => {
    if (changing) return;
    setMaterielAModifier(null);
    setStatutChoisi('');
  };

  const confirmerChangementStatut = async () => {
    if (!materielAModifier || !statutChoisi) return;
    setChanging(true);
    setError(null);
    try {
      await apiClient.patch(`/materiels/${materielAModifier.id}/statut`, { statut: statutChoisi });
      setMessage('Statut du matériel mis à jour.');
      setMaterielAModifier(null);
      setStatutChoisi('');
      chargerMateriels();
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la mise à jour.');
    } finally {
      setChanging(false);
    }
  };

  const confirmerSuppression = async () => {
    if (!materielASupprimer) return;
    setDeleting(true);
    setError(null);
    try {
      await apiClient.delete(`/materiels/${materielASupprimer.id}`);
      setMessage('Matériel supprimé avec succès.');
      setMaterielASupprimer(null);
      chargerMateriels();
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la suppression.');
    } finally {
      setDeleting(false);
    }
  };

  return (
    <div className="space-y-6">

      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
         
        </div>
        <button
          onClick={() => setModalAjoutOuverte(true)}
          className="flex items-center gap-2 bg-blue-700 hover:bg-blue-800 text-white px-4 py-2.5 rounded-xl text-sm font-semibold shadow-sm shadow-blue-700/20 transition"
        >
          <PlusCircle className="w-4 h-4" />
          Nouveau matériel
        </button>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white rounded-2xl shadow-sm ring-1 ring-slate-900/5 p-5">
          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide">Total matériel</p>
          <p className="text-2xl font-bold text-slate-900 mt-1.5">{materiels.length || '—'}</p>
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
              <ListChecks className="w-4 h-4 text-slate-400" />
              Liste du matériel
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
                onClick={chargerMateriels}
                disabled={loadingMateriels}
                title="Actualiser"
                className="w-9 h-9 flex items-center justify-center rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition disabled:opacity-60"
              >
                <RefreshCw className={`w-4 h-4 ${loadingMateriels ? 'animate-spin' : ''}`} />
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
              {materielsFiltres.length} résultat{materielsFiltres.length > 1 ? 's' : ''}
            </span>
          </div>

          {loadingMateriels ? (
            <div className="p-6 space-y-3">
              {Array.from({ length: 4 }).map((_, i) => (
                <div key={i} className="h-14 rounded-lg bg-slate-50 animate-pulse" />
              ))}
            </div>
          ) : materielsFiltres.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-slate-400 text-[11px] font-semibold uppercase tracking-wide">
                    <th className="py-3 pl-6 pr-4">Matériel</th>
                    <th className="py-3 pr-4">Équipe associée</th>
                    <th className="py-3 pr-4">Statut</th>
                    <th className="py-3 pr-6 text-right">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {materielsFiltres.map((m) => (
                    <tr key={m.id} className="border-t border-slate-50 hover:bg-slate-50/70 transition-colors">
                      <td className="py-3.5 pl-6 pr-4">
                        <div className="flex items-center gap-3">
                          <div className={`w-9 h-9 rounded-full flex items-center justify-center shrink-0 ${couleurAvatar(m.id)}`}>
                            <Wrench className="w-4 h-4" />
                          </div>
                          <p className="text-slate-900 font-medium truncate">{m.type}</p>
                        </div>
                      </td>
                      <td className="py-3.5 pr-4 text-slate-600">
                        <span className="flex items-center gap-1.5">
                          <MapPin className="w-3.5 h-3.5 text-slate-400 shrink-0" />
                          {nomEquipe(m.idEquipeAssociee)}
                        </span>
                      </td>
                      <td className="py-3.5 pr-4">
                        <span className={`px-2.5 py-1 rounded-full text-[11px] font-semibold ${STATUT_STYLES[m.statut] || 'bg-slate-100 text-slate-600'}`}>
                          {STATUT_LABELS[m.statut] || m.statut}
                        </span>
                      </td>
                      <td className="py-3.5 pr-6 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <button
                            onClick={() => demanderChangementStatut(m)}
                            title="Changer le statut"
                            className="w-8 h-8 inline-flex items-center justify-center rounded-lg text-slate-400 hover:text-blue-700 hover:bg-blue-50 transition"
                          >
                            <Pencil className="w-4 h-4" />
                          </button>
                          <button
                            onClick={() => setMaterielASupprimer(m)}
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
            <div className="flex flex-col items-center justify-center py-16 px-6 text-center">
              <div className="w-12 h-12 rounded-full bg-slate-50 flex items-center justify-center mb-3">
                <Wrench className="w-5 h-5 text-slate-300" />
              </div>
              <p className="text-sm font-medium text-slate-600">
                {recherche || filtreStatut !== 'TOUS' ? 'Aucun résultat pour ces critères' : "Aucun matériel pour l'instant"}
              </p>
              {!recherche && filtreStatut === 'TOUS' && (
                <p className="text-xs text-slate-400 mt-1">
                  Cliquez sur « Nouveau matériel » pour en ajouter un.
                </p>
              )}
            </div>
          )}
        </div>

        <aside className="xl:col-span-4 bg-white rounded-2xl shadow-sm ring-1 ring-slate-900/5 p-6 space-y-5">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-xl bg-blue-50 flex items-center justify-center shrink-0">
              <Info className="w-4.5 h-4.5 text-blue-700" />
            </div>
            <h2 className="text-sm font-bold text-slate-900">Statuts du matériel</h2>
          </div>

          <ul className="space-y-3">
            <li className="flex items-start gap-2.5 text-sm text-slate-600">
              <span className="w-2 h-2 rounded-full bg-emerald-500 mt-1.5 shrink-0" />
              <span><span className="font-semibold text-slate-800">Disponible</span> — le matériel est prêt à être utilisé.</span>
            </li>
            <li className="flex items-start gap-2.5 text-sm text-slate-600">
              <span className="w-2 h-2 rounded-full bg-blue-500 mt-1.5 shrink-0" />
              <span><span className="font-semibold text-slate-800">En usage</span> — le matériel est actuellement utilisé sur le terrain.</span>
            </li>
            <li className="flex items-start gap-2.5 text-sm text-slate-600">
              <span className="w-2 h-2 rounded-full bg-amber-500 mt-1.5 shrink-0" />
              <span><span className="font-semibold text-slate-800">En maintenance</span> — le matériel est indisponible pour réparation ou entretien.</span>
            </li>
          </ul>

          <div className="flex items-start gap-2.5 bg-slate-50 rounded-xl px-4 py-3.5">
            <Info className="w-4 h-4 text-slate-400 mt-0.5 shrink-0" />
            <p className="text-xs text-slate-500 leading-relaxed">
              Chaque matériel doit être associé à une équipe existante lors de sa création.
            </p>
          </div>
        </aside>
      </div>

      {modalAjoutOuverte && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full">
            <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
              <h3 className="text-base font-bold text-slate-900">Nouveau matériel</h3>
              <button
                onClick={() => setModalAjoutOuverte(false)}
                className="w-8 h-8 flex items-center justify-center rounded-full text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="px-6 py-5 space-y-4">
              <div>
                <label className="block text-xs font-medium text-slate-500 mb-1">Type de matériel</label>
                <input
                  type="text"
                  value={form.type}
                  onChange={(e) => setForm({ ...form, type: e.target.value })}
                  placeholder="Ex : Camion-citerne, Perceuse..."
                  className="w-full border border-slate-200 rounded-xl px-3.5 py-2.5 text-sm outline-none focus:ring-2 focus:ring-blue-600/30 focus:border-blue-400 transition"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-500 mb-1">Équipe associée</label>
                <select
                  value={form.idEquipeAssociee}
                  onChange={(e) => setForm({ ...form, idEquipeAssociee: e.target.value })}
                  disabled={loadingEquipes}
                  className="w-full border border-slate-200 rounded-xl px-3.5 py-2.5 text-sm outline-none focus:ring-2 focus:ring-blue-600/30 focus:border-blue-400 transition"
                >
                  <option value="">
                    {loadingEquipes ? 'Chargement des équipes...' : 'Sélectionner une équipe'}
                  </option>
                  {equipes.map((equipe) => (
                    <option key={equipe.id} value={equipe.id}>
                      {equipe.nom || equipe.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="flex justify-end gap-3 px-6 py-4 border-t border-slate-100">
              <button
                onClick={() => setModalAjoutOuverte(false)}
                disabled={creating}
                className="px-4 py-2.5 rounded-xl text-sm font-semibold text-slate-600 hover:bg-slate-100 transition disabled:opacity-60"
              >
                Annuler
              </button>
              <button
                onClick={ajouterMateriel}
                disabled={creating}
                className="px-5 py-2.5 rounded-xl text-sm font-semibold bg-blue-700 hover:bg-blue-800 disabled:opacity-60 text-white transition"
              >
                {creating ? 'Ajout...' : 'Ajouter'}
              </button>
            </div>
          </div>
        </div>
      )}

      {materielAModifier && (
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
                Matériel <span className="font-semibold text-slate-800">{materielAModifier.type}</span> · {nomEquipe(materielAModifier.idEquipeAssociee)}
              </p>

              <label className="block text-xs font-medium text-slate-500 mb-2">Nouveau statut</label>
              <div className="space-y-2">
                {STATUTS_MATERIEL.map((statut) => (
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
                disabled={changing}
                className="px-4 py-2.5 rounded-xl text-sm font-semibold text-slate-600 hover:bg-slate-100 transition disabled:opacity-60"
              >
                Annuler
              </button>
              <button
                onClick={confirmerChangementStatut}
                disabled={changing || !statutChoisi || statutChoisi === materielAModifier.statut}
                className="px-5 py-2.5 rounded-xl text-sm font-semibold text-white bg-blue-700 hover:bg-blue-800 disabled:opacity-60 transition"
              >
                {changing ? 'Mise à jour...' : 'Mettre à jour'}
              </button>
            </div>
          </div>
        </div>
      )}

      {materielASupprimer && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4" onClick={() => !deleting && setMaterielASupprimer(null)}>
          <div className="bg-white rounded-2xl shadow-2xl max-w-sm w-full p-6" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center gap-3 mb-3">
              <div className="w-10 h-10 rounded-full bg-red-50 flex items-center justify-center shrink-0">
                <AlertTriangle className="w-5 h-5 text-red-600" />
              </div>
              <h3 className="text-base font-bold text-slate-900">Confirmer la suppression</h3>
            </div>
            <p className="text-sm text-slate-600 mb-6">
              Voulez-vous vraiment supprimer le matériel{' '}
              <span className="font-semibold text-slate-800">« {materielASupprimer.type} »</span> associé à l'équipe{' '}
              <span className="font-semibold text-slate-800">{nomEquipe(materielASupprimer.idEquipeAssociee)}</span> ?
              Cette action est irréversible.
            </p>
            <div className="flex justify-end gap-3">
              <button
                onClick={() => setMaterielASupprimer(null)}
                disabled={deleting}
                className="px-4 py-2.5 rounded-xl text-sm font-semibold text-slate-600 hover:bg-slate-100 transition disabled:opacity-60"
              >
                Annuler
              </button>
              <button
                onClick={confirmerSuppression}
                disabled={deleting}
                className="px-4 py-2.5 rounded-xl text-sm font-semibold bg-red-600 hover:bg-red-700 text-white transition disabled:opacity-60"
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