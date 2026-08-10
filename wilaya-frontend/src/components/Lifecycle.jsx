import React, { useState, useEffect } from 'react';

export default function Lifecycle() {
  const [activeStepIndex, setActiveStepIndex] = useState(0);

  useEffect(() => {
    const totalSteps = steps.length;

    const timer = setInterval(() => {
      setActiveStepIndex((prevIndex) => (prevIndex + 1) % totalSteps);
    }, 5000); 
    return () => clearInterval(timer);
    
  }, []);

  const steps = [
    { num: "01", title: "Signalement", desc: "L'usager crée une alerte géolocalisée." },
    { num: "02", title: "Analyse", desc: "Vérification et validation par le back-office." },
    { num: "03", title: "Affectation", desc: "Envoi de la mission à l'équipe technique." },
    { num: "04", title: "Intervention", desc: "Résolution du problème sur le terrain." },
    { num: "05", title: "Mise à jour", desc: "Notification de progression à l'usager." },
    { num: "06", title: "Clôture", desc: "Validation finale et archivage du dossier.", success: true }
  ];

  const getStepClass = (index) => {
    if (index === activeStepIndex) {
      return "scale-110 bg-blue-700 shadow-xl shadow-blue-600/40";
    } else if (index < activeStepIndex) {
      return steps[index].success 
        ? "bg-emerald-600 shadow-lg shadow-emerald-600/30" 
        : "bg-blue-900 opacity-80";
    } else {
      return "bg-slate-300 text-slate-500";
    }
  };

  return (
    <section className="py-24 px-6 bg-slate-50">
      <div className="max-w-7xl mx-auto text-center mb-16">
        <h2 className="text-3xl font-bold text-slate-900">Cycle de Vie d'une Intervention</h2>
        <p className="text-slate-500 mt-3">Défilement automatique pour simuler l'état du traitement.</p>
      </div>

      <div className="max-w-7xl mx-auto grid grid-cols-2 md:grid-cols-6 gap-6 relative">
        <div className="absolute top-6 left-1/2 right-1/2 h-0.5 bg-slate-200 -translate-x-1/2 hidden md:block" style={{ width: '75%' }}></div>

        {steps.map((s, index) => (
          <div 
            key={index} 
            className={`flex flex-col items-center text-center relative z-10 transition-all duration-700 ease-in-out ${index > activeStepIndex ? 'opacity-70' : ''}`}
          >
            <div className={`w-14 h-14 md:w-16 md:h-16 rounded-full flex items-center justify-center text-white font-extrabold mb-5 shadow-lg transition-all duration-700 ease-in-out ${getStepClass(index)}`}>
              <span className="text-lg md:text-2xl">{s.num}</span>
            </div>
            
            <div className={`transition-all duration-700 ${index === activeStepIndex ? 'text-blue-700' : 'text-slate-800'}`}>
              <h4 className={`font-bold text-base mb-1.5 ${index === activeStepIndex ? 'text-blue-800' : 'text-slate-950'}`}>{s.title}</h4>
              <p className={`text-sm leading-normal transition-opacity duration-700 ${index === activeStepIndex ? 'opacity-100' : 'opacity-80'}`}>{s.desc}</p>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}