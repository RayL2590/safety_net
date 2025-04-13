package com.ryan.safetynet.alerts.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de la classe MedicalRecord")
class MedicalRecordTest {

    private MedicalRecord medicalRecord;
    private Validator validator;

    @BeforeEach
    void setUp() {
        medicalRecord = new MedicalRecord();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test de création d'une instance de MedicalRecord avec des valeurs valides")
    void testCreateMedicalRecordWithValidValues() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        LocalDate birthdate = LocalDate.of(1990, 1, 1);
        List<String> medications = List.of("aznol:350mg", "hydrapermazol:100mg");
        List<String> allergies = List.of("nillacilan", "peanuts");

        // Act
        medicalRecord.setFirstName(firstName);
        medicalRecord.setLastName(lastName);
        medicalRecord.setBirthdate(birthdate);
        medicalRecord.setMedications(medications);
        medicalRecord.setAllergies(allergies);

        // Assert
        assertEquals(firstName, medicalRecord.getFirstName());
        assertEquals(lastName, medicalRecord.getLastName());
        assertEquals(birthdate, medicalRecord.getBirthdate());
        assertEquals(medications, medicalRecord.getMedications());
        assertEquals(allergies, medicalRecord.getAllergies());
    }

    @Test
    @DisplayName("Test de validation avec tous les champs vides")
    void testValidationWithAllEmptyFields() {
        // Act
        Set<ConstraintViolation<MedicalRecord>> violations = validator.validate(medicalRecord);

        // Assert
        assertEquals(2, violations.size()); // Seuls firstName et lastName sont obligatoires
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Test de validation avec un prénom vide")
    void testValidationWithEmptyFirstName(String firstName) {
        // Arrange
        medicalRecord.setFirstName(firstName);
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate(LocalDate.of(1990, 1, 1));

        // Act
        Set<ConstraintViolation<MedicalRecord>> violations = validator.validate(medicalRecord);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le prénom est obligatoire")));
    }

    @Test
    @DisplayName("Test de validation avec une date de naissance dans le futur")
    void testValidationWithFutureBirthdate() {
        // Arrange
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate(LocalDate.now().plusDays(1)); // Date dans le futur

        // Act
        Set<ConstraintViolation<MedicalRecord>> violations = validator.validate(medicalRecord);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("La date de naissance doit être dans le passé")));
    }

    @Test
    @DisplayName("Test de validation avec des médicaments invalides")
    void testValidationWithInvalidMedications() {
        // Arrange
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate(LocalDate.of(1990, 1, 1));
        medicalRecord.setMedications(List.of("invalid@medication")); // Caractère spécial non autorisé

        // Act
        Set<ConstraintViolation<MedicalRecord>> violations = validator.validate(medicalRecord);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Médicament invalide")));
    }

    @Test
    @DisplayName("Test de validation avec des allergies invalides")
    void testValidationWithInvalidAllergies() {
        // Arrange
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate(LocalDate.of(1990, 1, 1));
        medicalRecord.setAllergies(List.of("invalid@allergy")); // Caractère spécial non autorisé

        // Act
        Set<ConstraintViolation<MedicalRecord>> violations = validator.validate(medicalRecord);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Allergie invalide")));
    }

    @Test
    @DisplayName("Test de la méthode toString")
    void testToString() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        LocalDate birthdate = LocalDate.of(1990, 1, 1);
        List<String> medications = List.of("aznol:350mg");
        List<String> allergies = List.of("peanuts");

        medicalRecord.setFirstName(firstName);
        medicalRecord.setLastName(lastName);
        medicalRecord.setBirthdate(birthdate);
        medicalRecord.setMedications(medications);
        medicalRecord.setAllergies(allergies);

        // Act
        String toStringResult = medicalRecord.toString();

        // Assert
        assertTrue(toStringResult.contains("firstName='John'"));
        assertTrue(toStringResult.contains("lastName='Doe'"));
        assertTrue(toStringResult.contains("birthdate=1990-01-01"));
        assertTrue(toStringResult.contains("medications=[aznol:350mg]"));
        assertTrue(toStringResult.contains("allergies=[peanuts]"));
    }

    @Test
    @DisplayName("Test de validation avec des valeurs valides")
    void testValidationWithValidValues() {
        // Arrange
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate(LocalDate.of(1990, 1, 1));
        medicalRecord.setMedications(List.of("aznol:350mg"));
        medicalRecord.setAllergies(List.of("peanuts"));

        // Act
        Set<ConstraintViolation<MedicalRecord>> violations = validator.validate(medicalRecord);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test avec des listes vides de médicaments et d'allergies")
    void testWithEmptyLists() {
        // Arrange
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate(LocalDate.of(1990, 1, 1));
        medicalRecord.setMedications(List.of());
        medicalRecord.setAllergies(List.of());

        // Act
        Set<ConstraintViolation<MedicalRecord>> violations = validator.validate(medicalRecord);

        // Assert
        assertTrue(violations.isEmpty());
        assertTrue(medicalRecord.getMedications().isEmpty());
        assertTrue(medicalRecord.getAllergies().isEmpty());
    }

    @Test
    @DisplayName("Test avec des listes null de médicaments et d'allergies")
    void testWithNullLists() {
        // Arrange
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate(LocalDate.of(1990, 1, 1));
        medicalRecord.setMedications(null);
        medicalRecord.setAllergies(null);

        // Act
        Set<ConstraintViolation<MedicalRecord>> violations = validator.validate(medicalRecord);

        // Assert
        assertTrue(violations.isEmpty());
        assertNull(medicalRecord.getMedications());
        assertNull(medicalRecord.getAllergies());
    }
} 