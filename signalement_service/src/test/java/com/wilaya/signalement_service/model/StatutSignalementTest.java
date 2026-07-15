package com.wilaya.signalement_service.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class StatutSignalementTest {

    @ParameterizedTest
    @EnumSource(StatutSignalement.class)
    void testEnumValues(StatutSignalement statut) {
        assertNotNull(statut);
        assertNotNull(statut.name());
    }
}