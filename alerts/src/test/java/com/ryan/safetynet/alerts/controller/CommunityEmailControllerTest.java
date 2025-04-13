package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.CommunityEmailDTO;
import com.ryan.safetynet.alerts.service.CommunityEmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du controller CommunityEmailController")
class CommunityEmailControllerTest {

    @Mock
    private CommunityEmailService communityEmailService;

    @InjectMocks
    private CommunityEmailController communityEmailController;

    @Test
    @DisplayName("Test de récupération des emails avec des emails présents")
    void testGetEmailsByCity_WithEmails() {
        // Arrange
        String city = "Paris";
        CommunityEmailDTO mockResponse = new CommunityEmailDTO();
        List<String> emails = Arrays.asList(
            "john.doe@email.com",
            "jane.doe@email.com",
            "bob.smith@email.com"
        );
        mockResponse.setEmails(emails);
        
        when(communityEmailService.getEmailsByCity(city)).thenReturn(mockResponse);

        // Act
        ResponseEntity<CommunityEmailDTO> response = communityEmailController.getEmailsByCity(city);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getEmails().size());
        assertTrue(response.getBody().getEmails().contains("john.doe@email.com"));
        assertTrue(response.getBody().getEmails().contains("jane.doe@email.com"));
        assertTrue(response.getBody().getEmails().contains("bob.smith@email.com"));
    }

    @Test
    @DisplayName("Test de récupération des emails sans emails présents")
    void testGetEmailsByCity_NoEmails() {
        // Arrange
        String city = "Paris";
        CommunityEmailDTO mockResponse = new CommunityEmailDTO();
        mockResponse.setEmails(List.of());
        
        when(communityEmailService.getEmailsByCity(city)).thenReturn(mockResponse);

        // Act
        ResponseEntity<CommunityEmailDTO> response = communityEmailController.getEmailsByCity(city);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Test de récupération des emails avec une ville vide")
    void testGetEmailsByCity_EmptyCity() {
        // Arrange
        String city = "";
        CommunityEmailDTO mockResponse = new CommunityEmailDTO();
        mockResponse.setEmails(List.of());
        
        when(communityEmailService.getEmailsByCity(city)).thenReturn(mockResponse);

        // Act
        ResponseEntity<CommunityEmailDTO> response = communityEmailController.getEmailsByCity(city);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Test de récupération des emails avec une erreur du service")
    void testGetEmailsByCity_WithServiceError() {
        // Arrange
        String city = "Paris";
        when(communityEmailService.getEmailsByCity(city))
            .thenThrow(new RuntimeException("Erreur du service"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
            communityEmailController.getEmailsByCity(city)
        );
    }

    @Test
    @DisplayName("Test de récupération des emails avec des doublons")
    void testGetEmailsByCity_WithDuplicates() {
        // Arrange
        String city = "Paris";
        CommunityEmailDTO mockResponse = new CommunityEmailDTO();
        List<String> emails = Arrays.asList(
            "john.doe@email.com",
            "john.doe@email.com", // Doublon
            "jane.doe@email.com"
        );
        mockResponse.setEmails(emails);
        
        when(communityEmailService.getEmailsByCity(city)).thenReturn(mockResponse);

        // Act
        ResponseEntity<CommunityEmailDTO> response = communityEmailController.getEmailsByCity(city);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getEmails().size()); // Les doublons sont conservés
    }
} 