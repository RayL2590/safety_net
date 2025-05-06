package com.ryan.safetynet.alerts.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de la classe FireStation")
class FireStationTest {

    private FireStation fireStation;
    private Validator validator;

    @BeforeEach
    void setUp() {
        fireStation = new FireStation();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test de création d'une instance de FireStation avec des valeurs valides")
    void testCreateFireStationWithValidValues() {
        // Arrange
        String address = "123 Main St";
        String station = "1";

        // Act
        fireStation.setAddress(address);
        fireStation.setStation(station);

        // Assert
        assertEquals(address, fireStation.getAddress());
        assertEquals(station, fireStation.getStation());
    }

    @Test
    @DisplayName("Test de validation avec une adresse vide")
    void testValidationWithEmptyAddress() {
        // Arrange
        fireStation.setAddress("");
        fireStation.setStation("1");

        // Act
        Set<ConstraintViolation<FireStation>> violations = validator.validate(fireStation);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("L'adresse est obligatoire'", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Test de validation avec une station vide")
    void testValidationWithEmptyStation() {
        // Arrange
        fireStation.setAddress("123 Main St");
        fireStation.setStation("");

        // Act
        Set<ConstraintViolation<FireStation>> violations = validator.validate(fireStation);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("La station est obligatoire", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Test de la méthode toString")
    void testToString() {
        // Arrange
        String address = "123 Main St";
        String station = "1";
        fireStation.setAddress(address);
        fireStation.setStation(station);

        // Act
        String toStringResult = fireStation.toString();

        // Assert
        assertTrue(toStringResult.contains("address='" + address + "'"));
        assertTrue(toStringResult.contains("station='" + station + "'"));
    }

    @Test
    @DisplayName("Test de validation avec des valeurs valides")
    void testValidationWithValidValues() {
        // Arrange
        fireStation.setAddress("123 Main St");
        fireStation.setStation("1");

        // Act
        Set<ConstraintViolation<FireStation>> violations = validator.validate(fireStation);

        // Assert
        assertTrue(violations.isEmpty());
    }
} 