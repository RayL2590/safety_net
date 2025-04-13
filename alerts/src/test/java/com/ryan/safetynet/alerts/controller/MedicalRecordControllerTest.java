package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.service.MedicalRecordService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import com.ryan.safetynet.alerts.dto.MedicalRecordInputDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du controller MedicalRecordController")
class MedicalRecordControllerTest {

    @Mock
    private MedicalRecordService medicalRecordService;

    @InjectMocks
    private MedicalRecordController medicalRecordController;

    @Test
    @DisplayName("Test d'ajout d'un nouveau dossier médical")
    void testAddMedicalRecord() throws IOException {
        // Arrange
        MedicalRecordInputDTO inputDTO = new MedicalRecordInputDTO();
        inputDTO.setFirstName("John");
        inputDTO.setLastName("Doe");
        inputDTO.setBirthdate("01/01/1990");
        inputDTO.setMedications(Arrays.asList("med1", "med2"));
        inputDTO.setAllergies(Arrays.asList("allergy1", "allergy2"));

        MedicalRecord expectedRecord = new MedicalRecord();
        expectedRecord.setFirstName("John");
        expectedRecord.setLastName("Doe");
        expectedRecord.setBirthdate(LocalDate.of(1990, 1, 1));
        expectedRecord.setMedications(Arrays.asList("med1", "med2"));
        expectedRecord.setAllergies(Arrays.asList("allergy1", "allergy2"));

        when(medicalRecordService.addMedicalRecord(any(MedicalRecord.class))).thenReturn(expectedRecord);

        // Act
        ResponseEntity<MedicalRecord> response = medicalRecordController.addMedicalRecord(inputDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), response.getBody().getBirthdate());
        assertEquals(2, response.getBody().getMedications().size());
        assertEquals(2, response.getBody().getAllergies().size());
    }

    @Test
    @DisplayName("Test de mise à jour d'un dossier médical existant")
    void testUpdateMedicalRecord() throws IOException {
        // Arrange
        MedicalRecordInputDTO inputDTO = new MedicalRecordInputDTO();
        inputDTO.setFirstName("John");
        inputDTO.setLastName("Doe");
        inputDTO.setBirthdate("01/01/1990");
        inputDTO.setMedications(Arrays.asList("med3", "med4"));
        inputDTO.setAllergies(Arrays.asList("allergy3", "allergy4"));

        MedicalRecord updatedRecord = new MedicalRecord();
        updatedRecord.setFirstName("John");
        updatedRecord.setLastName("Doe");
        updatedRecord.setBirthdate(LocalDate.of(1990, 1, 1));
        updatedRecord.setMedications(Arrays.asList("med3", "med4"));
        updatedRecord.setAllergies(Arrays.asList("allergy3", "allergy4"));

        when(medicalRecordService.updateMedicalRecord(eq("John"), eq("Doe"), any(MedicalRecord.class)))
            .thenReturn(updatedRecord);

        // Act
        ResponseEntity<MedicalRecord> response = medicalRecordController.updateMedicalRecord(inputDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals(2, response.getBody().getMedications().size());
        assertEquals(2, response.getBody().getAllergies().size());
    }

    @Test
    @DisplayName("Test de mise à jour d'un dossier médical inexistant")
    void testUpdateMedicalRecord_NotFound() throws IOException {
        // Arrange
        MedicalRecordInputDTO inputDTO = new MedicalRecordInputDTO();
        inputDTO.setFirstName("John");
        inputDTO.setLastName("Doe");
        inputDTO.setBirthdate("01/01/1990");

        when(medicalRecordService.updateMedicalRecord(eq("John"), eq("Doe"), any(MedicalRecord.class)))
            .thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
            medicalRecordController.updateMedicalRecord(inputDTO)
        );
    }

    @Test
    @DisplayName("Test de suppression d'un dossier médical")
    void testDeleteMedicalRecord() throws IOException {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        when(medicalRecordService.deleteMedicalRecord(firstName, lastName)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = medicalRecordController.deleteMedicalRecord(firstName, lastName);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Test de suppression d'un dossier médical inexistant")
    void testDeleteMedicalRecord_NotFound() throws IOException {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        when(medicalRecordService.deleteMedicalRecord(firstName, lastName)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
            medicalRecordController.deleteMedicalRecord(firstName, lastName)
        );
    }

    @Test
    @DisplayName("Test de récupération d'un dossier médical")
    void testGetMedicalRecord() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName(firstName);
        medicalRecord.setLastName(lastName);
        medicalRecord.setBirthdate(LocalDate.of(1990, 1, 1));
        medicalRecord.setMedications(Arrays.asList("med1", "med2"));
        medicalRecord.setAllergies(Arrays.asList("allergy1", "allergy2"));

        when(medicalRecordService.findMedicalRecordByName(firstName, lastName))
            .thenReturn(Optional.of(medicalRecord));

        // Act
        ResponseEntity<MedicalRecord> response = medicalRecordController.getMedicalRecord(firstName, lastName);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), response.getBody().getBirthdate());
        assertEquals(2, response.getBody().getMedications().size());
        assertEquals(2, response.getBody().getAllergies().size());
    }

    @Test
    @DisplayName("Test de récupération d'un dossier médical inexistant")
    void testGetMedicalRecord_NotFound() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        when(medicalRecordService.findMedicalRecordByName(firstName, lastName))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
            medicalRecordController.getMedicalRecord(firstName, lastName)
        );
    }

    @Test
    @DisplayName("Test d'ajout d'un dossier médical avec une date de naissance invalide")
    void testAddMedicalRecord_InvalidBirthdate() {
        // Arrange
        MedicalRecordInputDTO inputDTO = new MedicalRecordInputDTO();
        inputDTO.setFirstName("John");
        inputDTO.setLastName("Doe");
        inputDTO.setBirthdate("invalid-date");
        inputDTO.setMedications(Arrays.asList("med1", "med2"));
        inputDTO.setAllergies(Arrays.asList("allergy1", "allergy2"));

        // Act & Assert
        assertThrows(java.time.format.DateTimeParseException.class, () ->
            medicalRecordController.addMedicalRecord(inputDTO)
        );
    }
}
