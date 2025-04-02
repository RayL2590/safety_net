package com.ryan.safetynet.alerts.dto;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO pour la standardisation des réponses d'erreur de l'API.
 * Ce DTO est utilisé par le GlobalExceptionHandler pour formater
 * toutes les réponses d'erreur de manière cohérente.
 * Il inclut des informations détaillées sur l'erreur, le timestamp,
 * et les détails spécifiques de l'erreur.
 */
@Getter
public class ErrorResponse {
    /** Code HTTP de l'erreur */
    private final int status;

    /** Message d'erreur principal */
    private final String message;

    /** Date et heure de l'erreur */
    private final LocalDateTime timestamp;

    /** Détails supplémentaires de l'erreur (optionnel) */
    private final Map<String, String> details;

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
}