package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MedicalRecordServiceTest {

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;
    
    private List<MedicalRecord> medicalRecordList;
    private Data mockData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Création d'une liste de dossiers médicaux pour les tests
        medicalRecordList = new ArrayList<>();
        
        // Ajout d'un dossier médical existant pour les tests
        MedicalRecord existingRecord = new MedicalRecord();
        existingRecord.setFirstName("Harry");
        existingRecord.setLastName("Potter");
        existingRecord.setBirthdate(LocalDate.of(1980, 7, 31));
        existingRecord.setMedications(Arrays.asList("Skele-Gro", "Pepperup Potion"));
        existingRecord.setAllergies(Arrays.asList("Dementors"));
        medicalRecordList.add(existingRecord);
        
        // Configuration du mock dataRepository
        mockData = new Data();
        mockData.setMedicalRecords(medicalRecordList);
        
        when(dataRepository.getData()).thenReturn(mockData);
    }

    @Test
    void testAddMedicalRecord() {
        // Préparation
        MedicalRecord newRecord = new MedicalRecord();
        newRecord.setFirstName("Hermione");
        newRecord.setLastName("Granger");
        newRecord.setBirthdate(LocalDate.of(1979, 9, 19));
        newRecord.setMedications(Arrays.asList("Calming Draught"));
        newRecord.setAllergies(new ArrayList<>());
        
        // Exécution
        MedicalRecord addedRecord = medicalRecordService.addMedicalRecord(newRecord);
        
        // Vérification
        assertNotNull(addedRecord);
        assertEquals("Hermione", addedRecord.getFirstName());
        assertEquals("Granger", addedRecord.getLastName());
        
        // Vérifier que le dossier a bien été ajouté à la liste
        verify(dataRepository, times(1)).getData();
        assertTrue(medicalRecordList.contains(newRecord));
    }
    
    @Test
    void testGetAllMedicalRecords() {
        // Exécution
        List<MedicalRecord> result = medicalRecordService.getAllMedicalRecords();
        
        // Vérification
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Harry", result.get(0).getFirstName());
        assertEquals("Potter", result.get(0).getLastName());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testFindMedicalRecordByName_RecordExists() {
        // Exécution
        Optional<MedicalRecord> result = medicalRecordService.findMedicalRecordByName("Harry", "Potter");
        
        // Vérification
        assertTrue(result.isPresent());
        assertEquals("Harry", result.get().getFirstName());
        assertEquals("Potter", result.get().getLastName());
        assertEquals(LocalDate.of(1980, 7, 31), result.get().getBirthdate());
        assertEquals(2, result.get().getMedications().size());
        assertEquals(1, result.get().getAllergies().size());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testFindMedicalRecordByName_RecordDoesNotExist() {
        // Exécution
        Optional<MedicalRecord> result = medicalRecordService.findMedicalRecordByName("Ron", "Weasley");
        
        // Vérification
        assertFalse(result.isPresent());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testUpdateMedicalRecord_RecordExists() {
        // Préparation
        MedicalRecord updatedInfo = new MedicalRecord();
        updatedInfo.setBirthdate(LocalDate.of(1980, 7, 31)); // Même date de naissance
        updatedInfo.setMedications(Arrays.asList("Felix Felicis", "Polyjuice Potion")); // Nouveaux médicaments
        updatedInfo.setAllergies(Arrays.asList("Dementors", "Basilisk Venom")); // Nouvelles allergies
        
        // Exécution
        MedicalRecord result = medicalRecordService.updateMedicalRecord("Harry", "Potter", updatedInfo);
        
        // Vérification
        assertNotNull(result);
        assertEquals("Harry", result.getFirstName()); // Le prénom ne change pas
        assertEquals("Potter", result.getLastName()); // Le nom ne change pas
        assertEquals(LocalDate.of(1980, 7, 31), result.getBirthdate());
        assertEquals(2, result.getMedications().size());
        assertTrue(result.getMedications().contains("Felix Felicis"));
        assertTrue(result.getMedications().contains("Polyjuice Potion"));
        assertEquals(2, result.getAllergies().size());
        assertTrue(result.getAllergies().contains("Dementors"));
        assertTrue(result.getAllergies().contains("Basilisk Venom"));
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testUpdateMedicalRecord_RecordDoesNotExist() {
        // Préparation
        MedicalRecord updatedInfo = new MedicalRecord();
        updatedInfo.setBirthdate(LocalDate.of(1980, 3, 1));
        updatedInfo.setMedications(Arrays.asList("Wit-Sharpening Potion"));
        updatedInfo.setAllergies(Arrays.asList("Spiders"));
        
        // Exécution
        MedicalRecord result = medicalRecordService.updateMedicalRecord("Ron", "Weasley", updatedInfo);
        
        // Vérification
        assertNull(result);
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testDeleteMedicalRecord_RecordExists() {
        // Exécution
        boolean result = medicalRecordService.deleteMedicalRecord("Harry", "Potter");
        
        // Vérification
        assertTrue(result);
        assertEquals(0, medicalRecordList.size()); // La liste devrait être vide après suppression
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testDeleteMedicalRecord_RecordDoesNotExist() {
        // Exécution
        boolean result = medicalRecordService.deleteMedicalRecord("Ron", "Weasley");
        
        // Vérification
        assertFalse(result);
        assertEquals(1, medicalRecordList.size()); // La liste ne devrait pas changer
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
}
