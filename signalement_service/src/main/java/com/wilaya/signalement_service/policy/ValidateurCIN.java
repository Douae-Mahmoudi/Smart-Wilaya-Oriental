package com.wilaya.signalement_service.policy;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;


@Component
public class ValidateurCIN {

    private static final Pattern FORMAT_CIN = Pattern.compile("^[A-Za-z]{1,2}[0-9]{5,7}$");

    public boolean estValide(String cin) {
        if (cin == null) {
            return false;
        }
        String normalise = cin.trim().replace(" ", "");
        return FORMAT_CIN.matcher(normalise).matches();
    }
}
