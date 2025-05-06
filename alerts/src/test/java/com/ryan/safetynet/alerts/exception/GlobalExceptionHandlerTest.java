package com.ryan.safetynet.alerts.exception;

import com.ryan.safetynet.alerts.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Test de gestion des erreurs de validation pour le numéro de caserne")
    void handleValidationExceptions_FireStationNumber() throws NoSuchMethodException {
        // Given
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "object");
        bindingResult.addError(new FieldError("object", "fireStationNumber", "Le numéro de la caserne doit contenir entre 1 et 4 chiffres"));
        
        Method method = TestController.class.getMethod("testMethod", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals("Erreur de validation des données", body.getMessage());
        assertNotNull(body.getDetails());
        assertEquals("Le numéro de la caserne doit être un nombre entre 1 et 9999", 
            body.getDetails().get("fireStationNumber"));
    }

    @Test
    @DisplayName("Test de gestion des erreurs de validation pour la liste des résidents")
    void handleValidationExceptions_Residents() throws NoSuchMethodException {
        // Given
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "object");
        bindingResult.addError(new FieldError("object", "residents", "La liste des résidents ne peut pas être null"));
        
        Method method = TestController.class.getMethod("testMethod", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals("Erreur de validation des données", body.getMessage());
        assertNotNull(body.getDetails());
        assertEquals("La liste des résidents ne peut pas être vide", 
            body.getDetails().get("residents"));
    }

    @Test
    @DisplayName("Test de gestion des erreurs d'entrée/sortie")
    void handleIOException() {
        // Given
        IOException ex = new IOException("Test IO Exception");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIOException(ex);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getStatus());
        assertEquals("Erreur lors de l'accès aux données du système", body.getMessage());
        assertNull(body.getDetails());
    }

    @Test
    @DisplayName("Test de gestion des casernes non trouvées")
    void handleResourceNotFoundException_FireStation() {
        // Given
        ResourceNotFoundException ex = ResourceNotFoundException.fireStationNotFound("12345");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
        assertEquals("La caserne de pompiers numéro 12345 n'existe pas", body.getMessage());
        assertNotNull(body.getDetails());
        assertEquals("La caserne de pompiers spécifiée n'existe pas dans notre système", 
            body.getDetails().get("fireStation"));
    }

    @Test
    @DisplayName("Test de gestion des résidents non trouvés")
    void handleResourceNotFoundException_Residents() {
        // Given
        ResourceNotFoundException ex = ResourceNotFoundException.noResidentsAtAddress("123 Main St");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
        assertEquals("Aucun résident trouvé à l'adresse 123 Main St", body.getMessage());
        assertNotNull(body.getDetails());
        assertEquals("Aucun résident trouvé à cette adresse", 
            body.getDetails().get("resident"));
    }

    @Test
    @DisplayName("Test de gestion des informations médicales manquantes")
    void handleResourceNotFoundException_MedicalInfo() {
        // Given
        ResourceNotFoundException ex = ResourceNotFoundException.noMedicalInfo("John", "Doe");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
        assertEquals("Aucune information médicale disponible pour John Doe", body.getMessage());
        assertNotNull(body.getDetails());
        assertEquals("Aucune information médicale disponible pour ce résident", 
            body.getDetails().get("medical"));
    }

    @Test
    @DisplayName("Test de gestion des endpoints non trouvés")
    void handleNoHandlerFoundException() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/invalid");
        HttpHeaders headers = new HttpHeaders();  // Création des headers explicitement
        NoHandlerFoundException ex = new NoHandlerFoundException(
            "GET", 
            "/api/invalid", 
            headers  // On passe les headers au lieu de null
        );

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNoHandlerFoundException(ex);

        // Then
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
            "Le statut doit être NOT_FOUND (404)");
        
        ErrorResponse body = response.getBody();
        assertNotNull(body, "Le corps de la réponse ne doit pas être null");
        
        assertAll("Vérification de la réponse d'erreur",
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus(),
                "Le code de statut dans le corps doit correspondre"),
            () -> assertEquals("L'endpoint demandé n'existe pas dans notre système", body.getMessage(),
                "Le message d'erreur doit être explicite"),
            () -> assertNull(body.getDetails(),
                "Les détails supplémentaires doivent être null pour cette erreur")
        );
    }

    @Test
    @DisplayName("Test de gestion des exceptions génériques")
    void handleGenericException() {
        // Given
        Exception ex = new RuntimeException("Test exception");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getStatus());
        assertEquals("Une erreur inattendue s'est produite lors du traitement de votre demande", 
            body.getMessage());
        assertNull(body.getDetails());
    }

    // Classe de test pour simuler un contrôleur
    private static class TestController {
        @SuppressWarnings("unused")
        public void testMethod(String param) {
            // Méthode de test
        }
    }
} 