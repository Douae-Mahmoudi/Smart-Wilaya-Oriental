package com.wilaya.signalement_service.exception;

public class DoublonSignalementException extends RuntimeException {
    public DoublonSignalementException(String message) {
        super(message);
    }
}
