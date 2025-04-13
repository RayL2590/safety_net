package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.FireStationDTO;
import com.ryan.safetynet.alerts.dto.FireStationInputDTO;
import com.ryan.safetynet.alerts.dto.PersonDTO;
import com.ryan.safetynet.alerts.model.FireStation;
import com.ryan.safetynet.alerts.service.FireStationCoverageService;
import com.ryan.safetynet.alerts.service.FireStationService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du controller FireStationController")
class FireStationControllerTest {

    @Mock
    private FireStationCoverageService fireStationCoverageService;

    @Mock
    private FireStationService fireStationService;

    @InjectMocks
    private FireStationController fireStationController;

    @Test
    @DisplayName("Test de récupération des personnes couvertes par une caserne")
    void testGetPersonsCoveredByStation() {
        // Arrange
        int stationNumber = 1;
        FireStationDTO mockResponse = new FireStationDTO();
        List<PersonDTO> persons = Arrays.asList(
            createSamplePerson("John", "Doe", 30),
            createSamplePerson("Jane", "Doe", 10)
        );
        mockResponse.setPersons(persons);
        mockResponse.setAdultCount(1);
        mockResponse.setChildCount(1);
        
        when(fireStationCoverageService.getPersonsCoveredByStation(stationNumber)).thenReturn(mockResponse);

        // Act
        ResponseEntity<FireStationDTO> response = fireStationController.getPersonsCoveredByStation(stationNumber);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getPersons().size());
        assertEquals(1, response.getBody().getAdultCount());
        assertEquals(1, response.getBody().getChildCount());
    }

    @Test
    @DisplayName("Test d'ajout d'une nouvelle caserne")
    void testAddFireStation() throws IOException {
        // Arrange
        FireStationInputDTO inputDTO = new FireStationInputDTO();
        inputDTO.setStation(1);
        inputDTO.setAddress("123 Main St");

        FireStation expectedFireStation = new FireStation();
        expectedFireStation.setStation("1");
        expectedFireStation.setAddress("123 Main St");

        when(fireStationService.existsByAddress("123 Main St")).thenReturn(false);
        when(fireStationService.addFireStation(any(FireStation.class))).thenReturn(expectedFireStation);

        // Act
        ResponseEntity<FireStation> response = fireStationController.addFireStation(inputDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getStation());
        assertEquals("123 Main St", response.getBody().getAddress());
    }

    @Test
    @DisplayName("Test d'ajout d'une caserne avec une adresse existante")
    void testAddFireStation_ExistingAddress() {
        // Arrange
        FireStationInputDTO inputDTO = new FireStationInputDTO();
        inputDTO.setStation(1);
        inputDTO.setAddress("123 Main St");

        when(fireStationService.existsByAddress("123 Main St")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            fireStationController.addFireStation(inputDTO)
        );
    }

    @Test
    @DisplayName("Test de mise à jour d'une caserne existante")
    void testUpdateFireStation() {
        // Arrange
        FireStationInputDTO inputDTO = new FireStationInputDTO();
        inputDTO.setStation(2);
        inputDTO.setAddress("123 Main St");

        FireStation updatedFireStation = new FireStation();
        updatedFireStation.setStation("2");
        updatedFireStation.setAddress("123 Main St");

        when(fireStationService.updateFireStation(any(FireStation.class))).thenReturn(updatedFireStation);

        // Act
        ResponseEntity<FireStation> response = fireStationController.updateFireStation(inputDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("2", response.getBody().getStation());
        assertEquals("123 Main St", response.getBody().getAddress());
    }

    @Test
    @DisplayName("Test de mise à jour d'une caserne inexistante")
    void testUpdateFireStation_NotFound() {
        // Arrange
        FireStationInputDTO inputDTO = new FireStationInputDTO();
        inputDTO.setStation(2);
        inputDTO.setAddress("123 Main St");

        when(fireStationService.updateFireStation(any(FireStation.class))).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
            fireStationController.updateFireStation(inputDTO)
        );
    }

    @Test
    @DisplayName("Test de suppression d'une caserne par adresse")
    void testDeleteFireStation_ByAddress() {
        // Arrange
        String address = "123 Main St";
        when(fireStationService.deleteFireStationByAddress(address)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = fireStationController.deleteFireStation(address, null);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Test de suppression d'une caserne par adresse inexistante")
    void testDeleteFireStation_ByAddress_NotFound() {
        // Arrange
        String address = "123 Main St";
        when(fireStationService.deleteFireStationByAddress(address)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
            fireStationController.deleteFireStation(address, null)
        );
    }

    @Test
    @DisplayName("Test de suppression de casernes par numéro de station")
    void testDeleteFireStation_ByStation() {
        // Arrange
        String station = "1";
        when(fireStationService.deleteFireStationsByStation(station)).thenReturn(2);

        // Act
        ResponseEntity<Void> response = fireStationController.deleteFireStation(null, station);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Test de suppression de casernes par numéro de station inexistant")
    void testDeleteFireStation_ByStation_NotFound() {
        // Arrange
        String station = "1";
        when(fireStationService.deleteFireStationsByStation(station)).thenReturn(0);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
            fireStationController.deleteFireStation(null, station)
        );
    }

    @Test
    @DisplayName("Test de suppression sans paramètres")
    void testDeleteFireStation_NoParameters() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            fireStationController.deleteFireStation(null, null)
        );
    }

    private PersonDTO createSamplePerson(String firstName, String lastName, int age) {
        PersonDTO person = new PersonDTO();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setAge(age);
        person.setPhone("123-456-7890");
        return person;
    }
}
