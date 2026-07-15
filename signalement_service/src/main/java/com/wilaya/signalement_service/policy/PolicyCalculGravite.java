package com.wilaya.signalement_service.policy;

import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.TypeIntervention;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;


@Component
public class PolicyCalculGravite {

    private static final Set<String> ZONES_SENSIBLES = Set.of(
            "hopital", "hôpital", "ecole", "école", "clinique", "creche", "crèche",
            "college", "collège", "lycee", "lycée", "marche", "marché", "mosquee", "mosquée"
    );

    private static final Set<TypeIntervention> TYPES_CRITIQUES = Set.of(
            TypeIntervention.EAU, TypeIntervention.ELECTRICITE
    );

    public NiveauGravite calculer(TypeIntervention type, String zone) {
        if (type == null) {
            throw new IllegalArgumentException("Le type d'intervention est obligatoire");
        }

        boolean zoneSensible = estZoneSensible(zone);
        boolean typeCritique = TYPES_CRITIQUES.contains(type);

        if (typeCritique && zoneSensible) {
            return NiveauGravite.HAUTE;
        }
        if (typeCritique) {
            return NiveauGravite.MOYENNE;
        }
        if (zoneSensible) {
            return NiveauGravite.MOYENNE;
        }
        return NiveauGravite.BASSE;
    }

    private boolean estZoneSensible(String zone) {
        if (zone == null || zone.isBlank()) {
            return false;
        }
        String zoneNormalisee = zone.toLowerCase(Locale.FRENCH);
        return ZONES_SENSIBLES.stream().anyMatch(zoneNormalisee::contains);
    }
}
