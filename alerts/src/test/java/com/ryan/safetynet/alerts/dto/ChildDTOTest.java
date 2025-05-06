package com.ryan.safetynet.alerts.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests du DTO ChildDTO")
class ChildDTOTest {

    private ChildDTO childDTO;
    private Validator validator;

    @BeforeEach
    void setUp() {
        childDTO = new ChildDTO();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des valeurs valides")
    void testCreateWithValidValues() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        int age = 10;

        // Act
        childDTO.setFirstName(firstName);
        childDTO.setLastName(lastName);
        childDTO.setAge(age);

        // Assert
        assertEquals(firstName, childDTO.getFirstName());
        assertEquals(lastName, childDTO.getLastName());
        assertEquals(age, childDTO.getAge());
    }

    @Test
    @DisplayName("Test de validation avec des valeurs valides")
    void testValidationWithValidValues() {
        // Arrange
        childDTO.setFirstName("John");
        childDTO.setLastName("Doe");
        childDTO.setAge(10);

        // Act
        Set<ConstraintViolation<ChildDTO>> violations = validator.validate(childDTO);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Test de validation avec un prénom vide")
    void testValidationWithEmptyFirstName(String firstName) {
        // Arrange
        childDTO.setFirstName(firstName);
        childDTO.setLastName("Doe");
        childDTO.setAge(10);

        // Act
        Set<ConstraintViolation<ChildDTO>> violations = validator.validate(childDTO);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Test de validation avec un nom vide")
    void testValidationWithEmptyLastName(String lastName) {
        // Arrange
        childDTO.setFirstName("John");
        childDTO.setLastName(lastName);
        childDTO.setAge(10);

        // Act
        Set<ConstraintViolation<ChildDTO>> violations = validator.validate(childDTO);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec un âge négatif")
    void testValidationWithNegativeAge() {
        // Arrange
        childDTO.setFirstName("John");
        childDTO.setLastName("Doe");
        childDTO.setAge(-1);

        // Act
        Set<ConstraintViolation<ChildDTO>> violations = validator.validate(childDTO);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec un âge supérieur à 18")
    void testValidationWithAgeOver18() {
        // Arrange
        childDTO.setFirstName("John");
        childDTO.setLastName("Doe");
        childDTO.setAge(19);

        // Act
        Set<ConstraintViolation<ChildDTO>> violations = validator.validate(childDTO);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec un âge égal à 18")
    void testValidationWithAge18() {
        // Arrange
        childDTO.setFirstName("John");
        childDTO.setLastName("Doe");
        childDTO.setAge(18);

        // Act
        Set<ConstraintViolation<ChildDTO>> violations = validator.validate(childDTO);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec un âge égal à 0")
    void testValidationWithAge0() {
        // Arrange
        childDTO.setFirstName("John");
        childDTO.setLastName("Doe");
        childDTO.setAge(0);

        // Act
        Set<ConstraintViolation<ChildDTO>> violations = validator.validate(childDTO);

        // Assert
        assertTrue(violations.isEmpty());
    }
} 