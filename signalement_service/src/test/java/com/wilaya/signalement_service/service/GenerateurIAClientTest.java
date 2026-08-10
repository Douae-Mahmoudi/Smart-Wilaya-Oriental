package com.wilaya.signalement_service.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GenerateurIAClientTest {

    @Test
    void devrait_declarer_une_seule_methode_generer_qui_prend_un_prompt_et_retourne_du_texte() throws NoSuchMethodException {
        Method methode = GenerateurIAClient.class.getMethod("generer", String.class);

        assertThat(methode.getReturnType()).isEqualTo(String.class);
        assertThat(GenerateurIAClient.class.getMethods()).hasSize(1);
    }

    @Test
    void geminiClient_devrait_implementer_le_contrat() {
        assertThat(GenerateurIAClient.class).isAssignableFrom(GeminiClient.class);
    }
}








































