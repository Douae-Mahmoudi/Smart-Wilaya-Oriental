package com.wilaya.signalement_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RessourceNonTrouveeException.class)
    public ResponseEntity<Map<String, Object>> gererNonTrouve(RessourceNonTrouveeException ex) {
        return construireReponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DoublonSignalementException.class)
    public ResponseEntity<Map<String, Object>> gererDoublon(DoublonSignalementException ex) {
        return construireReponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> gererEtatInvalide(IllegalStateException ex) {
        return construireReponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> gererArgumentInvalide(IllegalArgumentException ex) {
        return construireReponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> gererValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erreurs = new HashMap<>();
        for (FieldError erreur : ex.getBindingResult().getFieldErrors()) {
            erreurs.put(erreur.getField(), erreur.getDefaultMessage());
        }
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("erreurs", erreurs);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> gererJsonIllisible(HttpMessageNotReadableException ex) {
        log.warn("JSON illisible dans la requête : {}", ex.getMessage());
        return construireReponse(HttpStatus.BAD_REQUEST,
                "Le corps de la requête est mal formé ou illisible");
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, Object>> gererPartieManquante(MissingServletRequestPartException ex) {
        log.warn("Partie multipart manquante : {}", ex.getMessage());
        return construireReponse(HttpStatus.BAD_REQUEST,
                "Le champ '" + ex.getRequestPartName() + "' est requis dans le formulaire");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> gererGenerique(Exception ex) {
        log.error("Erreur inattendue non gérée : ", ex);
        return construireReponse(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur inattendue est survenue");
    }

    private ResponseEntity<Map<String, Object>> construireReponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}