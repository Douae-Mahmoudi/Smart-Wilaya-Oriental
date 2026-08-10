import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  User,
  Lock,
  Bell,
  BellOff,
  Loader2,
  Mail,
  Phone,
  ShieldCheck,
  CheckCircle2,
  AlertTriangle,
  Save,
  KeyRound,
} from 'lucide-react';
import { utilisateurClient as apiClient } from '../../api/client';
import logoOriental from '../../assets/Logo_Region_Oriental.png';
import UserMenu from '../../components/UserMenu';
 
function ZelligeMotif() {
  return (
    <svg className="absolute inset-0 h-full w-full" preserveAspectRatio="xMidYMid slice" aria-hidden="true">
      <defs>
        <pattern id="zellige" width="56" height="56" patternUnits="userSpaceOnUse" patternTransform="rotate(0)">
          <g fill="none" stroke="#D4AF37" strokeWidth="0.75" strokeOpacity="0.35">
            <path d="M28 0 L56 28 L28 56 L0 28 Z" />
            <path d="M28 8 L48 28 L28 48 L8 28 Z" />
            <circle cx="28" cy="28" r="4" />
          </g>
        </pattern>
      </defs>
      <rect width="100%" height="100%" fill="url(#zellige)" />
    </svg>
  );
}
 
function ForceMotDePasse({ valeur }) {
  const score = useMemo(() => {
    if (!valeur) return 0;
    let s = 0;
    if (valeur.length >= 8) s++;
    if (valeur.length >= 12) s++;
    if (/[0-9]/.test(valeur) && /[A-Za-z]/.test(valeur)) s++;
    if (/[^A-Za-z0-9]/.test(valeur)) s++;
    return Math.min(s, 4);
  }, [valeur]);
 
  const config = [
    { label: 'Trop court', couleur: 'bg-slate-200' },
    { label: 'Faible', couleur: 'bg-red-400' },
    { label: 'Moyen', couleur: 'bg-amber-400' },
    { label: 'Bon', couleur: 'bg-blue-500' },
    { label: 'Excellent', couleur: 'bg-green-500' },
  ][score];
 
  if (!valeur) return null;
 
  return (
    <div className="mt-2">
      <div className="flex gap-1.5">
        {[0, 1, 2, 3].map((i) => (
          <div
            key={i}
            className={`h-1 flex-1 rounded-full transition-colors duration-300 ${
              i < score ? config.couleur : 'bg-slate-100'
            }`}
          />
        ))}
      </div>
      <p className="mt-1.5 text-xs font-medium text-slate-500">{config.label}</p>
    </div>
  );
}
 
export default function MonProfilPage() {
  const navigate = useNavigate();
  const [onglet, setOnglet] = useState('infos');
 
  const [profil, setProfil] = useState(null);
  const [loadingProfil, setLoadingProfil] = useState(true);
  const [form, setForm] = useState({ nom: '', prenom: '', telephone: '', notificationsActivees: true });
  const [savingInfos, setSavingInfos] = useState(false);
 
  const [motDePasse, setMotDePasse] = useState({ ancienMotDePasse: '', nouveauMotDePasse: '', confirmation: '' });
  const [savingMdp, setSavingMdp] = useState(false);
 
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);
 
  useEffect(() => {
    const chargerProfil = async () => {
      setLoadingProfil(true);
      try {
        const { data } = await apiClient.get('/utilisateurs/moi');
        setProfil(data);
        setForm({
          nom: data.nom,
          prenom: data.prenom,
          telephone: data.telephone || '',
          notificationsActivees: data.notificationsActivees,
        });
      } catch (err) {
        setError('Impossible de charger le profil.');
      } finally {
        setLoadingProfil(false);
      }
    };
    chargerProfil();
  }, []);
 
  const enregistrerInfos = async () => {
    setSavingInfos(true);
    setError(null);
    setMessage(null);
    try {
      await apiClient.patch('/utilisateurs/moi', form);
      setMessage('Profil mis à jour avec succès.');
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la mise à jour.');
    } finally {
      setSavingInfos(false);
    }
  };
 
  const enregistrerMotDePasse = async () => {
    setError(null);
    setMessage(null);
 
    if (motDePasse.nouveauMotDePasse.length < 8) {
      setError('Le nouveau mot de passe doit contenir au moins 8 caractères.');
      return;
    }
    if (motDePasse.nouveauMotDePasse !== motDePasse.confirmation) {
      setError('La confirmation ne correspond pas au nouveau mot de passe.');
      return;
    }
 
    setSavingMdp(true);
    try {
      await apiClient.patch('/utilisateurs/moi/mot-de-passe', {
        ancienMotDePasse: motDePasse.ancienMotDePasse,
        nouveauMotDePasse: motDePasse.nouveauMotDePasse,
      });
      setMessage('Mot de passe modifié avec succès.');
      setMotDePasse({ ancienMotDePasse: '', nouveauMotDePasse: '', confirmation: '' });
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors du changement de mot de passe.');
    } finally {
      setSavingMdp(false);
    }
  };
 
  const initiales = `${form.prenom?.[0] || ''}${form.nom?.[0] || ''}`.toUpperCase() || '··';
 
  return (
    <div className="min-h-screen bg-[#F4F6FB]">
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,500;9..144,600&family=Inter:wght@400;500;600;700&display=swap');
        .font-display { font-family: 'Fraunces', serif; }
        .font-body { font-family: 'Inter', sans-serif; }
      `}</style>
 
      <header className="bg-white/90 backdrop-blur-md shadow-sm sticky top-0 z-50">
        <div className="w-full px-6 md:px-12 h-20 flex items-center justify-between">
          <div className="flex items-center space-x-3.5">
            <img src={logoOriental} alt="Logo Région Oriental" className="h-14 w-auto object-contain" />
            <div>
              <div className="font-bold text-xl text-blue-900 tracking-tight leading-tight">
                Smart Wilaya Oriental
              </div>
              <span className="text-xs font-semibold text-blue-600 uppercase tracking-wider">
                Espace Administrateur
              </span>
            </div>
          </div>
          <UserMenu />
        </div>
      </header>
 
      <main className="font-body w-full px-6 md:px-12 py-10">
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 text-sm text-slate-500 hover:text-slate-700 font-medium mb-6 transition"
        >
          <ArrowLeft className="w-4 h-4" />
          Retour
        </button>
 
        <div className="grid grid-cols-1 lg:grid-cols-[420px_1fr] gap-8 items-stretch">
          <div className="h-full">
            <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-[#0F1E3D] to-[#16295A] p-10 md:p-12 shadow-2xl shadow-blue-900/20 h-full flex flex-col">
              <ZelligeMotif />
 
              <div className="relative flex flex-col h-full">
                <div className="w-28 h-28 rounded-full bg-white/10 border-2 border-[#D4AF37]/70 flex items-center justify-center">
                  <span className="font-display text-4xl font-semibold text-white">{initiales}</span>
                </div>
 
                <div className="mt-7">
                  {loadingProfil ? (
                    <div className="h-8 w-44 bg-white/10 rounded animate-pulse" />
                  ) : (
                    <h2 className="font-display text-3xl font-semibold text-white leading-snug">
                      {form.prenom} {form.nom}
                    </h2>
                  )}
                  <span className="inline-flex items-center mt-3 px-3 py-1.5 rounded-full bg-[#D4AF37]/15 text-[#E8C767] text-xs font-bold uppercase tracking-wider">
                    Administrateur
                  </span>
                </div>
 
                <div className="mt-10 pt-8 border-t border-white/10 space-y-6">
                  <div className="flex items-start gap-3.5">
                    <Mail className="w-5 h-5 text-white/40 mt-0.5 shrink-0" />
                    <span className="text-[15px] text-white/70 break-all">{profil?.email || '—'}</span>
                  </div>
                  <div className="flex items-start gap-3.5">
                    <Phone className="w-5 h-5 text-white/40 mt-0.5 shrink-0" />
                    <span className="text-[15px] text-white/70">{form.telephone || 'Non renseigné'}</span>
                  </div>
                  <div className="flex items-start gap-3.5">
                    {form.notificationsActivees ? (
                      <Bell className="w-5 h-5 text-white/40 mt-0.5 shrink-0" />
                    ) : (
                      <BellOff className="w-5 h-5 text-white/40 mt-0.5 shrink-0" />
                    )}
                    <span className="text-[15px] text-white/70">
                      Notifications {form.notificationsActivees ? 'activées' : 'désactivées'}
                    </span>
                  </div>
                </div>
 
                <div className="mt-auto pt-10 flex items-center gap-2 text-white/30 text-xs">
                  <ShieldCheck className="w-4 h-4" />
                  Espace Administrateur — Smart Wilaya Oriental
                </div>
              </div>
            </div>
          </div>
 
          <div className="bg-white rounded-3xl shadow-xl shadow-slate-200/60 border border-slate-100 overflow-hidden">
            <div className="px-6 md:px-8 pt-6">
              <div className="inline-flex p-1 bg-slate-100 rounded-xl">
                <button
                  onClick={() => setOnglet('infos')}
                  className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-semibold transition-all duration-200 ${
                    onglet === 'infos'
                      ? 'bg-white text-blue-700 shadow-sm'
                      : 'text-slate-500 hover:text-slate-700'
                  }`}
                >
                  <User className="w-4 h-4" />
                  Informations
                </button>
                <button
                  onClick={() => setOnglet('mot-de-passe')}
                  className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-semibold transition-all duration-200 ${
                    onglet === 'mot-de-passe'
                      ? 'bg-white text-blue-700 shadow-sm'
                      : 'text-slate-500 hover:text-slate-700'
                  }`}
                >
                  <Lock className="w-4 h-4" />
                  Mot de passe
                </button>
              </div>
            </div>
 
            <div className="px-6 py-7 md:px-8 md:py-8">
              {error && (
                <div className="flex items-start gap-2.5 mb-5 px-4 py-3 rounded-xl bg-red-50 border border-red-100">
                  <AlertTriangle className="w-4 h-4 text-red-600 mt-0.5 shrink-0" />
                  <p className="text-sm text-red-700 font-medium">{error}</p>
                </div>
              )}
              {message && (
                <div className="flex items-start gap-2.5 mb-5 px-4 py-3 rounded-xl bg-green-50 border border-green-100">
                  <CheckCircle2 className="w-4 h-4 text-green-600 mt-0.5 shrink-0" />
                  <p className="text-sm text-green-700 font-medium">{message}</p>
                </div>
              )}
 
              {loadingProfil ? (
                <p className="text-sm text-slate-400 flex items-center gap-2 py-14 justify-center">
                  <Loader2 className="w-4 h-4 animate-spin" /> Chargement...
                </p>
              ) : onglet === 'infos' ? (
                <div className="space-y-6">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <label className="block text-xs font-semibold text-slate-600 mb-1.5">Nom</label>
                      <input
                        type="text"
                        value={form.nom}
                        onChange={(e) => setForm({ ...form, nom: e.target.value })}
                        className="w-full border border-slate-200 rounded-xl px-4 py-2.5 text-sm text-slate-900 outline-none transition focus:ring-2 focus:ring-blue-600/40 focus:border-blue-600"
                      />
                    </div>
                    <div>
                      <label className="block text-xs font-semibold text-slate-600 mb-1.5">Prénom</label>
                      <input
                        type="text"
                        value={form.prenom}
                        onChange={(e) => setForm({ ...form, prenom: e.target.value })}
                        className="w-full border border-slate-200 rounded-xl px-4 py-2.5 text-sm text-slate-900 outline-none transition focus:ring-2 focus:ring-blue-600/40 focus:border-blue-600"
                      />
                    </div>
                  </div>
 
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <label className="block text-xs font-semibold text-slate-600 mb-1.5">Email</label>
                      <div className="relative">
                        <Mail className="w-4 h-4 text-slate-300 absolute left-4 top-1/2 -translate-y-1/2" />
                        <input
                          type="email"
                          value={profil?.email || ''}
                          disabled
                          className="w-full border border-slate-200 rounded-xl pl-11 pr-4 py-2.5 text-sm bg-slate-50 text-slate-400 cursor-not-allowed"
                        />
                      </div>
                    </div>

                    <div>
                      <label className="block text-xs font-semibold text-slate-600 mb-1.5">Téléphone</label>
                      <div className="relative">
                        <Phone className="w-4 h-4 text-slate-300 absolute left-4 top-1/2 -translate-y-1/2" />
                        <input
                          type="text"
                          value={form.telephone}
                          onChange={(e) => setForm({ ...form, telephone: e.target.value })}
                          placeholder="+212 6 00 00 00 00"
                          className="w-full border border-slate-200 rounded-xl pl-11 pr-4 py-2.5 text-sm text-slate-900 outline-none transition focus:ring-2 focus:ring-blue-600/40 focus:border-blue-600"
                        />
                      </div>
                    </div>
                  </div>
 
                  <label className="flex items-center justify-between gap-3 px-4 py-3.5 rounded-xl border border-slate-200 cursor-pointer hover:border-slate-300 transition">
                    <span className="flex items-center gap-2.5 text-sm text-slate-700 font-medium">
                      <Bell className="w-4 h-4 text-slate-400" />
                      Recevoir les notifications
                    </span>
                    <span className="relative inline-flex items-center">
                      <input
                        type="checkbox"
                        checked={form.notificationsActivees}
                        onChange={(e) => setForm({ ...form, notificationsActivees: e.target.checked })}
                        className="sr-only peer"
                      />
                      <span className="w-10 h-6 bg-slate-200 rounded-full peer-checked:bg-blue-700 transition-colors" />
                      <span className="absolute left-1 top-1 w-4 h-4 bg-white rounded-full shadow transition-transform peer-checked:translate-x-4" />
                    </span>
                  </label>
 
                  <button
                    onClick={enregistrerInfos}
                    disabled={savingInfos}
                    className="flex items-center gap-2 bg-blue-700 hover:bg-blue-800 disabled:opacity-60 text-white font-semibold px-6 py-2.5 rounded-xl transition shadow-sm shadow-blue-700/20"
                  >
                    {savingInfos ? (
                      <>
                        <Loader2 className="w-4 h-4 animate-spin" /> Enregistrement...
                      </>
                    ) : (
                      <>
                        <Save className="w-4 h-4" /> Enregistrer les modifications
                      </>
                    )}
                  </button>
                </div>
              ) : (
                <div className="space-y-6 max-w-xl">
                  <div className="flex items-center gap-2.5 px-4 py-3 rounded-xl bg-blue-50/60 border border-blue-100 mb-2">
                    <ShieldCheck className="w-4 h-4 text-blue-700 shrink-0" />
                    <p className="text-xs text-blue-800 leading-snug">
                      Utilisez un mot de passe d'au moins 8 caractères, avec chiffres et symboles.
                    </p>
                  </div>
 
                  <div>
                    <label className="block text-xs font-semibold text-slate-600 mb-1.5">Mot de passe actuel</label>
                    <div className="relative">
                      <KeyRound className="w-4 h-4 text-slate-300 absolute left-4 top-1/2 -translate-y-1/2" />
                      <input
                        type="password"
                        value={motDePasse.ancienMotDePasse}
                        onChange={(e) => setMotDePasse({ ...motDePasse, ancienMotDePasse: e.target.value })}
                        className="w-full border border-slate-200 rounded-xl pl-11 pr-4 py-2.5 text-sm outline-none transition focus:ring-2 focus:ring-blue-600/40 focus:border-blue-600"
                      />
                    </div>
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-600 mb-1.5">Nouveau mot de passe</label>
                    <div className="relative">
                      <Lock className="w-4 h-4 text-slate-300 absolute left-4 top-1/2 -translate-y-1/2" />
                      <input
                        type="password"
                        value={motDePasse.nouveauMotDePasse}
                        onChange={(e) => setMotDePasse({ ...motDePasse, nouveauMotDePasse: e.target.value })}
                        placeholder="8 caractères minimum"
                        className="w-full border border-slate-200 rounded-xl pl-11 pr-4 py-2.5 text-sm outline-none transition focus:ring-2 focus:ring-blue-600/40 focus:border-blue-600"
                      />
                    </div>
                    <ForceMotDePasse valeur={motDePasse.nouveauMotDePasse} />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-600 mb-1.5">
                      Confirmer le nouveau mot de passe
                    </label>
                    <div className="relative">
                      <Lock className="w-4 h-4 text-slate-300 absolute left-4 top-1/2 -translate-y-1/2" />
                      <input
                        type="password"
                        value={motDePasse.confirmation}
                        onChange={(e) => setMotDePasse({ ...motDePasse, confirmation: e.target.value })}
                        className="w-full border border-slate-200 rounded-xl pl-11 pr-4 py-2.5 text-sm outline-none transition focus:ring-2 focus:ring-blue-600/40 focus:border-blue-600"
                      />
                    </div>
                  </div>
 
                  <button
                    onClick={enregistrerMotDePasse}
                    disabled={savingMdp}
                    className="flex items-center gap-2 bg-blue-700 hover:bg-blue-800 disabled:opacity-60 text-white font-semibold px-6 py-2.5 rounded-xl transition shadow-sm shadow-blue-700/20"
                  >
                    {savingMdp ? (
                      <>
                        <Loader2 className="w-4 h-4 animate-spin" /> Modification...
                      </>
                    ) : (
                      <>
                        <ShieldCheck className="w-4 h-4" /> Changer le mot de passe
                      </>
                    )}
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
