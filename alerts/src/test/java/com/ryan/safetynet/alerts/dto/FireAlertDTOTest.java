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

@DisplayName("Tests du DTO FireAlertDTO")
class FireAlertDTOTest {

    private FireAlertDTO fireAlertDTO;
    private Validator validator;

    @BeforeEach
    void setUp() {
        fireAlertDTO = new FireAlertDTO();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des valeurs valides")
    void testCreateWithValidValues() {
        // Arrange
        List<PersonWithMedicalInfoDTO> residents = List.of(
            createSamplePersonWithMedicalInfo("John", "Doe"),
            createSamplePersonWithMedicalInfo("Jane", "Doe")
        );
        String stationNumber = "1";

        // Act
        fireAlertDTO.setResidents(residents);
        fireAlertDTO.setFireStationNumber(stationNumber);

        // Assert
        assertEquals(residents, fireAlertDTO.getResidents());
        assertEquals(stationNumber, fireAlertDTO.getFireStationNumber());
    }

    @Test
    @DisplayName("Test de validation avec des valeurs valides")
    void testValidationWithValidValues() {
        // Arrange
        fireAlertDTO.setResidents(List.of(createSamplePersonWithMedicalInfo("John", "Doe")));
        fireAlertDTO.setFireStationNumber("1");

        // Act
        Set<ConstraintViolation<FireAlertDTO>> violations = validator.validate(fireAlertDTO);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec une liste de résidents vide")
    void testValidationWithEmptyResidents() {
        // Arrange
        fireAlertDTO.setResidents(List.of());
        fireAlertDTO.setFireStationNumber("1");

        // Act
        Set<ConstraintViolation<FireAlertDTO>> violations = validator.validate(fireAlertDTO);

        // Assert
        assertTrue(violations.isEmpty());
        assertTrue(fireAlertDTO.getResidents().isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec une liste de résidents null")
    void testValidationWithNullResidents() {
        // Arrange
        fireAlertDTO.setResidents(null);
        fireAlertDTO.setFireStationNumber("1");

        // Act
        Set<ConstraintViolation<FireAlertDTO>> violations = validator.validate(fireAlertDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("La liste des résidents ne peut pas être null")));
    }

    @Test
    @DisplayName("Test de validation avec un numéro de caserne vide")
    void testValidationWithEmptyStationNumber() {
        // Arrange
        fireAlertDTO.setResidents(List.of(createSamplePersonWithMedicalInfo("John", "Doe")));
        fireAlertDTO.setFireStationNumber("");

        // Act
        Set<ConstraintViolation<FireAlertDTO>> violations = validator.validate(fireAlertDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le numéro de la caserne est obligatoire")));
    }

    @Test
    @DisplayName("Test de validation avec un numéro de caserne null")
    void testValidationWithNullStationNumber() {
        // Arrange
        fireAlertDTO.setResidents(List.of(createSamplePersonWithMedicalInfo("John", "Doe")));
        fireAlertDTO.setFireStationNumber(null);

        // Act
        Set<ConstraintViolation<FireAlertDTO>> violations = validator.validate(fireAlertDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le numéro de la caserne est obligatoire")));
    }

    private PersonWithMedicalInfoDTO createSamplePersonWithMedicalInfo(String firstName, String lastName) {
        PersonWithMedicalInfoDTO person = new PersonWithMedicalInfoDTO();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setPhone("123-456-7890");
        person.setAge(30);
        person.setMedications(List.of("med1", "med2"));
        person.setAllergies(List.of("allergy1", "allergy2"));
        return person;
    }
} 