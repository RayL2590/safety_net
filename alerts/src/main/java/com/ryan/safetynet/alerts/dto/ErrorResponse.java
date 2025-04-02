package com.ryan.safetynet.alerts.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO pour la standardisation des réponses d'erreur de l'API.
 * Ce DTO est utilisé par le GlobalExceptionHandler pour formater
 * toutes les réponses d'erreur de manière cohérente.
 * Il inclut des informations détaillées sur l'erreur, le timestamp,
 * et les détails spécifiques de l'erreur.
 */
public class ErrorResponse {
    /** Code HTTP de l'erreur */
    private int status;

    /** Message d'erreur principal */
    private String message;

    /** Date et heure de l'erreur */
    private LocalDateTime timestamp;

    /** Détails supplémentaires de l'erreur (optionnel) */
    private Map<String, String> details;

    /**
     * Constructeur avec tous les champs.
     * @param status Code HTTP de l'erreur
     * @param message Message d'erreur principal
     * @param details Détails supplémentaires de l'erreur
     */
    public ErrorResponse(int status, String message, Map<String, String> details) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.details = details;
    }

    // Getters et Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }
}