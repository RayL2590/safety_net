package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.FireStation;
import com.ryan.safetynet.alerts.repository.DataRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service FireStationService")
class FireStationServiceTest {

    @Mock
    private DataRepository dataRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private FireStationService fireStationService;

    private Data mockData;
    private List<FireStation> mockFireStations;

    @BeforeEach
    void setUp() {
        mockData = new Data();
        mockFireStations = new ArrayList<>();
        mockData.setFireStations(mockFireStations);
    }

    @Test
    @DisplayName("Test de récupération des adresses couvertes par une liste de stations")
    void testGetAddressesCoveredByStations() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        List<Integer> stationNumbers = Arrays.asList(1, 2);
        FireStation station1 = new FireStation();
        station1.setStation("1");
        station1.setAddress("123 Main St");
        FireStation station2 = new FireStation();
        station2.setStation("2");
        station2.setAddress("456 Oak St");
        FireStation station3 = new FireStation();
        station3.setStation("3");
        station3.setAddress("789 Pine St");
        mockFireStations.addAll(Arrays.asList(station1, station2, station3));

        // Act
        List<String> addresses = fireStationService.getAddressesCoveredByStations(stationNumbers);

        // Assert
        assertEquals(2, addresses.size());
        assertTrue(addresses.contains("123 Main St"));
        assertTrue(addresses.contains("456 Oak St"));
        assertFalse(addresses.contains("789 Pine St"));
    }

    @Test
    @DisplayName("Test de récupération des adresses couvertes par une station")
    void testGetAddressesCoveredByStation() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        Integer stationNumber = 1;
        FireStation station1 = new FireStation();
        station1.setStation("1");
        station1.setAddress("123 Main St");
        FireStation station2 = new FireStation();
        station2.setStation("1");
        station2.setAddress("456 Oak St");
        FireStation station3 = new FireStation();
        station3.setStation("2");
        station3.setAddress("789 Pine St");
        mockFireStations.addAll(Arrays.asList(station1, station2, station3));

        // Act
        List<String> addresses = fireStationService.getAddressesCoveredByStation(stationNumber);

        // Assert
        assertEquals(2, addresses.size());
        assertTrue(addresses.contains("123 Main St"));
        assertTrue(addresses.contains("456 Oak St"));
        assertFalse(addresses.contains("789 Pine St"));
    }

    @Test
    @DisplayName("Test de recherche d'une station par adresse existante")
    void testFindFireStationByAddress_Existing() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String address = "123 Main St";
        FireStation expectedStation = new FireStation();
        expectedStation.setStation("1");
        expectedStation.setAddress(address);
        mockFireStations.add(expectedStation);

        // Act
        Optional<FireStation> result = fireStationService.findFireStationByAddress(address);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedStation, result.get());
    }

    @Test
    @DisplayName("Test de recherche d'une station par adresse inexistante")
    void testFindFireStationByAddress_NonExisting() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String address = "123 Main St";
        FireStation station = new FireStation();
        station.setStation("1");
        station.setAddress("456 Oak St");
        mockFireStations.add(station);

        // Act
        Optional<FireStation> result = fireStationService.findFireStationByAddress(address);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test d'ajout d'une station valide")
    void testAddFireStation_Valid() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        FireStation newStation = new FireStation();
        newStation.setStation("1");
        newStation.setAddress("123 Main St");
        when(validator.validate(any(FireStation.class))).thenReturn(new HashSet<>());

        // Act
        FireStation result = fireStationService.addFireStation(newStation);

        // Assert
        assertEquals(newStation, result);
        assertTrue(mockFireStations.contains(newStation));
        verify(dataRepository).saveData();
    }

    @Test
    @DisplayName("Test d'ajout d'une station invalide")
    void testAddFireStation_Invalid() {
        // Arrange
        FireStation invalidStation = new FireStation();
        Set<ConstraintViolation<FireStation>> violations = new HashSet<>();
        // Créer une violation fictive avec le type générique explicite
        @SuppressWarnings("unchecked")
        ConstraintViolation<FireStation> violation = mock(ConstraintViolation.class);
        violations.add(violation);
        when(validator.validate(any(FireStation.class))).thenReturn(violations);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> 
            fireStationService.addFireStation(invalidStation)
        );
    }

    @Test
    @DisplayName("Test de mise à jour d'une station existante")
    void testUpdateFireStation_Existing() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        FireStation existingStation = new FireStation();
        existingStation.setStation("1");
        existingStation.setAddress("123 Main St");
        mockFireStations.add(existingStation);

        FireStation updatedStation = new FireStation();
        updatedStation.setStation("2");
        updatedStation.setAddress("123 Main St");
        when(validator.validate(any(FireStation.class))).thenReturn(new HashSet<>());

        // Act
        FireStation result = fireStationService.updateFireStation(updatedStation);

        // Assert
        assertNotNull(result);
        assertEquals("2", result.getStation());
        assertEquals("123 Main St", result.getAddress());
    }

    @Test
    @DisplayName("Test de mise à jour d'une station inexistante")
    void testUpdateFireStation_NonExisting() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        FireStation nonExistingStation = new FireStation();
        nonExistingStation.setStation("1");
        nonExistingStation.setAddress("123 Main St");
        when(validator.validate(any(FireStation.class))).thenReturn(new HashSet<>());

        // Act
        FireStation result = fireStationService.updateFireStation(nonExistingStation);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Test de suppression d'une station par adresse existante")
    void testDeleteFireStationByAddress_Existing() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String address = "123 Main St";
        FireStation station = new FireStation();
        station.setStation("1");
        station.setAddress(address);
        mockFireStations.add(station);

        // Act
        boolean result = fireStationService.deleteFireStationByAddress(address);

        // Assert
        assertTrue(result);
        assertTrue(mockFireStations.isEmpty());
    }

    @Test
    @DisplayName("Test de suppression d'une station par adresse inexistante")
    void testDeleteFireStationByAddress_NonExisting() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String address = "123 Main St";
        FireStation station = new FireStation();
        station.setStation("1");
        station.setAddress("456 Oak St");
        mockFireStations.add(station);

        // Act
        boolean result = fireStationService.deleteFireStationByAddress(address);

        // Assert
        assertFalse(result);
        assertEquals(1, mockFireStations.size());
    }

    @Test
    @DisplayName("Test de suppression des stations par numéro")
    void testDeleteFireStationsByStation() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String stationNumber = "1";
        FireStation station1 = new FireStation();
        station1.setStation("1");
        station1.setAddress("123 Main St");
        FireStation station2 = new FireStation();
        station2.setStation("1");
        station2.setAddress("456 Oak St");
        FireStation station3 = new FireStation();
        station3.setStation("2");
        station3.setAddress("789 Pine St");
        mockFireStations.addAll(Arrays.asList(station1, station2, station3));

        // Act
        int deletedCount = fireStationService.deleteFireStationsByStation(stationNumber);

        // Assert
        assertEquals(2, deletedCount);
        assertEquals(1, mockFireStations.size());
        assertEquals("2", mockFireStations.get(0).getStation());
    }

    @Test
    @DisplayName("Test de vérification d'existence par adresse")
    void testExistsByAddress() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String address = "123 Main St";
        FireStation station = new FireStation();
        station.setStation("1");
        station.setAddress(address);
        mockFireStations.add(station);

        // Act & Assert
        assertTrue(fireStationService.existsByAddress(address));
        assertFalse(fireStationService.existsByAddress("456 Oak St"));
    }

    @Test
    @DisplayName("Test de vérification d'existence par numéro de station")
    void testExistsByStationNumber() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String stationNumber = "1";
        FireStation station = new FireStation();
        station.setStation(stationNumber);
        station.setAddress("123 Main St");
        mockFireStations.add(station);

        // Act & Assert
        assertTrue(fireStationService.existsByStationNumber(stationNumber));
        assertFalse(fireStationService.existsByStationNumber("2"));
    }
}
