import React, { useState, useEffect, useRef, useCallback } from 'react';
import { MapContainer, TileLayer, Marker, useMapEvents, useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png';
import markerIcon from 'leaflet/dist/images/marker-icon.png';
import markerShadow from 'leaflet/dist/images/marker-shadow.png';
import { Search, MapPin, Loader2 } from 'lucide-react';
 
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});
 
const CENTRE_PAR_DEFAUT = [34.6805, -1.9089];
const NOMINATIM_BASE = 'https://nominatim.openstreetmap.org';
const VIEWBOX_ORIENTAL = '-3.6,35.6,-1.0,32.0';
 
function ClicCarte({ onSelect }) {
  useMapEvents({
    click(e) {
      onSelect(e.latlng.lat, e.latlng.lng);
    },
  });
  return null;
}
 
function RecentrerCarte({ position, zoom }) {
  const map = useMap();
  useEffect(() => {
    if (position) {
      map.flyTo(position, zoom ?? map.getZoom(), { duration: 0.8 });
    }
  }, [position, zoom, map]);
  return null;
}
 
export default function LocationPicker({ latitude, longitude, onChange, height = '280px' }) {
  const position = latitude && longitude ? [latitude, longitude] : null;
 
  const [recherche, setRecherche] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [rechercheEnCours, setRechercheEnCours] = useState(false);
  const [adresseResolue, setAdresseResolue] = useState('');
  const [volEnCours, setVolEnCours] = useState(null);
  const debounceRef = useRef(null);
 
  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);
    if (recherche.trim().length < 3) {
      setSuggestions([]);
      return;
    }
    debounceRef.current = setTimeout(async () => {
      setRechercheEnCours(true);
      try {
        const params = new URLSearchParams({
          q: recherche,
          format: 'jsonv2',
          addressdetails: '1',
          limit: '5',
          countrycodes: 'ma',
          viewbox: VIEWBOX_ORIENTAL,
        });
        const res = await fetch(`${NOMINATIM_BASE}/search?${params}`);
        const data = await res.json();
        setSuggestions(Array.isArray(data) ? data : []);
      } catch {
        setSuggestions([]);
      } finally {
        setRechercheEnCours(false);
      }
    }, 400);
    return () => clearTimeout(debounceRef.current);
  }, [recherche]);
 
  const choisirSuggestion = (s) => {
    const lat = parseFloat(s.lat);
    const lon = parseFloat(s.lon);
    onChange(lat, lon);
    setAdresseResolue(s.display_name);
    setRecherche(s.display_name);
    setSuggestions([]);
    setVolEnCours([lat, lon]);
  };
 
  const geocoderInverse = useCallback(async (lat, lng) => {
    try {
      const params = new URLSearchParams({ lat, lon: lng, format: 'jsonv2' });
      const res = await fetch(`${NOMINATIM_BASE}/reverse?${params}`);
      const data = await res.json();
      setAdresseResolue(data.display_name || '');
    } catch {
      setAdresseResolue('');
    }
  }, []);
 
  const gererClicCarte = (lat, lng) => {
    onChange(lat, lng);
    geocoderInverse(lat, lng);
  };
 
  return (
    <div className="space-y-2">
      <div className="relative">
        <div className="flex items-center border border-slate-200 rounded-xl px-3 py-2.5 focus-within:ring-2 focus-within:ring-blue-600/40 focus-within:border-blue-600">
          <Search className="w-4 h-4 text-slate-400 mr-2 shrink-0" />
          <input
            type="text"
            value={recherche}
            onChange={(e) => setRecherche(e.target.value)}
            placeholder="Rechercher une adresse ou un quartier..."
            className="w-full text-sm outline-none"
          />
          {rechercheEnCours && <Loader2 className="w-4 h-4 text-slate-400 animate-spin shrink-0" />}
        </div>
 
        {suggestions.length > 0 && (
          <ul className="absolute z-[1000] w-full bg-white border border-slate-200 rounded-xl mt-1 shadow-lg max-h-56 overflow-y-auto">
            {suggestions.map((s) => (
              <li key={s.place_id}>
                <button
                  type="button"
                  onClick={() => choisirSuggestion(s)}
                  className="w-full text-left px-3 py-2.5 text-sm text-slate-700 hover:bg-blue-50 flex items-start gap-2"
                >
                  <MapPin className="w-4 h-4 text-blue-600 mt-0.5 shrink-0" />
                  {s.display_name}
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
 
      <div className="rounded-xl overflow-hidden border border-slate-200" style={{ height }}>
        <MapContainer
          center={position || CENTRE_PAR_DEFAUT}
          zoom={position ? 17 : 12}
          style={{ width: '100%', height: '100%' }}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <ClicCarte onSelect={gererClicCarte} />
          {position && <Marker position={position} />}
          <RecentrerCarte position={volEnCours} zoom={17} />
        </MapContainer>
      </div>
 
      {adresseResolue && (
        <p className="text-xs text-slate-500 flex items-start gap-1.5">
          <MapPin className="w-3.5 h-3.5 mt-0.5 shrink-0 text-blue-600" />
          {adresseResolue}
        </p>
      )}
 
      <p className="text-xs text-slate-400">
        Utilisez la recherche ci-dessus pour un point précis, ou zoomez avant de cliquer sur la carte.
      </p>
    </div>
  );
}
 













































































