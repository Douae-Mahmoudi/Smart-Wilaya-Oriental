package com.wilaya.signalement_service.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TypeInterventionTest {

    @ParameterizedTest
    @EnumSource(TypeIntervention.class)
    void testEnumValues(TypeIntervention type) {
        assertNotNull(type);
        assertNotNull(type.name());
    }
}