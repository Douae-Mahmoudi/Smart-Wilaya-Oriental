package com.wilaya.signalement_service.config;

import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.repository.SignalementRepository;
import com.wilaya.signalement_service.util.TfIdfVectorizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TfIdfIndexInitializerTest {

    @Mock
    private SignalementRepository repository;

    @Mock
    private TfIdfVectorizer vectorizer;

    private TfIdfIndexInitializer initializer;

    private void construire() {
        initializer = new TfIdfIndexInitializer(repository, vectorizer);
    }

    private Signalement signalementAvecDescription(String description) {
        Signalement s = mock(Signalement.class);
        when(s.getDescription()).thenReturn(description);
        return s;
    }

    @Test
    void devrait_reconstruire_index_avec_les_descriptions_valides() {
        construire();

        List<Signalement> signalements = List.of(
                signalementAvecDescription("Fuite d'eau importante rue Test"),
                signalementAvecDescription("Panne électrique dans le quartier")
        );
        when(repository.findAll()).thenReturn(signalements);
        when(vectorizer.getTotalDocuments()).thenReturn(2);

        initializer.initializeIndex();

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorizer).rebuildIndex(captor.capture());

        List<String> descriptionsEnvoyees = captor.getValue();
        assertThat(descriptionsEnvoyees).hasSize(2);
        assertThat(descriptionsEnvoyees).allSatisfy(desc -> assertThat(desc).isNotBlank());
    }

    @Test
    void devrait_ignorer_les_descriptions_nulles_et_vides() {
        construire();

        List<Signalement> signalements = List.of(
                signalementAvecDescription("Fuite d'eau importante"),
                signalementAvecDescription(null),
                signalementAvecDescription(""),
                signalementAvecDescription("   ")
        );
        when(repository.findAll()).thenReturn(signalements);
        when(vectorizer.getTotalDocuments()).thenReturn(1);

        initializer.initializeIndex();

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorizer).rebuildIndex(captor.capture());

        assertThat(captor.getValue()).hasSize(1);
    }

    @Test
    void devrait_gerer_une_liste_de_signalements_vide() {
        construire();

        when(repository.findAll()).thenReturn(List.of());
        when(vectorizer.getTotalDocuments()).thenReturn(0);

        initializer.initializeIndex();

        verify(vectorizer).rebuildIndex(List.of());
    }

    @Test
    void devrait_appeler_findAll_une_seule_fois() {
        construire();

        when(repository.findAll()).thenReturn(List.of());
        when(vectorizer.getTotalDocuments()).thenReturn(0);

        initializer.initializeIndex();

        verify(repository, times(1)).findAll();
        verify(vectorizer, times(1)).rebuildIndex(anyList());
    }
}


































