package com.wilaya.utilisateur_service.domain.service;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GenerateurCodeTest {

    private final GenerateurCode generateurCode = new GenerateurCode();

    @RepeatedTest(100)
    void genererCodeSixChiffresRenvoieToujoursUneChaineDeSixCaracteres() {
        String code = generateurCode.genererCodeSixChiffres();

        assertThat(code).hasSize(6);
    }

    @RepeatedTest(100)
    void genererCodeSixChiffresRenvoieUniquementDesChiffres() {
        String code = generateurCode.genererCodeSixChiffres();

        assertThat(code).matches("\\d{6}");
    }

    @RepeatedTest(200)
    void genererCodeSixChiffresRenvoieUneValeurDansLaPlageAttendue() {
        String code = generateurCode.genererCodeSixChiffres();
        int valeur = Integer.parseInt(code);

        assertThat(valeur).isBetween(100000, 999999);
    }

    @Test
    void genererCodeSixChiffresNeCommenceJamaisParZero() {

        for (int i = 0; i < 500; i++) {
            String code = generateurCode.genererCodeSixChiffres();
            assertThat(code).doesNotStartWith("0");
        }
    }

    @Test
    void genererCodeSixChiffresProduitDesValeursDifferentesSurPlusieursAppels() {
        Set<String> codesGeneres = new HashSet<>();

        for (int i = 0; i < 50; i++) {
            codesGeneres.add(generateurCode.genererCodeSixChiffres());
        }


        assertThat(codesGeneres.size()).isGreaterThan(1);
    }
}
