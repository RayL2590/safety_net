package com.ryan.safetynet.alerts.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests du DTO MedicalRecordInputDTO")
class MedicalRecordInputDTOTest {

    private MedicalRecordInputDTO medicalRecordInputDTO;
    private Validator validator;

    @BeforeEach
    void setUp() {
        medicalRecordInputDTO = new MedicalRecordInputDTO();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des valeurs valides")
    void testCreateWithValidValues() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String birthdate = "01/01/1990";
        List<String> medications = List.of("med1", "med2");
        List<String> allergies = List.of("allergy1", "allergy2");

        // Act
        medicalRecordInputDTO.setFirstName(firstName);
        medicalRecordInputDTO.setLastName(lastName);
        medicalRecordInputDTO.setBirthdate(birthdate);
        medicalRecordInputDTO.setMedications(medications);
        medicalRecordInputDTO.setAllergies(allergies);

        // Assert
        assertEquals(firstName, medicalRecordInputDTO.getFirstName());
        assertEquals(lastName, medicalRecordInputDTO.getLastName());
        assertEquals(birthdate, medicalRecordInputDTO.getBirthdate());
        assertEquals(medications, medicalRecordInputDTO.getMedications());
        assertEquals(allergies, medicalRecordInputDTO.getAllergies());
    }

    @Test
    @DisplayName("Test de validation avec des valeurs valides")
    void testValidationWithValidValues() {
        // Arrange
        medicalRecordInputDTO.setFirstName("John");
        medicalRecordInputDTO.setLastName("Doe");
        medicalRecordInputDTO.setBirthdate("01/01/1990");
        medicalRecordInputDTO.setMedications(List.of("med1", "med2"));
        medicalRecordInputDTO.setAllergies(List.of("allergy1", "allergy2"));

        // Act
        Set<ConstraintViolation<MedicalRecordInputDTO>> violations = validator.validate(medicalRecordInputDTO);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec un prénom vide")
    void testValidationWithEmptyFirstName() {
        // Arrange
        medicalRecordInputDTO.setFirstName("");
        medicalRecordInputDTO.setLastName("Doe");
        medicalRecordInputDTO.setBirthdate("01/01/1990");

        // Act
        Set<ConstraintViolation<MedicalRecordInputDTO>> violations = validator.validate(medicalRecordInputDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le prénom est obligatoire")));
    }

    @Test
    @DisplayName("Test de validation avec un nom vide")
    void testValidationWithEmptyLastName() {
        // Arrange
        medicalRecordInputDTO.setFirstName("John");
        medicalRecordInputDTO.setLastName("");
        medicalRecordInputDTO.setBirthdate("01/01/1990");

        // Act
        Set<ConstraintViolation<MedicalRecordInputDTO>> violations = validator.validate(medicalRecordInputDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le nom est obligatoire")));
    }

    @Test
    @DisplayName("Test de validation avec une date de naissance invalide")
    void testValidationWithInvalidBirthdate() {
        // Arrange
        medicalRecordInputDTO.setFirstName("John");
        medicalRecordInputDTO.setLastName("Doe");
        medicalRecordInputDTO.setBirthdate("1990-01-01"); // Format invalide

        // Act
        Set<ConstraintViolation<MedicalRecordInputDTO>> violations = validator.validate(medicalRecordInputDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("La date de naissance doit être au format MM/dd/yyyy")));
    }

    @Test
    @DisplayName("Test de validation avec une liste de médicaments vide")
    void testValidationWithEmptyMedications() {
        // Arrange
        medicalRecordInputDTO.setFirstName("John");
        medicalRecordInputDTO.setLastName("Doe");
        medicalRecordInputDTO.setBirthdate("01/01/1990");
        medicalRecordInputDTO.setMedications(List.of());

        // Act
        Set<ConstraintViolation<MedicalRecordInputDTO>> violations = validator.validate(medicalRecordInputDTO);

        // Assert
        assertTrue(violations.isEmpty());
        assertTrue(medicalRecordInputDTO.getMedications().isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec une liste d'allergies vide")
    void testValidationWithEmptyAllergies() {
        // Arrange
        medicalRecordInputDTO.setFirstName("John");
        medicalRecordInputDTO.setLastName("Doe");
        medicalRecordInputDTO.setBirthdate("01/01/1990");
        medicalRecordInputDTO.setAllergies(List.of());

        // Act
        Set<ConstraintViolation<MedicalRecordInputDTO>> violations = validator.validate(medicalRecordInputDTO);

        // Assert
        assertTrue(violations.isEmpty());
        assertTrue(medicalRecordInputDTO.getAllergies().isEmpty());
    }

    @Test
    @DisplayName("Test de la méthode toString")
    void testToString() {
        // Arrange
        medicalRecordInputDTO.setFirstName("John");
        medicalRecordInputDTO.setLastName("Doe");
        medicalRecordInputDTO.setBirthdate("01/01/1990");
        medicalRecordInputDTO.setMedications(List.of("med1", "med2"));
        medicalRecordInputDTO.setAllergies(List.of("allergy1", "allergy2"));

        // Act
        String toStringResult = medicalRecordInputDTO.toString();

        // Assert
        assertNotNull(toStringResult);
        assertFalse(toStringResult.isEmpty());
        assertTrue(toStringResult.contains("MedicalRecordInputDTO"));
        assertTrue(toStringResult.contains("firstName=John"));
        assertTrue(toStringResult.contains("lastName=Doe"));
        assertTrue(toStringResult.contains("birthdate=01/01/1990"));
        assertTrue(toStringResult.contains("medications=[med1, med2]"));
        assertTrue(toStringResult.contains("allergies=[allergy1, allergy2]"));
    }
} 