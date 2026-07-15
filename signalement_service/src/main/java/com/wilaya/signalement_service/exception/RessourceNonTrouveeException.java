package com.wilaya.signalement_service.exception;

public class RessourceNonTrouveeException extends RuntimeException {
    public RessourceNonTrouveeException(String message) {
        super(message);
    }
}
