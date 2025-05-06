package com.ryan.safetynet.alerts.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests du DTO HouseholdMemberDTO")
class HouseholdMemberDTOTest {

    private HouseholdMemberDTO householdMemberDTO;

    @BeforeEach
    void setUp() {
        householdMemberDTO = new HouseholdMemberDTO();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des valeurs valides")
    void testCreateWithValidValues() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";

        // Act
        householdMemberDTO.setFirstName(firstName);
        householdMemberDTO.setLastName(lastName);

        // Assert
        assertEquals(firstName, householdMemberDTO.getFirstName());
        assertEquals(lastName, householdMemberDTO.getLastName());
    }

    @Test
    @DisplayName("Test de création avec un prénom vide")
    void testCreateWithEmptyFirstName() {
        // Arrange
        String emptyFirstName = "";
        String lastName = "Doe";

        // Act
        householdMemberDTO.setFirstName(emptyFirstName);
        householdMemberDTO.setLastName(lastName);

        // Assert
        assertEquals(emptyFirstName, householdMemberDTO.getFirstName());
        assertEquals(lastName, householdMemberDTO.getLastName());
    }

    @Test
    @DisplayName("Test de création avec un nom vide")
    void testCreateWithEmptyLastName() {
        // Arrange
        String firstName = "John";
        String emptyLastName = "";

        // Act
        householdMemberDTO.setFirstName(firstName);
        householdMemberDTO.setLastName(emptyLastName);

        // Assert
        assertEquals(firstName, householdMemberDTO.getFirstName());
        assertEquals(emptyLastName, householdMemberDTO.getLastName());
    }

    @Test
    @DisplayName("Test de création avec un prénom null")
    void testCreateWithNullFirstName() {
        // Arrange
        String lastName = "Doe";

        // Act
        householdMemberDTO.setFirstName(null);
        householdMemberDTO.setLastName(lastName);

        // Assert
        assertNull(householdMemberDTO.getFirstName());
        assertEquals(lastName, householdMemberDTO.getLastName());
    }

    @Test
    @DisplayName("Test de création avec un nom null")
    void testCreateWithNullLastName() {
        // Arrange
        String firstName = "John";

        // Act
        householdMemberDTO.setFirstName(firstName);
        householdMemberDTO.setLastName(null);

        // Assert
        assertEquals(firstName, householdMemberDTO.getFirstName());
        assertNull(householdMemberDTO.getLastName());
    }

    @Test
    @DisplayName("Test de la méthode toString")
    void testToString() {
        // Arrange
        householdMemberDTO.setFirstName("John");
        householdMemberDTO.setLastName("Doe");

        // Act
        String toStringResult = householdMemberDTO.toString();

        // Assert
        assertNotNull(toStringResult);
        assertFalse(toStringResult.isEmpty());
        assertTrue(toStringResult.contains("HouseholdMemberDTO(firstName=John, lastName=Doe)"));
    }
} 