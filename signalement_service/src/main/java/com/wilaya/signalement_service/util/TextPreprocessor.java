package com.wilaya.signalement_service.util;

import org.tartarus.snowball.ext.FrenchStemmer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TextPreprocessor {

    private static final List<String> STOPWORDS = List.of(
            "le", "la", "les", "un", "une", "des", "pour", "par", "avec",
            "sans", "chez", "dans", "sur", "sous", "entre", "en", "à", "au",
            "aux", "du", "de", "et", "ou", "donc", "or", "ni", "car", "mais"
    );


    private static final Map<String, String> SYNONYMS = new HashMap<>();


    private static final Map<String, String> PHRASES = new HashMap<>();

    static {
        addGroup("eau", "eau", "hydraulique");
        addGroup("coupure", "coupure", "interruption", "panne", "arret", "suspension",
                "indisponibilite", "indisponible", "coupee", "coupe");
        addGroup("alimentation", "alimentation", "approvisionnement", "distribution", "desserte", "fourniture");
        addGroup("fuite", "fuite", "ecoulement", "infiltration", "rupture");
        addGroup("canalisation", "canalisation", "conduite", "tuyauterie");
        addGroup("pollution", "pollution", "contamination", "impurete", "pollue", "polluee", "contaminee");
        addGroup("robinet", "robinet");
        addPhrase("borne-fontaine", "robinet");
        addPhrase("borne fontaine", "robinet");

        addGroup("electricite", "electricite", "electrique", "courant", "energie");
        addGroup("reseau", "reseau", "ligne", "installation");
        addGroup("eclairage", "eclairage", "lampadaire", "lampe", "luminaire");
        addGroup("obscurite", "obscurite", "noir", "sombre");

        addGroup("route", "route", "chaussee", "voie", "voirie");
        addGroup("trottoir", "trottoir");
        addPhrase("passage pieton", "trottoir");
        addGroup("degradation", "degradation", "dommage", "deterioration", "usure", "degrade", "degradee", "endommage", "endommagee");
        addGroup("nidpoule", "cavite", "trou");
        addPhrase("nid-de-poule", "nidpoule");
        addPhrase("nid de poule", "nidpoule");
        addGroup("fissure", "fissure", "craquelure");
        addGroup("reparation", "reparation", "rehabilitation");
        addPhrase("remise en etat", "reparation");

        addGroup("dechets", "dechets", "detritus", "ordures", "immondices");
        addGroup("salete", "salete", "insalubrite", "malproprete");
        addGroup("nettoyage", "nettoyage", "balayage", "entretien");
        addGroup("encombrants", "encombrants", "gravats", "debris");

        addGroup("espacevert", "verdure");
        addPhrase("espace vert", "espacevert");
        addPhrase("espaces verts", "espacevert");
        addGroup("vegetation", "vegetation", "plantes");
        addGroup("herbe", "herbe", "gazon");
        addGroup("arbre", "arbre", "arbuste");
        addGroup("elagage", "elagage", "taille");
        addGroup("debroussaillage", "debroussaillage", "desherbage");

        addGroup("urgence", "urgence", "critique", "prioritaire");
        addGroup("signalement", "signalement", "incident", "probleme");
        addGroup("resolu", "resolu", "corrige", "repare", "traite");
        addGroup("encours", "traitement");
        addPhrase("en cours", "encours");
    }


    private static void addGroup(String canonical, String... words) {
        SYNONYMS.put(normalizeKey(canonical), canonical);
        for (String w : words) {
            SYNONYMS.put(normalizeKey(w), canonical);
        }
    }


    private static void addPhrase(String phrase, String canonical) {
        PHRASES.put(stripAccents(phrase.toLowerCase()), canonical);
    }

    private static String normalizeKey(String word) {
        return stripAccents(word.toLowerCase());
    }


    private static String stripAccents(String s) {
        return s
                .replaceAll("[àâ]", "a")
                .replaceAll("[éèêë]", "e")
                .replaceAll("[ïî]", "i")
                .replaceAll("[ôö]", "o")
                .replaceAll("[ùûü]", "u")
                .replaceAll("ç", "c");
    }

    public static String stem(String text) {
        if (text == null || text.isBlank()) return "";

        String lower = text.toLowerCase();

        lower = applyPhrases(lower);

        String cleaned = lower
                .replaceAll("[^a-zàâéèêëïîôùûç ]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        FrenchStemmer stemmer = new FrenchStemmer();
        return Arrays.stream(cleaned.split(" "))
                .filter(word -> word.length() > 1 && !STOPWORDS.contains(word))
                .map(TextPreprocessor::applySynonym)
                .map(word -> {
                    stemmer.setCurrent(word);
                    stemmer.stem();
                    return stemmer.getCurrent();
                })
                .collect(Collectors.joining(" "));
    }


    private static String applyPhrases(String text) {
        String result = text;
        for (Map.Entry<String, String> entry : PHRASES.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }


    private static String applySynonym(String word) {
        return SYNONYMS.getOrDefault(stripAccents(word), word);
    }
}


