import React from 'react';
import { ShieldCheck, MapPin, Clock, Bell, Lock, BarChart3 } from 'lucide-react';

export default function Features() {
  const featuresList = [
    { title: "Signalement Citoyen", desc: "Signalez en quelques clics tout dysfonctionnement.", icon: ShieldCheck },
    { title: "Suivi en Temps Réel", desc: "Une timeline précise vous informe de chaque étape.", icon: Clock },
    { title: "Carte Interactive", desc: "Visualisez les interventions en cours sur la région.", icon: MapPin },
    { title: "Alertes Intelligentes", desc: "Notifications push pour les travaux programmés.", icon: Bell },
    { title: "Accès Sécurisé", desc: "Authentification forte pour protéger vos données.", icon: Lock },
    { title: "Analytiques Décisionnels", desc: "Tableaux de bord avancés pour les administrateurs.", icon: BarChart3 }
  ];

  return (
    <section className="py-24 px-6 bg-slate-50">
      <div className="max-w-7xl mx-auto text-center mb-16">
        <h2 className="text-3xl font-bold text-slate-900">Fonctionnalités Clés</h2>
        <p className="text-slate-600 mt-3">Une infrastructure digitale robuste pour une gestion urbaine simplifiée.</p>
      </div>

      <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-8">
        {featuresList.map((item, index) => {
          const IconComponent = item.icon;
          return (
            <div key={index} className="bg-white p-8 rounded-2xl shadow-sm border border-slate-200/60 hover:shadow-md transition">
              <div className="bg-blue-50 text-blue-700 w-12 h-12 rounded-xl flex items-center justify-center mb-6">
                <IconComponent className="w-6 h-6" />
              </div>
              <h3 className="text-xl font-semibold text-slate-900 mb-2">{item.title}</h3>
              <p className="text-slate-600 text-sm leading-relaxed">{item.desc}</p>
            </div>
          );
        })}
      </div>
    </section>
  );
}