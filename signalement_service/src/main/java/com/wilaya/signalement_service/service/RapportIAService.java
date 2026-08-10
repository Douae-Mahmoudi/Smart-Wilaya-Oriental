package com.wilaya.signalement_service.service;

import com.wilaya.signalement_service.dto.RapportIAResponse;
import com.wilaya.signalement_service.dto.StatistiquesSignalementResponse;
import com.wilaya.signalement_service.exception.RapportIAIndisponibleException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class RapportIAService {

    private final SignalementService signalementService;
    private final GenerateurIAClient generateurIAClient;

    public RapportIAService(SignalementService signalementService, GenerateurIAClient generateurIAClient) {
        this.signalementService = signalementService;
        this.generateurIAClient = generateurIAClient;
    }

    public RapportIAResponse genererRapport() {
        StatistiquesSignalementResponse stats = signalementService.calculerStatistiques();
        String prompt = construirePrompt(stats);

        try {
            String contenu = generateurIAClient.generer(prompt);
            return new RapportIAResponse(contenu, LocalDateTime.now());
        } catch (RuntimeException e) {
            throw new RapportIAIndisponibleException("Le service IA est actuellement indisponible.", e);
        }
    }

    private String construirePrompt(StatistiquesSignalementResponse stats) {
        String dateActuelle = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH));

        return """
                Nous sommes le %s. Utilise IMPÉRATIVEMENT cette date exacte partout où une date \
                est nécessaire dans le rapport (mémorandum, date d'émission...). N'invente jamais \
                une autre date.

                Rédige un rapport professionnel et synthétique résumant l'activité \
                de gestion des interventions municipales, à partir des statistiques suivantes :
 
                - Total des signalements : %d
                - En cours de traitement : %d
                - Résolus : %d
                - Critiques (haute gravité, non résolus) : %d
 
                Le rapport doit être clair, structuré, et destiné à un superviseur municipal.
                """.formatted(dateActuelle, stats.total(), stats.enCours(), stats.resolus(), stats.critiques());
    }
}





























































































