package com.wilaya.signalement_service.policy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidateurCINTest {

    private ValidateurCIN validateur;

    @BeforeEach
    void setUp() {
        validateur = new ValidateurCIN();
    }

    @ParameterizedTest
    @ValueSource(strings = {"A1234567", "AB12345", "AB1234567", "A 12345"})
    void testValidCIN(String cin) {
        assertTrue(validateur.estValide(cin));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "1234567", "ABC12345", "AB1234", "AB123456789"})
    void testInvalidCIN(String cin) {
        assertFalse(validateur.estValide(cin));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "null"})
    void testNullCIN(String cin) {
        if ("null".equals(cin)) {
            assertFalse(validateur.estValide(null));
        } else {
            assertFalse(validateur.estValide(cin));
        }
    }
}