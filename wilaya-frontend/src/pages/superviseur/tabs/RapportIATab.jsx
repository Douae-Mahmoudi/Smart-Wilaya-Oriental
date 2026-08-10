import React, { useState } from 'react';
import { Sparkles, Loader2, AlertTriangle } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { signalementClient } from '../../../api/client';
 
const markdownComponents = {
  h1: ({ node, ...props }) => <h1 className="text-xl font-bold text-blue-900 mt-6 mb-3 first:mt-0" {...props} />,
  h2: ({ node, ...props }) => <h2 className="text-lg font-bold text-blue-900 mt-6 mb-3 first:mt-0" {...props} />,
  h3: ({ node, ...props }) => <h3 className="text-base font-bold text-blue-800 mt-5 mb-2" {...props} />,
  p: ({ node, ...props }) => <p className="text-sm text-slate-700 leading-relaxed mb-3" {...props} />,
  strong: ({ node, ...props }) => <strong className="font-bold text-slate-900" {...props} />,
  ul: ({ node, ...props }) => <ul className="list-disc list-inside text-sm text-slate-700 mb-3 space-y-1" {...props} />,
  ol: ({ node, ...props }) => <ol className="list-decimal list-inside text-sm text-slate-700 mb-3 space-y-1" {...props} />,
  li: ({ node, ...props }) => <li className="leading-relaxed" {...props} />,
  hr: ({ node, ...props }) => <hr className="my-6 border-slate-200" {...props} />,
  table: ({ node, ...props }) => (
    <div className="overflow-x-auto mb-4">
      <table className="w-full text-sm border-collapse" {...props} />
    </div>
  ),
  thead: ({ node, ...props }) => <thead className="bg-slate-50" {...props} />,
  th: ({ node, ...props }) => (
    <th className="border border-slate-200 px-3 py-2 text-left font-semibold text-slate-700" {...props} />
  ),
  td: ({ node, ...props }) => <td className="border border-slate-200 px-3 py-2 text-slate-700" {...props} />,
};
 
export default function RapportIATab() {
  const [rapport, setRapport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
 
  const genererRapport = async () => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await signalementClient.post('/rapports-ia/generer');
      setRapport(data);
    } catch (err) {
      setError(err.response?.data?.message || 'Impossible de générer le rapport pour le moment.');
    } finally {
      setLoading(false);
    }
  };
 
  return (
    <div className="space-y-6">
      {error && (
        <div className="flex items-start gap-2.5 px-4 py-3 rounded-xl bg-red-50 border border-red-100">
          <AlertTriangle className="w-4 h-4 text-red-600 mt-0.5 shrink-0" />
          <p className="text-sm text-red-700 font-medium">{error}</p>
        </div>
      )}
 
      <div className="bg-white rounded-2xl shadow-xl p-6 md:p-8">
        <div className="flex items-center justify-between mb-5">
          <h2 className="text-lg font-bold text-slate-900 flex items-center gap-2">
            <Sparkles className="w-5 h-5 text-blue-700" />
            Rapport IA
          </h2>
          <button
            onClick={genererRapport}
            disabled={loading}
            className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-sm font-semibold bg-blue-700 hover:bg-blue-800 disabled:opacity-50 text-white transition"
          >
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Sparkles className="w-4 h-4" />}
            Générer un rapport IA
          </button>
        </div>
 
        {!rapport && !loading && (
          <p className="text-sm text-slate-400 text-center py-14">
            Aucun rapport généré pour le moment. Cliquez sur "Générer un rapport IA" pour en créer un.
          </p>
        )}
 
        {loading && (
          <div className="space-y-3">
            {Array.from({ length: 4 }).map((_, i) => (
              <div key={i} className="h-5 bg-slate-50 rounded-lg animate-pulse" />
            ))}
          </div>
        )}
 
        {rapport && !loading && (
          <div>
            <p className="text-xs text-slate-400 mb-4">
              Généré le {new Date(rapport.dateGeneration).toLocaleString('fr-FR')}
            </p>
            <div>
              <ReactMarkdown remarkPlugins={[remarkGfm]} components={markdownComponents}>
                {rapport.contenu}
              </ReactMarkdown>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
