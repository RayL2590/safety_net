package com.ryan.safetynet.alerts.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests du DTO PhoneAlertDTO")
class PhoneAlertDTOTest {

    private PhoneAlertDTO phoneAlertDTO;

    @BeforeEach
    void setUp() {
        phoneAlertDTO = new PhoneAlertDTO();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des numéros de téléphone valides")
    void testCreateWithValidPhoneNumbers() {
        // Arrange
        List<String> phoneNumbers = Arrays.asList("123-456-7890", "987-654-3210");

        // Act
        phoneAlertDTO.setPhoneNumbers(phoneNumbers);

        // Assert
        assertEquals(phoneNumbers, phoneAlertDTO.getPhoneNumbers());
    }

    @Test
    @DisplayName("Test de création avec une liste vide")
    void testCreateWithEmptyList() {
        // Arrange
        List<String> emptyList = Collections.emptyList();

        // Act
        phoneAlertDTO.setPhoneNumbers(emptyList);

        // Assert
        assertTrue(phoneAlertDTO.getPhoneNumbers().isEmpty());
    }

    @Test
    @DisplayName("Test de création avec une liste null")
    void testCreateWithNullList() {
        // Act
        phoneAlertDTO.setPhoneNumbers(null);

        // Assert
        assertNull(phoneAlertDTO.getPhoneNumbers());
    }

    @Test
    @DisplayName("Test de la méthode toString")
    void testToString() {
        // Arrange
        List<String> phoneNumbers = Arrays.asList("123-456-7890", "987-654-3210");
        phoneAlertDTO.setPhoneNumbers(phoneNumbers);

        // Act
        String toStringResult = phoneAlertDTO.toString();

        // Assert
        assertNotNull(toStringResult);
        assertFalse(toStringResult.isEmpty());
        assertTrue(toStringResult.contains("PhoneAlertDTO(phoneNumbers=[123-456-7890, 987-654-3210])"));
    }
} 