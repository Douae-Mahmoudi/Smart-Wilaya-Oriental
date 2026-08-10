import React, { useState, useEffect, useMemo } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import {
  MapPin,
  RefreshCw,
  FileWarning,
  Clock,
  CheckCircle2,
  AlertTriangle,
  ChevronLeft,
  ChevronRight,
  History,
} from 'lucide-react';
import { signalementClient as apiClient } from '../../../api/client';
import ModalHistorique from '../../../components/ModalHistorique';

const CENTRE_PAR_DEFAUT = [34.6805, -1.9089];

const COULEUR_GRAVITE = {
  HAUTE: '#dc2626',
  MOYENNE: '#eab308',
  BASSE: '#16a34a',
};

const STATUT_STYLES = {
  SIGNALE: 'bg-slate-100 text-slate-600',
  CLASSIFIE: 'bg-blue-50 text-blue-700',
  EN_RECHERCHE_EQUIPE: 'bg-amber-50 text-amber-700',
  AFFECTE: 'bg-indigo-50 text-indigo-700',
  EN_INTERVENTION: 'bg-orange-50 text-orange-700',
  RESOLU: 'bg-green-50 text-green-700',
  CLOTURE: 'bg-slate-100 text-slate-500',
};

const LIGNES_PAR_PAGE = 8;

export default function CarteTab() {
  const [stats, setStats] = useState(null);
  const [signalements, setSignalements] = useState([]);
  const [pointsCarte, setPointsCarte] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(1);
  const [historiqueModal, setHistoriqueModal] = useState({
    ouvert: false,
    numeroSuivi: '',
    historique: [],
  });

  const charger = async () => {
    setLoading(true);
    setError(null);
    try {
      const [statsRes, listeRes, carteRes] = await Promise.all([
        apiClient.get('/signalements/statistiques'),
        apiClient.get('/signalements'),
        apiClient.get('/signalements/carte'),
      ]);
      setStats(statsRes.data);
      setSignalements(listeRes.data);
      setPointsCarte(carteRes.data);
      setPage(1);
    } catch (err) {
      setError(err.response?.data?.message || 'Impossible de charger les données.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    charger();
  }, []);

  const cartesStats = stats
    ? [
        {
          label: 'Total signalements',
          valeur: stats.total,
          icon: FileWarning,
          couleur: 'text-blue-700 bg-blue-50',
        },
        {
          label: 'En cours',
          valeur: stats.enCours,
          icon: Clock,
          couleur: 'text-amber-700 bg-amber-50',
        },
        {
          label: 'Résolus',
          valeur: stats.resolus,
          icon: CheckCircle2,
          couleur: 'text-green-700 bg-green-50',
        },
        {
          label: 'Critiques',
          valeur: stats.critiques,
          icon: AlertTriangle,
          couleur: 'text-red-700 bg-red-50',
        },
      ]
    : [];

  const totalPages = Math.max(1, Math.ceil(signalements.length / LIGNES_PAR_PAGE));

  const signalementsPage = useMemo(() => {
    const debut = (page - 1) * LIGNES_PAR_PAGE;
    return signalements.slice(debut, debut + LIGNES_PAR_PAGE);
  }, [signalements, page]);

  const allerPage = (n) => setPage(Math.min(Math.max(n, 1), totalPages));

  const ouvrirHistorique = (signalement) => {
    setHistoriqueModal({
      ouvert: true,
      numeroSuivi: signalement.numeroSuivi,
      historique: signalement.historiqueStatuts || [],
    });
  };

  const fermerHistorique = () => {
    setHistoriqueModal({
      ouvert: false,
      numeroSuivi: '',
      historique: [],
    });
  };

  return (
    <div className="space-y-6">
      {error && <p className="text-sm text-red-600">{error}</p>}

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {loading
          ? Array.from({ length: 4 }).map((_, i) => (
              <div key={i} className="bg-white rounded-2xl shadow-xl p-5 h-24 animate-pulse" />
            ))
          : cartesStats.map(({ label, valeur, icon: Icon, couleur }) => (
              <div key={label} className="bg-white rounded-2xl shadow-xl p-5 flex items-center gap-4">
                <div className={`w-11 h-11 rounded-xl flex items-center justify-center ${couleur}`}>
                  <Icon className="w-5 h-5" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-slate-900">{valeur}</p>
                  <p className="text-xs text-slate-500 font-medium">{label}</p>
                </div>
              </div>
            ))}
      </div>

      <div className="bg-white rounded-2xl shadow-xl p-6 md:p-8">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-bold text-slate-900 flex items-center gap-2">
            <MapPin className="w-5 h-5 text-blue-700" />
            Carte des signalements
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
          <p className="text-sm text-slate-500">Chargement de la carte...</p>
        ) : (
          <div
            className="relative isolate rounded-xl overflow-hidden border border-slate-200"
            style={{ height: '500px', zIndex: 0 }}
          >
            <MapContainer center={CENTRE_PAR_DEFAUT} zoom={12} style={{ width: '100%', height: '100%' }}>
              <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              />
              {pointsCarte.map((s) => (
                <Marker key={s.id} position={[s.latitude, s.longitude]}>
                  <Popup>
                    <div className="text-sm">
                      <p className="font-semibold">{s.numeroSuivi}</p>
                      <p>Type : {s.type}</p>
                      <p>
                        Gravité :{' '}
                        <span style={{ color: COULEUR_GRAVITE[s.gravite] || '#334155', fontWeight: 600 }}>
                          {s.gravite}
                        </span>
                      </p>
                      <p>Statut : {s.statut}</p>
                    </div>
                  </Popup>
                </Marker>
              ))}
            </MapContainer>
          </div>
        )}

        {!loading && pointsCarte.length === 0 && !error && (
          <p className="text-sm text-slate-400 text-center py-6">
            Aucun signalement géolocalisé pour l'instant.
          </p>
        )}
      </div>

      <div className="bg-white rounded-2xl shadow-xl p-6 md:p-8">
        <h2 className="text-lg font-bold text-slate-900 mb-4">Historique des signalements</h2>

        {loading ? (
          <p className="text-sm text-slate-500">Chargement...</p>
        ) : signalements.length === 0 ? (
          <p className="text-sm text-slate-400 text-center py-10">Aucun signalement pour l'instant.</p>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-slate-200 text-left text-slate-500">
                    <th className="py-3 pr-4 font-semibold">N° Suivi</th>
                    <th className="py-3 pr-4 font-semibold">Type</th>
                    <th className="py-3 pr-4 font-semibold">Zone</th>
                    <th className="py-3 pr-4 font-semibold">Gravité</th>
                    <th className="py-3 pr-4 font-semibold">Statut</th>
                    <th className="py-3 pr-4 font-semibold min-w-[180px]">Dernier message</th>
                    <th className="py-3 pr-4 font-semibold">Date</th>
                    <th className="py-3 pr-4 font-semibold">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {signalementsPage.map((s) => (
                    <tr key={s.id} className="border-b border-slate-100 hover:bg-slate-50">
                      <td className="py-3 pr-4 text-blue-700 font-medium">{s.numeroSuivi}</td>
                      <td className="py-3 pr-4 text-slate-700">{s.type}</td>
                      <td className="py-3 pr-4 text-slate-700">{s.zone}</td>
                      <td className="py-3 pr-4">
                        <span
                          className="px-2.5 py-1 rounded-full text-xs font-semibold"
                          style={{
                            color: COULEUR_GRAVITE[s.gravite] || '#334155',
                            backgroundColor: `${COULEUR_GRAVITE[s.gravite] || '#334155'}15`,
                          }}
                        >
                          {s.gravite}
                        </span>
                      </td>
                      <td className="py-3 pr-4">
                        <span
                          className={`px-2.5 py-1 rounded-full text-xs font-semibold ${
                            STATUT_STYLES[s.statut] || 'bg-slate-100 text-slate-600'
                          }`}
                        >
                          {s.statut}
                        </span>
                      </td>
                      <td
                        className="py-3 pr-4 text-slate-700 max-w-sm break-words whitespace-normal"
                        title={s.dernierMessage || ''}
                      >
                        {s.dernierMessage || '—'}
                      </td>
                      <td className="py-3 pr-4 text-slate-500">
                        {new Date(s.dateCreation).toLocaleDateString('fr-FR')}
                      </td>
                      <td className="py-3 pr-4">
                        <button
                          onClick={() => ouvrirHistorique(s)}
                          className="flex items-center gap-1.5 text-xs font-medium text-blue-700 hover:text-blue-900 hover:underline"
                        >
                          <History className="w-4 h-4" />
                          Historique
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="flex items-center justify-between mt-5 pt-4 border-t border-slate-100">
              <p className="text-xs text-slate-500">
                Page {page} sur {totalPages} — {signalements.length} signalement
                {signalements.length > 1 ? 's' : ''} au total
              </p>
              <div className="flex items-center gap-1">
                <button
                  onClick={() => allerPage(page - 1)}
                  disabled={page === 1}
                  className="w-8 h-8 flex items-center justify-center rounded-lg text-slate-500 hover:bg-slate-100 disabled:opacity-40 disabled:hover:bg-transparent transition"
                >
                  <ChevronLeft className="w-4 h-4" />
                </button>

                {Array.from({ length: totalPages }, (_, i) => i + 1)
                  .filter((n) => n === 1 || n === totalPages || Math.abs(n - page) <= 1)
                  .reduce((acc, n, idx, arr) => {
                    if (idx > 0 && n - arr[idx - 1] > 1) acc.push('...');
                    acc.push(n);
                    return acc;
                  }, [])
                  .map((n, idx) =>
                    n === '...' ? (
                      <span key={`ellipsis-${idx}`} className="w-8 h-8 flex items-center justify-center text-slate-400 text-sm">
                        …
                      </span>
                    ) : (
                      <button
                        key={n}
                        onClick={() => allerPage(n)}
                        className={`w-8 h-8 flex items-center justify-center rounded-lg text-sm font-semibold transition ${
                          page === n ? 'bg-blue-700 text-white' : 'text-slate-600 hover:bg-slate-100'
                        }`}
                      >
                        {n}
                      </button>
                    )
                  )}

                <button
                  onClick={() => allerPage(page + 1)}
                  disabled={page === totalPages}
                  className="w-8 h-8 flex items-center justify-center rounded-lg text-slate-500 hover:bg-slate-100 disabled:opacity-40 disabled:hover:bg-transparent transition"
                >
                  <ChevronRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          </>
        )}
      </div>

      <ModalHistorique
        isOpen={historiqueModal.ouvert}
        onClose={fermerHistorique}
        historique={historiqueModal.historique}
        numeroSuivi={historiqueModal.numeroSuivi}
      />
    </div>
  );
}