package com.ryan.safetynet.alerts.exception;

import com.ryan.safetynet.alerts.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions de l'application.
 * Cette classe centralise la gestion des erreurs et assure une réponse
 * cohérente et formatée pour toutes les exceptions non gérées.
 * Elle gère notamment :
 * - Les erreurs de validation des données
 * - Les erreurs de ressources non trouvées
 * - Les erreurs d'entrée/sortie
 * - Les erreurs génériques non spécifiques
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Gère les erreurs de validation des données.
     * Cette méthode est appelée lorsqu'une validation de données échoue,
     * par exemple lors de la validation des DTOs avec les annotations Jakarta Validation.
     *
     * @param ex l'exception de validation
     * @return une réponse HTTP 400 (Bad Request) contenant les détails des erreurs de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Erreur de validation des données", ex);
        // Création d'une map pour stocker les erreurs par champ
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Erreur de validation des données", errors));
    }

    /**
     * Gère les erreurs d'entrée/sortie.
     * Cette méthode est appelée lorsqu'une erreur survient lors des opérations
     * de lecture/écriture des données, par exemple lors de la persistance.
     *
     * @param ex l'exception d'entrée/sortie
     * @return une réponse HTTP 500 (Internal Server Error) avec un message d'erreur
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        logger.error("Erreur d'entrée/sortie", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erreur de persistance des données", null));
    }

    /**
     * Gère les erreurs de ressources non trouvées.
     * Cette méthode est appelée lorsqu'une ressource demandée n'existe pas
     * dans le système, par exemple une personne ou un dossier médical.
     *
     * @param ex l'exception de ressource non trouvée
     * @return une réponse HTTP 404 (Not Found) avec le message d'erreur spécifique
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.warn("Ressource non trouvée: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null));
    }

    /**
     * Gère toutes les autres exceptions non spécifiques.
     * Cette méthode sert de gestionnaire par défaut pour toutes les exceptions
     * qui ne sont pas gérées par les autres méthodes.
     *
     * @param ex l'exception générique
     * @return une réponse HTTP 500 (Internal Server Error) avec un message d'erreur générique
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Erreur inattendue", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Une erreur inattendue s'est produite", null));
    }
}