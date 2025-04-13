package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.FireAlertDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.service.FireAlertService;
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
        ResponseEntity<FireAlertDTO> response = fireAlertController.getResidentsByAddress(address);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getFireStationNumber());
        assertEquals(2, response.getBody().getResidents().size());
        assertEquals("John", response.getBody().getResidents().get(0).getFirstName());
        assertEquals("Doe", response.getBody().getResidents().get(0).getLastName());
        assertEquals("Jane", response.getBody().getResidents().get(1).getFirstName());
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
        ResponseEntity<FireAlertDTO> response = fireAlertController.getResidentsByAddress(address);

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
        ResponseEntity<FireAlertDTO> response = fireAlertController.getResidentsByAddress(address);

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

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
            fireAlertController.getResidentsByAddress(address)
        );
    }

    @Test
    @DisplayName("Test de récupération des résidents avec une caserne inconnue")
    void testGetResidentsByAddress_UnknownStation() {
        // Arrange
        String address = "123 Main St";
        FireAlertDTO mockResponse = new FireAlertDTO();
        mockResponse.setFireStationNumber("Inconnu");
        
        List<PersonWithMedicalInfoDTO> residents = Arrays.asList(
            createSamplePersonWithMedicalInfo("John", "Doe", "123-456-7890", 30, List.of("med1"), List.of("allergy1"))
        );
        mockResponse.setResidents(residents);
        
        when(fireAlertService.getPersonsAndFireStationByAddress(address)).thenReturn(mockResponse);

        // Act
        ResponseEntity<FireAlertDTO> response = fireAlertController.getResidentsByAddress(address);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Inconnu", response.getBody().getFireStationNumber());
        assertEquals(1, response.getBody().getResidents().size());
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