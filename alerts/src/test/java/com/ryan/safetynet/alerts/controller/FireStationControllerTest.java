package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.FireStationDTO;
import com.ryan.safetynet.alerts.dto.FireStationInputDTO;
import com.ryan.safetynet.alerts.dto.PersonDTO;
import com.ryan.safetynet.alerts.dto.ErrorResponse;
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
import java.util.ArrayList;
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
        final int stationNumber = 1;
        final List<PersonDTO> expectedPersons = List.of(
            createSamplePerson("John", "Doe", 30),
            createSamplePerson("Jane", "Doe", 10)
        );
        
        FireStationDTO mockResponse = new FireStationDTO();
        mockResponse.setPersons(new ArrayList<>(expectedPersons));
        mockResponse.setAdultCount(1);
        mockResponse.setChildCount(1);
        
        when(fireStationCoverageService.getPersonsCoveredByStation(stationNumber))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = fireStationController.getPersonsCoveredByStation(stationNumber);

        // Assert
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Statut HTTP doit être 200");
        
        FireStationDTO responseBody = (FireStationDTO) response.getBody();
        assertNotNull(responseBody, "Le body de la réponse ne doit pas être null");
        
        List<PersonDTO> actualPersons = responseBody.getPersons();
        assertNotNull(actualPersons, "La liste des personnes ne doit pas être null");
        
        assertAll("Vérification complète de la réponse",
            () -> assertEquals(expectedPersons.size(), actualPersons.size(),
                "Le nombre de personnes ne correspond pas"),
            () -> assertEquals(1, responseBody.getAdultCount(),
                "Le compte d'adultes doit être 1"),
            () -> assertEquals(1, responseBody.getChildCount(),
                "Le compte d'enfants doit être 1"),
            () -> assertTrue(actualPersons.stream()
                .anyMatch(p -> "John".equals(p.getFirstName()) && "Doe".equals(p.getLastName())),
                "John Doe doit être présent"),
            () -> assertTrue(actualPersons.stream()
                .anyMatch(p -> "Jane".equals(p.getFirstName()) && "Doe".equals(p.getLastName())),
                "Jane Doe doit être présente")
        );
    }

    @Test
    @DisplayName("Test de récupération des personnes avec une station inexistante")
    void testGetPersonsCoveredByStation_NotFound() {
        // Arrange
        final int stationNumber = 999;
        when(fireStationCoverageService.getPersonsCoveredByStation(stationNumber))
            .thenThrow(new ResourceNotFoundException("La station de pompiers " + stationNumber + " n'existe pas"));

        // Act
        ResponseEntity<?> response = fireStationController.getPersonsCoveredByStation(stationNumber);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("La station de pompiers " + stationNumber + " n'existe pas", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Test de récupération des personnes avec une station sans personnes")
    void testGetPersonsCoveredByStation_NoPersons() {
        // Arrange
        final int stationNumber = 1;
        FireStationDTO mockResponse = new FireStationDTO();
        mockResponse.setPersons(new ArrayList<>());
        mockResponse.setAdultCount(0);
        mockResponse.setChildCount(0);
        
        when(fireStationCoverageService.getPersonsCoveredByStation(stationNumber))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = fireStationController.getPersonsCoveredByStation(stationNumber);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
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
        ResponseEntity<?> response = fireStationController.addFireStation(inputDTO);

        // Assert
        FireStation createdStation = (FireStation) response.getBody();
        assertNotNull(createdStation, "La caserne créée ne doit pas être null");

        assertAll("Properties check",
            () -> assertEquals("1", createdStation.getStation()),
            () -> assertEquals("123 Main St", createdStation.getAddress())
        );
    }

    @Test
    @DisplayName("Test d'ajout d'une caserne avec une adresse existante")
    void testAddFireStation_ExistingAddress() {
        // Arrange
        FireStationInputDTO inputDTO = new FireStationInputDTO();
        inputDTO.setStation(1);
        inputDTO.setAddress("123 Main St");

        when(fireStationService.existsByAddress("123 Main St")).thenReturn(true);

        // Act
        ResponseEntity<?> response = fireStationController.addFireStation(inputDTO);

        // Assert
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Le statut doit être CONFLICT");
        
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse, "Le corps de la réponse ne doit pas être null");
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getStatus());
        assertEquals("Un mapping existe déjà pour l'adresse : 123 Main St", errorResponse.getMessage());
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
        
        FireStation result = response.getBody();
        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals("2", result.getStation());
        assertEquals("123 Main St", result.getAddress());
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
