package com.wilaya.ressource_service.service;

import com.wilaya.ressource_service.dto.CreerMaterielRequest;
import com.wilaya.ressource_service.exception.RessourceNonTrouveeException;
import com.wilaya.ressource_service.model.Materiel;
import com.wilaya.ressource_service.model.StatutMateriel;
import com.wilaya.ressource_service.repository.MaterielRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterielServiceTest {

    @Mock
    private MaterielRepository materielRepository;

    @InjectMocks
    private MaterielService materielService;

    @Test
    void ajouterMateriel_devrait_sauvegarder_et_retourner_le_materiel() {
        UUID idEquipe = UUID.randomUUID();
        CreerMaterielRequest request = new CreerMaterielRequest("Camion citerne", idEquipe);

        when(materielRepository.save(any(Materiel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Materiel resultat = materielService.ajouterMateriel(request);

        assertThat(resultat.getType()).isEqualTo("Camion citerne");
        assertThat(resultat.getIdEquipeAssociee()).isEqualTo(idEquipe);
        verify(materielRepository, times(1)).save(any(Materiel.class));
    }

    @Test
    void changerStatut_devrait_mettre_a_jour_le_statut_si_materiel_existe() {
        UUID idMateriel = UUID.randomUUID();
        Materiel materielExistant = new Materiel("Pompe", null);

        when(materielRepository.findById(idMateriel)).thenReturn(Optional.of(materielExistant));
        when(materielRepository.save(any(Materiel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Materiel resultat = materielService.changerStatut(idMateriel, StatutMateriel.EN_MAINTENANCE);

        assertThat(resultat.getStatut()).isEqualTo(StatutMateriel.EN_MAINTENANCE);
        verify(materielRepository).save(materielExistant);
    }

    @Test
    void changerStatut_devrait_lever_exception_si_materiel_introuvable() {
        UUID idMateriel = UUID.randomUUID();
        when(materielRepository.findById(idMateriel)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> materielService.changerStatut(idMateriel, StatutMateriel.EN_USAGE))
                .isInstanceOf(RessourceNonTrouveeException.class)
                .hasMessageContaining("Matériel introuvable");

        verify(materielRepository, never()).save(any());
    }
}