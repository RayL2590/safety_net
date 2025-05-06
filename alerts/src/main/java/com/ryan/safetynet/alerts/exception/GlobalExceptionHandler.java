package com.ryan.safetynet.alerts.exception;

import com.ryan.safetynet.alerts.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

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
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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
        log.error("Erreur de validation des données", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            
            // Personnalisation des messages d'erreur
            if (field.equals("fireStationNumber")) {
                if (message != null && message.contains("chiffres")) {
                    errors.put(field, "Le numéro de la caserne doit être un nombre entre 1 et 9999");
                } else if (message != null && message.contains("obligatoire")) {
                    errors.put(field, "Le numéro de la caserne est requis pour identifier la caserne de pompiers");
                }
            } else if (field.equals("residents")) {
                if (message != null && message.contains("null")) {
                    errors.put(field, "La liste des résidents ne peut pas être vide");
                }
            } else {
                errors.put(field, message);
            }
        });
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
        log.error("Erreur d'entrée/sortie", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Erreur lors de l'accès aux données du système", null));
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
        log.warn("Ressource non trouvée: {}", ex.getMessage());
        Map<String, String> details = new HashMap<>();
        
        // Personnalisation des messages selon le type de ressource
        if (ex.getMessage().contains("caserne")) {
            details.put("fireStation", "La caserne de pompiers spécifiée n'existe pas dans notre système");
        } else if (ex.getMessage().contains("résident")) {
            details.put("resident", "Aucun résident trouvé à cette adresse");
        } else if (ex.getMessage().contains("médical")) {
            details.put("medical", "Aucune information médicale disponible pour ce résident");
        }
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), details));
    }

    /**
     * Gère les erreurs de endpoints non trouvés.
     * Cette méthode est appelée lorsqu'un endpoint demandé n'existe pas.
     *
     * @param ex l'exception de endpoint non trouvé
     * @return une réponse HTTP 404 (Not Found) avec un message d'erreur approprié
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("Endpoint non trouvé: {}", ex.getRequestURL());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 
                    "L'endpoint demandé n'existe pas dans notre système", null));
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
        log.error("Erreur inattendue", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Une erreur inattendue s'est produite lors du traitement de votre demande", null));
    }

    /**
     * Gère les exceptions de type IllegalArgumentException.
     * Cette méthode est appelée lorsqu'une méthode reçoit un argument
     * non valide, par exemple une adresse vide ou une valeur null.
     *
     * @param ex l'exception de type IllegalArgumentException
     * @return une réponse HTTP 400 (Bad Request) avec le message d'erreur spécifique
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

}