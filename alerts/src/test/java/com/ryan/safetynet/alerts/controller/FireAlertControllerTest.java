package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.FireAlertDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.dto.ErrorResponse;
import com.ryan.safetynet.alerts.service.FireAlertService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du controller FireAlertController")
class FireAlertControllerTest {

    @Mock
    private FireAlertService fireAlertService;

    @InjectMocks
    private FireAlertController fireAlertController;

    @Test
    @DisplayName("Test de récupération des résidents avec des résidents présents")
    void testGetResidentsByAddress_WithResidents() {
        // Arrange
        String address = "123 Main St";
        FireAlertDTO mockResponse = new FireAlertDTO();
        mockResponse.setFireStationNumber("1");
        
        List<PersonWithMedicalInfoDTO> residents = Arrays.asList(
            createSamplePersonWithMedicalInfo("John", "Doe", "123-456-7890", 30, List.of("med1"), List.of("allergy1")),
            createSamplePersonWithMedicalInfo("Jane", "Doe", "123-456-7891", 28, List.of("med2"), List.of("allergy2"))
        );
        mockResponse.setResidents(residents);
        
        when(fireAlertService.getPersonsAndFireStationByAddress(address)).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = fireAlertController.getResidentsByAddress(address);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        FireAlertDTO body = (FireAlertDTO) response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals("1", body.getFireStationNumber());
        assertEquals(2, body.getResidents().size());
        assertEquals("John", body.getResidents().get(0).getFirstName());
        assertEquals("Doe", body.getResidents().get(0).getLastName());
        assertEquals("Jane", body.getResidents().get(1).getFirstName());
    }

    @Test
    @DisplayName("Test de récupération des résidents sans résidents présents")
    void testGetResidentsByAddress_NoResidents() {
        // Arrange
        String address = "123 Main St";
        FireAlertDTO mockResponse = new FireAlertDTO();
        mockResponse.setResidents(List.of());
        
        when(fireAlertService.getPersonsAndFireStationByAddress(address)).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = fireAlertController.getResidentsByAddress(address);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Test de récupération des résidents avec une adresse vide")
    void testGetResidentsByAddress_EmptyAddress() {
        // Arrange
        String address = "";
        FireAlertDTO mockResponse = new FireAlertDTO();
        mockResponse.setResidents(List.of());
        
        when(fireAlertService.getPersonsAndFireStationByAddress(address)).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = fireAlertController.getResidentsByAddress(address);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Test de récupération des résidents avec une erreur du service")
    void testGetResidentsByAddress_WithServiceError() {
        // Arrange
        String address = "123 Main St";
        when(fireAlertService.getPersonsAndFireStationByAddress(address))
            .thenThrow(new RuntimeException("Erreur du service"));

        // Act
        ResponseEntity<?> response = fireAlertController.getResidentsByAddress(address);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Test de récupération des résidents avec une station inexistante")
    void testGetResidentsByAddress_StationNotFound() {
        // Arrange
        String address = "123 Main St";
        when(fireAlertService.getPersonsAndFireStationByAddress(address))
            .thenThrow(new ResourceNotFoundException("La station de pompiers n'existe pas"));

        // Act
        ResponseEntity<?> response = fireAlertController.getResidentsByAddress(address);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("La station de pompiers n'existe pas", errorResponse.getMessage());
    }

    private PersonWithMedicalInfoDTO createSamplePersonWithMedicalInfo(String firstName, String lastName, String phone, int age, List<String> medications, List<String> allergies) {
        PersonWithMedicalInfoDTO person = new PersonWithMedicalInfoDTO();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setPhone(phone);
        person.setAge(age);
        person.setMedications(medications);
        person.setAllergies(allergies);
        return person;
    }
} 