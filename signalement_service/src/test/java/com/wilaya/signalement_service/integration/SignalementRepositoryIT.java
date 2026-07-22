package com.wilaya.signalement_service.integration;

import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.StatutSignalement;
import com.wilaya.signalement_service.model.TypeIntervention;
import com.wilaya.signalement_service.repository.SignalementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SignalementRepositoryIT {

    @Autowired
    private SignalementRepository repository;

    private Signalement creerSignalement(String cin, TypeIntervention type, String zone) {
        return new Signalement(cin, type, "description test", "photo.jpg", zone, NiveauGravite.MOYENNE,"124 Rue Quods ");
    }

    @Test
    void devrait_trouver_un_signalement_par_numero_suivi() {
        Signalement signalement = repository.save(creerSignalement("AB123456", TypeIntervention.EAU, "Zone Nord"));

        Optional<Signalement> resultat = repository.findByNumeroSuivi(signalement.getNumeroSuivi());

        assertThat(resultat).isPresent();
        assertThat(resultat.get().getCinDeclarant()).isEqualTo("AB123456");
    }

    @Test
    void devrait_retourner_vide_si_numero_suivi_inconnu() {
        Optional<Signalement> resultat = repository.findByNumeroSuivi("SIG-INCONNU");

        assertThat(resultat).isEmpty();
    }

    @Test
    void devrait_trouver_doublon_par_type_et_zone_recent() {
        repository.save(creerSignalement("AB123456", TypeIntervention.EAU, "Zone Nord"));
        LocalDateTime seuil = LocalDateTime.now().minusMinutes(60);

        List<Signalement> resultat = repository.findByTypeAndZoneAndDateCreationAfterAndStatutNotIn(
                TypeIntervention.EAU, "Zone Nord", seuil,
                List.of(StatutSignalement.RESOLU, StatutSignalement.CLOTURE));

        assertThat(resultat).hasSize(1);
    }

    @Test
    void ne_devrait_pas_trouver_doublon_pour_une_autre_zone() {
        repository.save(creerSignalement("AB123456", TypeIntervention.EAU, "Zone Nord"));
        LocalDateTime seuil = LocalDateTime.now().minusMinutes(60);

        List<Signalement> resultat = repository.findByTypeAndZoneAndDateCreationAfterAndStatutNotIn(
                TypeIntervention.EAU, "Zone Sud", seuil,
                List.of(StatutSignalement.RESOLU, StatutSignalement.CLOTURE));

        assertThat(resultat).isEmpty();
    }

    @Test
    void ne_devrait_pas_trouver_doublon_pour_un_autre_type() {
        repository.save(creerSignalement("AB123456", TypeIntervention.EAU, "Zone Nord"));
        LocalDateTime seuil = LocalDateTime.now().minusMinutes(60);

        List<Signalement> resultat = repository.findByTypeAndZoneAndDateCreationAfterAndStatutNotIn(
                TypeIntervention.VOIRIE, "Zone Nord", seuil,
                List.of(StatutSignalement.RESOLU, StatutSignalement.CLOTURE));

        assertThat(resultat).isEmpty();
    }

    @Test
    void devrait_trouver_doublon_par_cin_recent() {
        repository.save(creerSignalement("AB123456", TypeIntervention.EAU, "Zone Nord"));
        LocalDateTime seuil = LocalDateTime.now().minusMinutes(60);

        List<Signalement> resultat = repository.findByCinDeclarantAndDateCreationAfterAndStatutNotIn(
                "AB123456", seuil, List.of(StatutSignalement.RESOLU, StatutSignalement.CLOTURE));

        assertThat(resultat).hasSize(1);
    }

    @Test
    void ne_devrait_pas_trouver_doublon_pour_un_autre_cin() {
        repository.save(creerSignalement("AB123456", TypeIntervention.EAU, "Zone Nord"));
        LocalDateTime seuil = LocalDateTime.now().minusMinutes(60);

        List<Signalement> resultat = repository.findByCinDeclarantAndDateCreationAfterAndStatutNotIn(
                "ZZ999999", seuil, List.of(StatutSignalement.RESOLU, StatutSignalement.CLOTURE));

        assertThat(resultat).isEmpty();
    }

    @Test
    void devrait_lister_tous_les_signalements() {
        repository.save(creerSignalement("AB123456", TypeIntervention.EAU, "Zone Nord"));
        repository.save(creerSignalement("CD654321", TypeIntervention.VOIRIE, "Zone Sud"));

        List<Signalement> resultat = repository.findAll();

        assertThat(resultat).hasSize(2);
    }
}