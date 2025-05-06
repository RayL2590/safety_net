package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.PhoneAlertDTO;
import com.ryan.safetynet.alerts.service.PhoneAlertService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du controller PhoneAlertController")
class PhoneAlertControllerTest {

    @Mock
    private PhoneAlertService phoneAlertService;

    @InjectMocks
    private PhoneAlertController phoneAlertController;

    @Test
    @DisplayName("Test de récupération des numéros de téléphone avec des numéros trouvés")
    void testGetPhoneNumbersByStation_WithNumbersFound() {
        // Arrange
        final int stationNumber = 1;
        final List<String> expectedPhoneNumbers = Arrays.asList("555-1234", "555-5678", "555-9012");
        
        when(phoneAlertService.getPhoneNumbersByStation(stationNumber))
            .thenReturn(expectedPhoneNumbers);

        // Act
        ResponseEntity<PhoneAlertDTO> response = phoneAlertController.getPhoneNumbersByStation(stationNumber);

        // Assert
        // Vérification de la réponse globale
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), 
            "Le statut HTTP doit être 200 (OK)");

        // Vérification du corps de la réponse
        PhoneAlertDTO responseBody = response.getBody();
        assertNotNull(responseBody, "Le corps de la réponse ne doit pas être null");
        
        List<String> actualPhoneNumbers = responseBody.getPhoneNumbers();
        assertNotNull(actualPhoneNumbers, "La liste des numéros ne doit pas être null");
        
        // Vérifications complètes des numéros
        assertAll("Vérification des numéros de téléphone",
            () -> assertEquals(expectedPhoneNumbers.size(), actualPhoneNumbers.size(),
                "Le nombre de numéros de téléphone ne correspond pas"),
            () -> assertTrue(actualPhoneNumbers.containsAll(expectedPhoneNumbers),
                "Tous les numéros attendus doivent être présents"),
            () -> assertEquals(expectedPhoneNumbers, actualPhoneNumbers,
                "La liste complète des numéros doit correspondre")
        );
    }


    @Test
    @DisplayName("Test de récupération des numéros de téléphone sans numéros trouvés")
    void testGetPhoneNumbersByStation_WithNoNumbersFound() {
        // Arrange
        int stationNumber = 1;
        when(phoneAlertService.getPhoneNumbersByStation(stationNumber)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<PhoneAlertDTO> response = phoneAlertController.getPhoneNumbersByStation(stationNumber);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Test de récupération des numéros de téléphone avec des doublons")
    void testGetPhoneNumbersByStation_WithDuplicateNumbers() {
        // Arrange
        int stationNumber = 1;
        List<String> phoneNumbers = Arrays.asList("555-1234", "555-1234", "555-5678");
        
        when(phoneAlertService.getPhoneNumbersByStation(stationNumber)).thenReturn(phoneNumbers);

        // Act
        ResponseEntity<PhoneAlertDTO> response = phoneAlertController.getPhoneNumbersByStation(stationNumber);

        // Assert
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(200, response.getStatusCode().value(), "Le statut HTTP doit être 200");
        
        PhoneAlertDTO body = response.getBody();
        assertNotNull(body, "Le corps de la réponse ne doit pas être null");
        
        assertEquals(3, body.getPhoneNumbers().size(), "La liste doit contenir 3 éléments (doublons inclus)");
        assertTrue(body.getPhoneNumbers().containsAll(phoneNumbers), "Tous les numéros doivent être présents");
    }


    @Test
    @DisplayName("Test de récupération des numéros de téléphone avec un numéro de station négatif")
    void testGetPhoneNumbersByStation_WithNegativeStationNumber() {
        // Arrange
        int stationNumber = -1;
        when(phoneAlertService.getPhoneNumbersByStation(stationNumber)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<PhoneAlertDTO> response = phoneAlertController.getPhoneNumbersByStation(stationNumber);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(phoneAlertService).getPhoneNumbersByStation(stationNumber);
    }

    @Test
    @DisplayName("Test de récupération des numéros de téléphone avec un numéro de station zéro")
    void testGetPhoneNumbersByStation_WithZeroStationNumber() {
        // Arrange
        int stationNumber = 0;
        when(phoneAlertService.getPhoneNumbersByStation(stationNumber)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<PhoneAlertDTO> response = phoneAlertController.getPhoneNumbersByStation(stationNumber);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(phoneAlertService).getPhoneNumbersByStation(stationNumber);
    }
} 