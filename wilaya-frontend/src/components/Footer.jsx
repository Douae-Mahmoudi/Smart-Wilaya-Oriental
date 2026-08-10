import React from 'react';
import { MapPin, Mail, Phone } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="bg-slate-950 text-slate-400 py-12 px-6 border-t border-slate-900">
      <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center gap-8">
        <div>
          <div className="text-white font-bold text-lg mb-2">Smart Wilaya Oriental</div>
          <p className="text-xs text-slate-500">© 2026 Smart Wilaya Oriental – Royaume du Maroc. Tous droits réservés.</p>
        </div>
        <div className="text-sm space-y-2 text-right md:text-left">
          <p className="flex items-center gap-2 justify-end md:justify-start"><MapPin className="w-4 h-4 text-blue-500" /> Oujda, Region Oriental, Maroc</p>
          <p className="flex items-center gap-2 justify-end md:justify-start"><Mail className="w-4 h-4 text-blue-500" /> contact@oriental-smart.ma</p>
          <p className="flex items-center gap-2 justify-end md:justify-start"><Phone className="w-4 h-4 text-blue-500" /> +212 (0) 536 00 00 00</p>
        </div>
      </div>
    </footer>
  );
}