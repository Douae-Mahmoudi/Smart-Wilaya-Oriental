package com.wilaya.utilisateur_service.domain.service;

import java.security.SecureRandom;


public class GenerateurCode {

    private static final SecureRandom RANDOM = new SecureRandom();

    public String genererCodeSixChiffres() {
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }
}
