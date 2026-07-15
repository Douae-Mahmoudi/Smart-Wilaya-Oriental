package com.wilaya.signalement_service.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class NiveauGraviteTest {

    @ParameterizedTest
    @EnumSource(NiveauGravite.class)
    void testEnumValues(NiveauGravite gravite) {
        assertNotNull(gravite);
        assertNotNull(gravite.name());
    }
}