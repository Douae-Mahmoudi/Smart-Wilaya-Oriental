import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Construction, Lightbulb, Droplet, Trash2, Trees, Camera, CheckCircle2, MapPin, AlertTriangle } from 'lucide-react';
import LocationPicker from '../components/LocationPicker';
 
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:7057';
 
const TYPES = [
  { value: 'EAU', label: 'Eau', icon: Droplet },
  { value: 'ELECTRICITE', label: 'Électricité', icon: Lightbulb },
  { value: 'VOIRIE', label: 'Voirie', icon: Construction },
  { value: 'PROPRETE', label: 'Propreté', icon: Trash2 },
  { value: 'ESPACES_VERTS', label: 'Espaces verts', icon: Trees },
];
 
const MAX_DESCRIPTION = 500;
const MAX_PHOTO_SIZE = 5 * 1024 * 1024; 
 
export default function SignalementPage() {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [verification, setVerification] = useState(false);
  const [error, setError] = useState(null);
  const [numeroSuivi, setNumeroSuivi] = useState(null);
  const [avertissementSimilaire, setAvertissementSimilaire] = useState(null);
 
  const [form, setForm] = useState({
    cinDeclarant: '',
    type: '',
    description: '',
    zone: '',
    adresse: '',
    latitude: null,
    longitude: null,
  });
  const [photo, setPhoto] = useState(null);
  const [photoPreview, setPhotoPreview] = useState(null);
  const [photoError, setPhotoError] = useState(null);
 
  const updateField = (field, value) => setForm((prev) => ({ ...prev, [field]: value }));
 
  const handleMapSelect = (lat, lng) => {
    setForm((prev) => ({ ...prev, latitude: lat, longitude: lng }));
  };
 
  const handlePhotoChange = (file) => {
    setPhotoError(null);
    if (!file) return;
    if (!['image/jpeg', 'image/png'].includes(file.type)) {
      setPhotoError('Formats acceptés : JPG, PNG.');
      return;
    }
    if (file.size > MAX_PHOTO_SIZE) {
      setPhotoError('La photo dépasse 5 Mo.');
      return;
    }
    setPhoto(file);
    setPhotoPreview(URL.createObjectURL(file));
  };
 
  const isStep1Valid =
    form.cinDeclarant.trim() &&
    form.type &&
    form.description.trim() &&
    form.zone.trim() &&
    form.latitude !== null &&
    form.longitude !== null;
 
  const goToStep2 = () => {
    if (!isStep1Valid) {
      setError(
        form.latitude === null
          ? 'Merci de cliquer sur la carte pour indiquer la position exacte du problème.'
          : 'Merci de remplir tous les champs obligatoires avant de continuer.'
      );
      return;
    }
    setError(null);
    setStep(2);
  };
 
  const envoyerSignalement = async () => {
    setLoading(true);
    setError(null);
    try {
      const formData = new FormData();
      formData.append(
        'data',
        new Blob([JSON.stringify(form)], { type: 'application/json' })
      );
      if (photo) formData.append('photo', photo);
 
      const response = await fetch(`${API_BASE_URL}/signalements`, {
        method: 'POST',
        body: formData,
      });
 
      if (!response.ok) {
        let messageServeur = `Erreur ${response.status}`;
        try {
          const errBody = await response.json();
          messageServeur = errBody.message || errBody.error || JSON.stringify(errBody);
        } catch {
        }
        throw new Error(messageServeur);
      }
 
      const data = await response.json();
      setNumeroSuivi(data.numeroSuivi);
    } catch (err) {
      setError(err.message || 'Une erreur est survenue.');
    } finally {
      setLoading(false);
    }
  };
 
  const verifierPuisEnvoyer = async () => {
    setVerification(true);
    setError(null);
    try {
      const response = await fetch(`${API_BASE_URL}/signalements/verifier-similaire-resolu`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          type: form.type,
          description: form.description,
          zone: form.zone,
          latitude: form.latitude,
          longitude: form.longitude,
        }),
      });
 
      if (response.ok) {
        const data = await response.json();
        if (data.existe) {
          setVerification(false);
          setAvertissementSimilaire(data);
          return;
        }
      }
    } catch {
    }
    setVerification(false);
    envoyerSignalement();
  };
 
  if (numeroSuivi) {
    return (
      <section className="min-h-[70vh] flex items-center justify-center px-6 py-24 bg-slate-50">
        <div className="max-w-md w-full bg-white rounded-2xl shadow-xl p-8 text-center">
          <CheckCircle2 className="w-14 h-14 text-emerald-500 mx-auto mb-4" />
          <h2 className="text-xl font-bold text-slate-900 mb-2">Signalement envoyé</h2>
          <p className="text-slate-600 text-sm mb-6">
            Conservez précieusement ce numéro de suivi pour consulter l'état de votre demande.
          </p>
          <div className="bg-blue-50 text-blue-700 font-mono font-semibold text-lg rounded-xl py-3 mb-6">
            {numeroSuivi}
          </div>
          <button
            onClick={() => navigate('/suivi')}
            className="w-full bg-blue-700 hover:bg-blue-800 text-white font-semibold py-3 rounded-xl transition"
          >
            Suivre ce signalement
          </button>
          <button
            onClick={() => navigate('/')}
            className="w-full mt-3 text-slate-500 hover:text-slate-700 text-sm"
          >
            Retour à l'accueil
          </button>
        </div>
      </section>
    );
  }
 
  if (avertissementSimilaire) {
    return (
      <section className="min-h-[70vh] flex items-center justify-center px-6 py-24 bg-slate-50">
        <div className="max-w-md w-full bg-white rounded-2xl shadow-xl p-8 text-center">
          <AlertTriangle className="w-14 h-14 text-amber-500 mx-auto mb-4" />
          <h2 className="text-xl font-bold text-slate-900 mb-2">Problème déjà résolu récemment ?</h2>
          <p className="text-slate-600 text-sm mb-6">
            Un signalement très similaire a été résolu récemment à cet emplacement.
            S'agit-il d'un nouveau problème ?
          </p>
          <button
            onClick={() => navigate('/suivi', { state: { numeroSuivi: avertissementSimilaire.numeroSuivi } })}
            className="w-full bg-blue-700 hover:bg-blue-800 text-white font-semibold py-3 rounded-xl transition mb-3"
          >
            Consulter le signalement précédent
          </button>
          <button
            onClick={() => {
              setAvertissementSimilaire(null);
              envoyerSignalement();
            }}
            disabled={loading}
            className="w-full border border-slate-200 hover:bg-slate-50 disabled:opacity-60 text-slate-700 font-semibold py-3 rounded-xl transition"
          >
            {loading ? 'Envoi en cours...' : 'Créer un nouveau signalement'}
          </button>
        </div>
      </section>
    );
  }
 
  return (
    <section className="min-h-[80vh] bg-slate-50 py-16 px-6">
      <div className="max-w-2xl mx-auto bg-white rounded-2xl shadow-xl p-8">
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-xl font-bold text-slate-900">Nouveau signalement</h1>
          <span className="text-xs font-medium text-blue-700">Étape {step} sur 2</span>
        </div>
        <div className="h-1.5 w-full bg-slate-100 rounded-full mb-8 overflow-hidden">
          <div
            className="h-full bg-blue-700 rounded-full transition-all"
            style={{ width: step === 1 ? '50%' : '100%' }}
          />
        </div>
 
        {error && (
          <div className="bg-red-50 text-red-600 text-sm rounded-xl px-4 py-3 mb-6">{error}</div>
        )}
 
        {step === 1 && (
          <div className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Numéro CIN</label>
              <input
                type="text"
                value={form.cinDeclarant}
                onChange={(e) => updateField('cinDeclarant', e.target.value)}
                placeholder="Entrez votre numéro CIN"
                className="w-full border border-slate-200 rounded-xl px-4 py-2.5 text-sm focus:ring-2 focus:ring-blue-600 focus:border-transparent outline-none"
              />
            </div>
 
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">
                Type d'intervention
              </label>
              <div className="grid grid-cols-3 gap-3">
                {TYPES.map(({ value, label, icon: Icon }) => (
                  <button
                    key={value}
                    type="button"
                    onClick={() => updateField('type', value)}
                    className={`flex flex-col items-center gap-2 rounded-xl border py-4 text-xs font-medium transition ${
                      form.type === value
                        ? 'border-blue-700 bg-blue-50 text-blue-700'
                        : 'border-slate-200 text-slate-600 hover:border-slate-300'
                    }`}
                  >
                    <Icon className="w-5 h-5" />
                    {label}
                  </button>
                ))}
              </div>
            </div>
 
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Description détaillée
              </label>
              <textarea
                value={form.description}
                onChange={(e) =>
                  e.target.value.length <= MAX_DESCRIPTION && updateField('description', e.target.value)
                }
                placeholder="Décrivez précisément le problème constaté..."
                rows={4}
                className="w-full border border-slate-200 rounded-xl px-4 py-2.5 text-sm focus:ring-2 focus:ring-blue-600 focus:border-transparent outline-none resize-none"
              />
              <p className="text-right text-xs text-slate-400 mt-1">
                {form.description.length} / {MAX_DESCRIPTION}
              </p>
            </div>
 
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">Localisation</label>
              <div className="grid grid-cols-2 gap-3 mb-3">
                <input
                  type="text"
                  value={form.zone}
                  onChange={(e) => updateField('zone', e.target.value)}
                  placeholder="Quartier / Zone"
                  className="border border-slate-200 rounded-xl px-4 py-2.5 text-sm focus:ring-2 focus:ring-blue-600 focus:border-transparent outline-none"
                />
                <input
                  type="text"
                  value={form.adresse}
                  onChange={(e) => updateField('adresse', e.target.value)}
                  placeholder="Adresse précise (optionnel)"
                  className="border border-slate-200 rounded-xl px-4 py-2.5 text-sm focus:ring-2 focus:ring-blue-600 focus:border-transparent outline-none"
                />
              </div>
 
              <div className="flex items-center gap-2 text-xs text-slate-500 mb-2">
                <MapPin className="w-3.5 h-3.5 text-blue-700" />
                Cliquez sur la carte pour indiquer l'emplacement exact du problème
              </div>
              <div className="relative isolate" style={{ zIndex: 0 }}>
                <LocationPicker
                  latitude={form.latitude}
                  longitude={form.longitude}
                  onChange={handleMapSelect}
                />
              </div>
              {form.latitude !== null && (
                <p className="text-xs text-emerald-600 mt-2 font-medium">
                  ✓ Position enregistrée ({form.latitude.toFixed(5)}, {form.longitude.toFixed(5)})
                </p>
              )}
            </div>
 
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">
                Photo (optionnel)
              </label>
              <label
                htmlFor="photo-upload"
                className="flex flex-col items-center justify-center border-2 border-dashed border-slate-200 rounded-xl py-8 cursor-pointer hover:border-blue-300 transition"
              >
                {photoPreview ? (
                  <img src={photoPreview} alt="Aperçu" className="h-24 rounded-lg object-cover" />
                ) : (
                  <>
                    <Camera className="w-8 h-8 text-slate-400 mb-2" />
                    <span className="text-sm text-blue-700 font-medium">Parcourir les fichiers</span>
                    <span className="text-xs text-slate-400 mt-1">JPG, PNG (max 5 Mo)</span>
                  </>
                )}
              </label>
              <input
                id="photo-upload"
                type="file"
                accept="image/jpeg,image/png"
                className="hidden"
                onChange={(e) => handlePhotoChange(e.target.files[0])}
              />
              {photoError && <p className="text-xs text-red-600 mt-1">{photoError}</p>}
            </div>
 
            <div className="flex justify-end gap-3 pt-2">
              <button
                type="button"
                onClick={() => navigate('/')}
                className="px-5 py-2.5 rounded-xl text-sm font-medium text-slate-600 hover:bg-slate-100 transition"
              >
                Annuler
              </button>
              <button
                type="button"
                onClick={goToStep2}
                className="px-6 py-2.5 rounded-xl text-sm font-semibold bg-blue-700 hover:bg-blue-800 text-white transition"
              >
                Suivant
              </button>
            </div>
          </div>
        )}
 
        {step === 2 && (
          <div className="space-y-6">
            <h2 className="text-sm font-semibold text-slate-700">Récapitulatif de votre signalement</h2>
            <dl className="divide-y divide-slate-100 text-sm">
              <div className="flex justify-between py-3">
                <dt className="text-slate-500">CIN</dt>
                <dd className="text-slate-900 font-medium">{form.cinDeclarant}</dd>
              </div>
              <div className="flex justify-between py-3">
                <dt className="text-slate-500">Type</dt>
                <dd className="text-slate-900 font-medium">
                  {TYPES.find((t) => t.value === form.type)?.label}
                </dd>
              </div>
              <div className="py-3">
                <dt className="text-slate-500 mb-1">Description</dt>
                <dd className="text-slate-900">{form.description}</dd>
              </div>
              <div className="flex justify-between py-3">
                <dt className="text-slate-500">Zone</dt>
                <dd className="text-slate-900 font-medium">{form.zone}</dd>
              </div>
              <div className="flex justify-between py-3">
                <dt className="text-slate-500">Adresse</dt>
                <dd className="text-slate-900 font-medium">{form.adresse || 'Non renseignée'}</dd>
              </div>
              <div className="flex justify-between py-3">
                <dt className="text-slate-500">Position GPS</dt>
                <dd className="text-slate-900 font-medium">
                  {form.latitude?.toFixed(5)}, {form.longitude?.toFixed(5)}
                </dd>
              </div>
              {photoPreview && (
                <div className="py-3">
                  <dt className="text-slate-500 mb-2">Photo</dt>
                  <img src={photoPreview} alt="Aperçu" className="h-24 rounded-lg object-cover" />
                </div>
              )}
            </dl>
 
            <div className="bg-blue-50 text-blue-700 text-xs rounded-xl px-4 py-3">
              Le niveau de gravité sera déterminé automatiquement après analyse de votre description
              et de votre photo.
            </div>
 
            <div className="flex justify-end gap-3 pt-2">
              <button
                type="button"
                onClick={() => setStep(1)}
                className="px-5 py-2.5 rounded-xl text-sm font-medium text-slate-600 hover:bg-slate-100 transition"
              >
                Retour
              </button>
              <button
                type="button"
                disabled={loading || verification}
                onClick={verifierPuisEnvoyer}
                className="px-6 py-2.5 rounded-xl text-sm font-semibold bg-blue-700 hover:bg-blue-800 disabled:opacity-60 text-white transition"
              >
                {verification ? 'Vérification...' : loading ? 'Envoi en cours...' : 'Envoyer le signalement'}
              </button>
            </div>
          </div>
        )}
      </div>
    </section>
  );
}
