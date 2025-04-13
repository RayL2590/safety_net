package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.MedicalRecord;
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
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service MedicalRecordService")
class MedicalRecordServiceTest {

    @Mock
    private DataRepository dataRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private Data mockData;
    private List<MedicalRecord> mockMedicalRecords;

    @BeforeEach
    void setUp() {
        mockData = new Data();
        mockMedicalRecords = new ArrayList<>();
        mockData.setMedicalRecords(mockMedicalRecords);
    }

    @Test
    @DisplayName("Test de recherche d'un dossier médical existant")
    void testFindMedicalRecordByName_Existing() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";
        MedicalRecord record = new MedicalRecord();
        record.setFirstName(firstName);
        record.setLastName(lastName);
        mockMedicalRecords.add(record);

        // Act
        Optional<MedicalRecord> result = medicalRecordService.findMedicalRecordByName(firstName, lastName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(record, result.get());
    }

    @Test
    @DisplayName("Test de recherche d'un dossier médical inexistant")
    void testFindMedicalRecordByName_NonExisting() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";

        // Act
        Optional<MedicalRecord> result = medicalRecordService.findMedicalRecordByName(firstName, lastName);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test d'ajout d'un dossier médical valide")
    void testAddMedicalRecord_Valid() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        MedicalRecord record = new MedicalRecord();
        record.setFirstName("John");
        record.setLastName("Doe");
        record.setBirthdate(LocalDate.of(1990, 1, 1));
        record.setMedications(List.of("med1"));
        record.setAllergies(List.of("allergy1"));

        when(validator.validate(record)).thenReturn(Collections.emptySet());

        // Act
        MedicalRecord result = medicalRecordService.addMedicalRecord(record);

        // Assert
        assertEquals(record, result);
        assertTrue(mockMedicalRecords.contains(record));
        verify(dataRepository).saveData();
    }

    @Test
    @DisplayName("Test d'ajout d'un dossier médical invalide")
    @SuppressWarnings("unchecked")
    void testAddMedicalRecord_Invalid() {
        // Arrange
        MedicalRecord record = new MedicalRecord();
        Set<ConstraintViolation<MedicalRecord>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));
        when(validator.validate(record)).thenReturn(violations);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () ->
            medicalRecordService.addMedicalRecord(record)
        );
    }

    @Test
    @DisplayName("Test de mise à jour d'un dossier médical existant")
    void testUpdateMedicalRecord_Existing() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";
        MedicalRecord existingRecord = new MedicalRecord();
        existingRecord.setFirstName(firstName);
        existingRecord.setLastName(lastName);
        existingRecord.setBirthdate(LocalDate.of(1990, 1, 1));
        mockMedicalRecords.add(existingRecord);

        MedicalRecord updatedRecord = new MedicalRecord();
        updatedRecord.setFirstName(firstName);
        updatedRecord.setLastName(lastName);
        updatedRecord.setBirthdate(LocalDate.of(1991, 1, 1));
        updatedRecord.setMedications(List.of("med1"));
        updatedRecord.setAllergies(List.of("allergy1"));

        when(validator.validate(updatedRecord)).thenReturn(Collections.emptySet());

        // Act
        MedicalRecord result = medicalRecordService.updateMedicalRecord(firstName, lastName, updatedRecord);

        // Assert
        assertEquals(updatedRecord.getBirthdate(), result.getBirthdate());
        assertEquals(updatedRecord.getMedications(), result.getMedications());
        assertEquals(updatedRecord.getAllergies(), result.getAllergies());
        verify(dataRepository).saveData();
    }

    @Test
    @DisplayName("Test de mise à jour d'un dossier médical inexistant")
    void testUpdateMedicalRecord_NonExisting() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";
        MedicalRecord updatedRecord = new MedicalRecord();
        updatedRecord.setFirstName(firstName);
        updatedRecord.setLastName(lastName);
        updatedRecord.setBirthdate(LocalDate.of(1991, 1, 1));

        when(validator.validate(updatedRecord)).thenReturn(Collections.emptySet());

        // Act
        MedicalRecord result = medicalRecordService.updateMedicalRecord(firstName, lastName, updatedRecord);

        // Assert
        assertNull(result);
        verify(dataRepository, never()).saveData();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Test de mise à jour d'un dossier médical invalide")
    void testUpdateMedicalRecord_Invalid() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        MedicalRecord updatedRecord = new MedicalRecord();
        updatedRecord.setFirstName(firstName);
        updatedRecord.setLastName(lastName);
        Set<ConstraintViolation<MedicalRecord>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));
        when(validator.validate(updatedRecord)).thenReturn(violations);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () ->
            medicalRecordService.updateMedicalRecord(firstName, lastName, updatedRecord)
        );
    }

    @Test
    @DisplayName("Test de suppression d'un dossier médical existant")
    void testDeleteMedicalRecord_Existing() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";
        MedicalRecord record = new MedicalRecord();
        record.setFirstName(firstName);
        record.setLastName(lastName);
        mockMedicalRecords.add(record);

        // Act
        boolean result = medicalRecordService.deleteMedicalRecord(firstName, lastName);

        // Assert
        assertTrue(result);
        verify(dataRepository).saveData();
    }

    @Test
    @DisplayName("Test de suppression d'un dossier médical inexistant")
    void testDeleteMedicalRecord_NonExisting() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";

        // Act
        boolean result = medicalRecordService.deleteMedicalRecord(firstName, lastName);

        // Assert
        assertFalse(result);
        verify(dataRepository, never()).saveData();
    }

    @Test
    @DisplayName("Test de suppression d'un dossier médical avec une erreur lors de la sauvegarde")
    void testDeleteMedicalRecord_WithSaveError() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";
        MedicalRecord record = new MedicalRecord();
        record.setFirstName(firstName);
        record.setLastName(lastName);
        mockMedicalRecords.add(record);
        doThrow(new RuntimeException("Erreur de sauvegarde")).when(dataRepository).saveData();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            try {
                medicalRecordService.deleteMedicalRecord(firstName, lastName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
