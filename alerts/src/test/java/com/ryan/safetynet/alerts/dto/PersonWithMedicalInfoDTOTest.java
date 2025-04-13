package com.ryan.safetynet.alerts.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests du DTO PersonWithMedicalInfoDTO")
class PersonWithMedicalInfoDTOTest {

    private PersonWithMedicalInfoDTO personWithMedicalInfoDTO;

    @BeforeEach
    void setUp() {
        personWithMedicalInfoDTO = new PersonWithMedicalInfoDTO();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des valeurs valides")
    void testCreateWithValidValues() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String phone = "123-456-7890";
        int age = 30;
        List<String> medications = Arrays.asList("med1", "med2");
        List<String> allergies = Arrays.asList("allergy1", "allergy2");

        // Act
        personWithMedicalInfoDTO.setFirstName(firstName);
        personWithMedicalInfoDTO.setLastName(lastName);
        personWithMedicalInfoDTO.setPhone(phone);
        personWithMedicalInfoDTO.setAge(age);
        personWithMedicalInfoDTO.setMedications(medications);
        personWithMedicalInfoDTO.setAllergies(allergies);

        // Assert
        assertEquals(firstName, personWithMedicalInfoDTO.getFirstName());
        assertEquals(lastName, personWithMedicalInfoDTO.getLastName());
        assertEquals(phone, personWithMedicalInfoDTO.getPhone());
        assertEquals(age, personWithMedicalInfoDTO.getAge());
        assertEquals(medications, personWithMedicalInfoDTO.getMedications());
        assertEquals(allergies, personWithMedicalInfoDTO.getAllergies());
    }

    @Test
    @DisplayName("Test de création avec des listes vides")
    void testCreateWithEmptyLists() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String phone = "123-456-7890";
        int age = 30;
        List<String> emptyList = Collections.emptyList();

        // Act
        personWithMedicalInfoDTO.setFirstName(firstName);
        personWithMedicalInfoDTO.setLastName(lastName);
        personWithMedicalInfoDTO.setPhone(phone);
        personWithMedicalInfoDTO.setAge(age);
        personWithMedicalInfoDTO.setMedications(emptyList);
        personWithMedicalInfoDTO.setAllergies(emptyList);

        // Assert
        assertEquals(firstName, personWithMedicalInfoDTO.getFirstName());
        assertEquals(lastName, personWithMedicalInfoDTO.getLastName());
        assertEquals(phone, personWithMedicalInfoDTO.getPhone());
        assertEquals(age, personWithMedicalInfoDTO.getAge());
        assertTrue(personWithMedicalInfoDTO.getMedications().isEmpty());
        assertTrue(personWithMedicalInfoDTO.getAllergies().isEmpty());
    }

    @Test
    @DisplayName("Test de création avec des listes null")
    void testCreateWithNullLists() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String phone = "123-456-7890";
        int age = 30;

        // Act
        personWithMedicalInfoDTO.setFirstName(firstName);
        personWithMedicalInfoDTO.setLastName(lastName);
        personWithMedicalInfoDTO.setPhone(phone);
        personWithMedicalInfoDTO.setAge(age);
        personWithMedicalInfoDTO.setMedications(null);
        personWithMedicalInfoDTO.setAllergies(null);

        // Assert
        assertEquals(firstName, personWithMedicalInfoDTO.getFirstName());
        assertEquals(lastName, personWithMedicalInfoDTO.getLastName());
        assertEquals(phone, personWithMedicalInfoDTO.getPhone());
        assertEquals(age, personWithMedicalInfoDTO.getAge());
        assertNull(personWithMedicalInfoDTO.getMedications());
        assertNull(personWithMedicalInfoDTO.getAllergies());
    }

    @Test
    @DisplayName("Test de création avec un âge négatif")
    void testCreateWithNegativeAge() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String phone = "123-456-7890";
        int negativeAge = -1;

        // Act
        personWithMedicalInfoDTO.setFirstName(firstName);
        personWithMedicalInfoDTO.setLastName(lastName);
        personWithMedicalInfoDTO.setPhone(phone);
        personWithMedicalInfoDTO.setAge(negativeAge);

        // Assert
        assertEquals(firstName, personWithMedicalInfoDTO.getFirstName());
        assertEquals(lastName, personWithMedicalInfoDTO.getLastName());
        assertEquals(phone, personWithMedicalInfoDTO.getPhone());
        assertEquals(negativeAge, personWithMedicalInfoDTO.getAge());
    }

    @Test
    @DisplayName("Test de la méthode toString")
    void testToString() {
        // Arrange
        personWithMedicalInfoDTO.setFirstName("John");
        personWithMedicalInfoDTO.setLastName("Doe");
        personWithMedicalInfoDTO.setPhone("123-456-7890");
        personWithMedicalInfoDTO.setAge(30);
        personWithMedicalInfoDTO.setMedications(Arrays.asList("med1", "med2"));
        personWithMedicalInfoDTO.setAllergies(Arrays.asList("allergy1", "allergy2"));

        // Act
        String toStringResult = personWithMedicalInfoDTO.toString();

        // Assert
        assertNotNull(toStringResult);
        assertFalse(toStringResult.isEmpty());
        assertTrue(toStringResult.contains("PersonWithMedicalInfoDTO(firstName=John, lastName=Doe, phone=123-456-7890, age=30, medications=[med1, med2], allergies=[allergy1, allergy2])"));
    }
} 