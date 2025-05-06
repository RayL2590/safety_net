package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.model.Person;
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
    
    @Mock
    private PersonService personService;

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
    @DisplayName("Test d'ajout d'un dossier médical valide pour une personne existante")
    void testAddMedicalRecord_Valid() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        MedicalRecord record = new MedicalRecord();
        record.setFirstName("John");
        record.setLastName("Doe");
        record.setBirthdate(LocalDate.of(1990, 1, 1));
        record.setMedications(List.of("med1"));
        record.setAllergies(List.of("allergy1"));

        // Mock pour la vérification de l'existence de la personne
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        when(personService.findPersonByName("John", "Doe")).thenReturn(Optional.of(person));
        
        when(validator.validate(record)).thenReturn(Collections.emptySet());

        // Act
        MedicalRecord result = medicalRecordService.addMedicalRecord(record);

        // Assert
        assertEquals(record, result);
        assertTrue(mockMedicalRecords.contains(record));
        verify(dataRepository).saveData();
        verify(personService).findPersonByName("John", "Doe");
    }
    
    @Test
    @DisplayName("Test d'ajout d'un dossier médical pour une personne inexistante")
    void testAddMedicalRecord_PersonDoesNotExist() throws IOException {
        // Arrange
        MedicalRecord record = new MedicalRecord();
        record.setFirstName("NonExistent");
        record.setLastName("Person");
        record.setBirthdate(LocalDate.of(1990, 1, 1));
        
        // La personne n'existe pas
        when(personService.findPersonByName("NonExistent", "Person")).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> 
            medicalRecordService.addMedicalRecord(record)
        );
        
        assertTrue(exception.getMessage().contains("Personne non trouvée"));
        verify(validator, never()).validate(record);
        verify(dataRepository, never()).saveData();
    }

    @Test
    @DisplayName("Test d'ajout d'un dossier médical invalide")
    @SuppressWarnings("unchecked")
    void testAddMedicalRecord_Invalid() throws IOException {
        // Arrange
        MedicalRecordService spyService = spy(medicalRecordService);
        
        MedicalRecord record = new MedicalRecord();
        record.setFirstName("John");
        record.setLastName("Doe");
        
        // La personne existe
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        when(personService.findPersonByName("John", "Doe")).thenReturn(Optional.of(person));
        
        // On simule qu'aucun dossier médical n'existe déjà
        doReturn(Optional.empty()).when(spyService).findMedicalRecordByName("John", "Doe");
        
        // Mais les données du dossier sont invalides
        Set<ConstraintViolation<MedicalRecord>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));
        when(validator.validate(record)).thenReturn(violations);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () ->
            spyService.addMedicalRecord(record)
        );
        
        verify(personService).findPersonByName("John", "Doe");
        verify(validator).validate(record);
    }

    @Test
    @DisplayName("Test d'ajout d'un dossier médical qui existe déjà")
    void testAddMedicalRecord_AlreadyExists() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";
        
        // Le dossier médical existe déjà
        MedicalRecord existingRecord = new MedicalRecord();
        existingRecord.setFirstName(firstName);
        existingRecord.setLastName(lastName);
        mockMedicalRecords.add(existingRecord);
        
        // La personne existe
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        when(personService.findPersonByName(firstName, lastName)).thenReturn(Optional.of(person));
        
        MedicalRecord newRecord = new MedicalRecord();
        newRecord.setFirstName(firstName);
        newRecord.setLastName(lastName);
        newRecord.setBirthdate(LocalDate.of(1990, 1, 1));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            medicalRecordService.addMedicalRecord(newRecord)
        );
        
        assertTrue(exception.getMessage().contains("Un dossier médical existe déjà pour"));
        assertTrue(exception.getMessage().contains("Utilisez la méthode PUT"));
        verify(personService).findPersonByName(firstName, lastName);
        verify(dataRepository, never()).saveData();
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
        
        // Mock pour la vérification de l'existence de la personne
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        when(personService.findPersonByName(firstName, lastName)).thenReturn(Optional.of(person));

        when(validator.validate(updatedRecord)).thenReturn(Collections.emptySet());

        // Act
        MedicalRecord result = medicalRecordService.updateMedicalRecord(firstName, lastName, updatedRecord);

        // Assert
        assertEquals(updatedRecord.getBirthdate(), result.getBirthdate());
        assertEquals(updatedRecord.getMedications(), result.getMedications());
        assertEquals(updatedRecord.getAllergies(), result.getAllergies());
        verify(dataRepository).saveData();
        verify(personService).findPersonByName(firstName, lastName);
    }

    @Test
    @DisplayName("Test de mise à jour d'un dossier médical pour une personne inexistante")
    void testUpdateMedicalRecord_PersonDoesNotExist() throws IOException {
        // Arrange
        String firstName = "NonExistent";
        String lastName = "Person";
        
        MedicalRecord updatedRecord = new MedicalRecord();
        updatedRecord.setFirstName(firstName);
        updatedRecord.setLastName(lastName);
        
        // La personne n'existe pas
        when(personService.findPersonByName(firstName, lastName)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> 
            medicalRecordService.updateMedicalRecord(firstName, lastName, updatedRecord)
        );
        
        assertTrue(exception.getMessage().contains("Personne non trouvée"));
        verify(validator, never()).validate(updatedRecord);
        verify(dataRepository, never()).saveData();
    }

    @Test
    @DisplayName("Test de mise à jour d'un dossier médical inexistant pour une personne existante")
    void testUpdateMedicalRecord_NonExisting() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";
        MedicalRecord updatedRecord = new MedicalRecord();
        updatedRecord.setFirstName(firstName);
        updatedRecord.setLastName(lastName);
        updatedRecord.setBirthdate(LocalDate.of(1991, 1, 1));
        
        // La personne existe
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        when(personService.findPersonByName(firstName, lastName)).thenReturn(Optional.of(person));

        when(validator.validate(updatedRecord)).thenReturn(Collections.emptySet());

        // Act
        MedicalRecord result = medicalRecordService.updateMedicalRecord(firstName, lastName, updatedRecord);

        // Assert
        assertNull(result);
        verify(dataRepository, never()).saveData();
        verify(personService).findPersonByName(firstName, lastName);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Test de mise à jour d'un dossier médical invalide")
    void testUpdateMedicalRecord_Invalid() throws IOException {
        // Arrange
        MedicalRecordService spyService = spy(medicalRecordService);
        
        String firstName = "John";
        String lastName = "Doe";
        MedicalRecord updatedRecord = new MedicalRecord();
        updatedRecord.setFirstName(firstName);
        updatedRecord.setLastName(lastName);
        
        // La personne existe
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        when(personService.findPersonByName(firstName, lastName)).thenReturn(Optional.of(person));
        
        // On simule qu'un dossier médical existe (pour la méthode findMedicalRecordByName)
        MedicalRecord existingRecord = new MedicalRecord();
        existingRecord.setFirstName(firstName);
        existingRecord.setLastName(lastName);
        
        // Mais les données du dossier sont invalides
        Set<ConstraintViolation<MedicalRecord>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));
        when(validator.validate(updatedRecord)).thenReturn(violations);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () ->
            spyService.updateMedicalRecord(firstName, lastName, updatedRecord)
        );
        
        verify(personService).findPersonByName(firstName, lastName);
        verify(validator).validate(updatedRecord);
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

    @Test
    @DisplayName("Test de récupération de tous les dossiers médicaux")
    void testGetAllMedicalRecords() {
        // Given
        List<MedicalRecord> expectedRecords = new ArrayList<>();
        MedicalRecord record1 = new MedicalRecord();
        record1.setFirstName("John");
        record1.setLastName("Doe");
        record1.setBirthdate(LocalDate.of(1990, 1, 1));
        record1.setMedications(List.of("med1"));
        record1.setAllergies(List.of("allergy1"));
        expectedRecords.add(record1);

        MedicalRecord record2 = new MedicalRecord();
        record2.setFirstName("Jane");
        record2.setLastName("Smith");
        record2.setBirthdate(LocalDate.of(1995, 2, 2));
        record2.setMedications(List.of("med2"));
        record2.setAllergies(List.of("allergy2"));
        expectedRecords.add(record2);

        Data mockData = new Data();
        mockData.setMedicalRecords(expectedRecords);
        when(dataRepository.getData()).thenReturn(mockData);

        // When
        List<MedicalRecord> result = medicalRecordService.getAllMedicalRecords();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedRecords, result);
        verify(dataRepository).getData();
    }
}
