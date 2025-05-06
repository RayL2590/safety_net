package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.CommunityEmailDTO;
import com.ryan.safetynet.alerts.service.CommunityEmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du CommunityEmailController")
class CommunityEmailControllerTest {

    @Mock
    private CommunityEmailService communityEmailService;

    @InjectMocks
    private CommunityEmailController communityEmailController;

    private final String TEST_CITY = "Culver";

    @Test
    @DisplayName("GET /communityEmail?city=Culver - Cas nominal avec emails")
    void getEmailsByCity_withEmails_shouldReturnOkWithEmails() {
        // Arrange
        List<String> expectedEmails = List.of("john@email.com", "jane@email.com");
        CommunityEmailDTO mockResponse = new CommunityEmailDTO();
        mockResponse.setEmails(expectedEmails);
        
        when(communityEmailService.getEmailsByCity(TEST_CITY)).thenReturn(mockResponse);

        // Act
        ResponseEntity<CommunityEmailDTO> response = communityEmailController.getEmailsByCity(TEST_CITY);

        // Assert
        CommunityEmailDTO body = response.getBody();
        assertAll("Vérification réponse complète",
            () -> assertNotNull(response, "Réponse ne devrait pas être null"),
            () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
            () -> {
                assertNotNull(body, "Le body de la réponse ne devrait pas être null");
                assertEquals(expectedEmails, body.getEmails());
            }
        );
    }

    @Test
    @DisplayName("GET /communityEmail?city=Paris - Cas sans emails")
    void getEmailsByCity_noEmails_shouldReturnOkWithNullBody() {
        // Arrange
        CommunityEmailDTO emptyResponse = new CommunityEmailDTO();
        emptyResponse.setEmails(Collections.emptyList());
        
        when(communityEmailService.getEmailsByCity(TEST_CITY)).thenReturn(emptyResponse);

        // Act
        ResponseEntity<CommunityEmailDTO> response = communityEmailController.getEmailsByCity(TEST_CITY);

        // Assert
        assertAll("Vérification réponse vide",
            () -> assertNotNull(response, "Réponse ne devrait pas être null"),
            () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
            () -> assertNull(response.getBody())
        );
    }

    @Test
    @DisplayName("GET /communityEmail?city='' - Cas avec ville vide")
    void getEmailsByCity_emptyCity_shouldHandleGracefully() {
        // Arrange
        String emptyCity = "";
        CommunityEmailDTO mockResponse = new CommunityEmailDTO();
        mockResponse.setEmails(List.of());
        
        when(communityEmailService.getEmailsByCity(emptyCity)).thenReturn(mockResponse);

        // Act
        ResponseEntity<CommunityEmailDTO> response = communityEmailController.getEmailsByCity(emptyCity);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /communityEmail?city=Paris - Cas avec erreur interne")
    void getEmailsByCity_serviceError_shouldPropagateException() {
        // Arrange
        when(communityEmailService.getEmailsByCity(TEST_CITY))
            .thenThrow(new RuntimeException("Simulated error"));

        // Act & Assert
        assertThrows(RuntimeException.class,
            () -> communityEmailController.getEmailsByCity(TEST_CITY),
            "L'exception devrait être propagée"
        );
    }

    @Test
    @DisplayName("GET /communityEmail?city=Paris - Vérification gestion des doublons")
    void getEmailsByCity_withDuplicates_shouldReturnAsIs() {
        // Arrange
        List<String> emailsWithDuplicates = List.of("dup@mail.com", "dup@mail.com", "unique@mail.com");
        CommunityEmailDTO mockResponse = new CommunityEmailDTO();
        mockResponse.setEmails(new ArrayList<>(emailsWithDuplicates));
        
        when(communityEmailService.getEmailsByCity(TEST_CITY)).thenReturn(mockResponse);

        // Act
        ResponseEntity<CommunityEmailDTO> response = communityEmailController.getEmailsByCity(TEST_CITY);

        // Assert
        CommunityEmailDTO body = response.getBody();
        assertNotNull(body, "Le body de la réponse ne devrait pas être null");
        assertAll("Vérification doublons",
            () -> assertEquals(emailsWithDuplicates.size(), body.getEmails().size()),
            () -> assertEquals(2, new HashSet<>(body.getEmails()).size())
        );
    }
}
