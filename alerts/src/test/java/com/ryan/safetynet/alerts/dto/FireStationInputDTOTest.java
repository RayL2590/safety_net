package com.ryan.safetynet.alerts.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests du DTO FireStationInputDTO")
class FireStationInputDTOTest {

    private FireStationInputDTO fireStationInputDTO;
    private Validator validator;

    @BeforeEach
    void setUp() {
        fireStationInputDTO = new FireStationInputDTO();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des valeurs valides")
    void testCreateWithValidValues() {
        // Arrange
        Integer station = 1;
        String address = "123 Main St";

        // Act
        fireStationInputDTO.setStation(station);
        fireStationInputDTO.setAddress(address);

        // Assert
        assertEquals(station, fireStationInputDTO.getStation());
        assertEquals(address, fireStationInputDTO.getAddress());
    }

    @Test
    @DisplayName("Test de validation avec des valeurs valides")
    void testValidationWithValidValues() {
        // Arrange
        fireStationInputDTO.setStation(1);
        fireStationInputDTO.setAddress("123 Main St");

        // Act
        Set<ConstraintViolation<FireStationInputDTO>> violations = validator.validate(fireStationInputDTO);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec un numéro de station null")
    void testValidationWithNullStation() {
        // Arrange
        fireStationInputDTO.setStation(null);
        fireStationInputDTO.setAddress("123 Main St");

        // Act
        Set<ConstraintViolation<FireStationInputDTO>> violations = validator.validate(fireStationInputDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le numéro de la caserne est obligatoire")));
    }

    @Test
    @DisplayName("Test de validation avec un numéro de station négatif")
    void testValidationWithNegativeStation() {
        // Arrange
        fireStationInputDTO.setStation(-1);
        fireStationInputDTO.setAddress("123 Main St");

        // Act
        Set<ConstraintViolation<FireStationInputDTO>> violations = validator.validate(fireStationInputDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le numéro de la caserne doit être positif")));
    }

    @Test
    @DisplayName("Test de validation avec un numéro de station à zéro")
    void testValidationWithZeroStation() {
        // Arrange
        fireStationInputDTO.setStation(0);
        fireStationInputDTO.setAddress("123 Main St");

        // Act
        Set<ConstraintViolation<FireStationInputDTO>> violations = validator.validate(fireStationInputDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le numéro de la caserne doit être positif")));
    }

    @Test
    @DisplayName("Test de validation avec une adresse vide")
    void testValidationWithEmptyAddress() {
        // Arrange
        fireStationInputDTO.setStation(1);
        fireStationInputDTO.setAddress("");

        // Act
        Set<ConstraintViolation<FireStationInputDTO>> violations = validator.validate(fireStationInputDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("L'adresse est obligatoire")));
    }

    @Test
    @DisplayName("Test de validation avec une adresse contenant uniquement des espaces")
    void testValidationWithBlankAddress() {
        // Arrange
        fireStationInputDTO.setStation(1);
        fireStationInputDTO.setAddress("   ");

        // Act
        Set<ConstraintViolation<FireStationInputDTO>> violations = validator.validate(fireStationInputDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("L'adresse est obligatoire")));
    }

    @Test
    @DisplayName("Test de validation avec une adresse null")
    void testValidationWithNullAddress() {
        // Arrange
        fireStationInputDTO.setStation(1);
        fireStationInputDTO.setAddress(null);

        // Act
        Set<ConstraintViolation<FireStationInputDTO>> violations = validator.validate(fireStationInputDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("L'adresse est obligatoire")));
    }

    @Test
    @DisplayName("Test de la méthode toString")
    void testToString() {
        // Arrange
        fireStationInputDTO.setStation(1);
        fireStationInputDTO.setAddress("123 Main St");

        // Act
        String toStringResult = fireStationInputDTO.toString();

        // Assert
        assertTrue(toStringResult.contains("station=1"));
        assertTrue(toStringResult.contains("address=123 Main St"));
    }
} 