package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.FireStation;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FireStationServiceTest {

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private FireStationService fireStationService;
    
    private List<FireStation> fireStationList;
    private Data mockData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Création d'une liste de casernes pour les tests
        fireStationList = new ArrayList<>();
        
        // Ajout de casernes existantes pour les tests
        FireStation station1 = new FireStation();
        station1.setAddress("1509 Culver St");
        station1.setStation("3");
        fireStationList.add(station1);
        
        FireStation station2 = new FireStation();
        station2.setAddress("29 15th St");
        station2.setStation("2");
        fireStationList.add(station2);
        
        FireStation station3 = new FireStation();
        station3.setAddress("834 Binoc Ave");
        station3.setStation("3");
        fireStationList.add(station3);
        
        // Configuration du mock dataRepository
        mockData = new Data();
        mockData.setFireStations(fireStationList);
        
        when(dataRepository.getData()).thenReturn(mockData);
    }

    @Test
    void testGetAllFireStations() {
        // Exécution
        List<FireStation> result = fireStationService.getAllFireStations();
        
        // Vérification
        assertNotNull(result);
        assertEquals(3, result.size());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testFindFireStationByAddress_StationExists() {
        // Exécution
        Optional<FireStation> result = fireStationService.findFireStationByAddress("1509 Culver St");
        
        // Vérification
        assertTrue(result.isPresent());
        assertEquals("1509 Culver St", result.get().getAddress());
        assertEquals("3", result.get().getStation());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testFindFireStationByAddress_StationDoesNotExist() {
        // Exécution
        Optional<FireStation> result = fireStationService.findFireStationByAddress("123 Unknown St");
        
        // Vérification
        assertFalse(result.isPresent());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testFindFireStationsByStation_StationsExist() {
        // Exécution
        List<FireStation> result = fireStationService.findFireStationsByStation("3");
        
        // Vérification
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1509 Culver St", result.get(0).getAddress());
        assertEquals("834 Binoc Ave", result.get(1).getAddress());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testFindFireStationsByStation_NoStationsExist() {
        // Exécution
        List<FireStation> result = fireStationService.findFireStationsByStation("5");
        
        // Vérification
        assertNotNull(result);
        assertEquals(0, result.size());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testAddFireStation() {
        // Préparation
        FireStation newStation = new FireStation();
        newStation.setAddress("951 LoneTree Rd");
        newStation.setStation("2");
        
        // Exécution
        FireStation addedStation = fireStationService.addFireStation(newStation);
        
        // Vérification
        assertNotNull(addedStation);
        assertEquals("951 LoneTree Rd", addedStation.getAddress());
        assertEquals("2", addedStation.getStation());
        
        // Vérifier que la station a bien été ajoutée à la liste
        verify(dataRepository, times(1)).getData();
        assertTrue(fireStationList.contains(newStation));
        assertEquals(4, fireStationList.size());
    }
    
    @Test
    void testUpdateFireStation_StationExists() {
        // Exécution
        FireStation result = fireStationService.updateFireStation("1509 Culver St", "4");
        
        // Vérification
        assertNotNull(result);
        assertEquals("1509 Culver St", result.getAddress());
        assertEquals("4", result.getStation()); // Le numéro de station a été mis à jour
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testUpdateFireStation_StationDoesNotExist() {
        // Exécution
        FireStation result = fireStationService.updateFireStation("123 Unknown St", "5");
        
        // Vérification
        assertNull(result);
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testDeleteFireStationByAddress_StationExists() {
        // Exécution
        boolean result = fireStationService.deleteFireStationByAddress("1509 Culver St");
        
        // Vérification
        assertTrue(result);
        assertEquals(2, fireStationList.size()); // La liste devrait avoir une station en moins
        
        // Vérifier que la station a bien été supprimée
        Optional<FireStation> deletedStation = fireStationList.stream()
                .filter(f -> f.getAddress().equals("1509 Culver St"))
                .findFirst();
        assertFalse(deletedStation.isPresent());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testDeleteFireStationByAddress_StationDoesNotExist() {
        // Exécution
        boolean result = fireStationService.deleteFireStationByAddress("123 Unknown St");
        
        // Vérification
        assertFalse(result);
        assertEquals(3, fireStationList.size()); // La liste ne devrait pas changer
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testDeleteFireStationsByStation_StationsExist() {
        // Exécution
        int result = fireStationService.deleteFireStationsByStation("3");
        
        // Vérification
        assertEquals(2, result); // 2 stations devraient être supprimées
        assertEquals(1, fireStationList.size()); // La liste devrait avoir 2 stations en moins
        
        // Vérifier que les stations ont bien été supprimées
        List<FireStation> remainingStations = fireStationList.stream()
                .filter(f -> f.getStation().equals("3"))
                .toList();
        assertEquals(0, remainingStations.size());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testDeleteFireStationsByStation_NoStationsExist() {
        // Exécution
        int result = fireStationService.deleteFireStationsByStation("5");
        
        // Vérification
        assertEquals(0, result); // Aucune station ne devrait être supprimée
        assertEquals(3, fireStationList.size()); // La liste ne devrait pas changer
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
}
