package com.ryan.safetynet.alerts.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests du DTO PersonInfoDTO")
class PersonInfoDTOTest {

    private PersonInfoDTO personInfoDTO;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        personInfoDTO = new PersonInfoDTO();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des valeurs valides")
    void testCreateWithValidValues() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String address = "123 Main St";
        int age = 30;
        String email = "john.doe@email.com";
        List<String> medications = List.of("med1", "med2");
        List<String> allergies = List.of("allergy1", "allergy2");

        // Act
        personInfoDTO.setFirstName(firstName);
        personInfoDTO.setLastName(lastName);
        personInfoDTO.setAddress(address);
        personInfoDTO.setAge(age);
        personInfoDTO.setEmail(email);
        personInfoDTO.setMedications(medications);
        personInfoDTO.setAllergies(allergies);

        // Assert
        assertEquals(firstName, personInfoDTO.getFirstName());
        assertEquals(lastName, personInfoDTO.getLastName());
        assertEquals(address, personInfoDTO.getAddress());
        assertEquals(age, personInfoDTO.getAge());
        assertEquals(email, personInfoDTO.getEmail());
        assertEquals(medications, personInfoDTO.getMedications());
        assertEquals(allergies, personInfoDTO.getAllergies());
    }

    @Test
    @DisplayName("Test de création avec un prénom vide")
    void testCreateWithEmptyFirstName() {
        // Arrange
        String emptyFirstName = "";
        String lastName = "Doe";
        String address = "123 Main St";
        int age = 30;
        String email = "john.doe@email.com";

        // Act
        personInfoDTO.setFirstName(emptyFirstName);
        personInfoDTO.setLastName(lastName);
        personInfoDTO.setAddress(address);
        personInfoDTO.setAge(age);
        personInfoDTO.setEmail(email);

        // Assert
        assertEquals(emptyFirstName, personInfoDTO.getFirstName());
        assertEquals(lastName, personInfoDTO.getLastName());
        assertEquals(address, personInfoDTO.getAddress());
        assertEquals(age, personInfoDTO.getAge());
        assertEquals(email, personInfoDTO.getEmail());
    }

    @Test
    @DisplayName("Test de création avec des listes vides")
    void testCreateWithEmptyLists() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String address = "123 Main St";
        int age = 30;
        String email = "john.doe@email.com";
        List<String> emptyList = List.of();

        // Act
        personInfoDTO.setFirstName(firstName);
        personInfoDTO.setLastName(lastName);
        personInfoDTO.setAddress(address);
        personInfoDTO.setAge(age);
        personInfoDTO.setEmail(email);
        personInfoDTO.setMedications(emptyList);
        personInfoDTO.setAllergies(emptyList);

        // Assert
        assertEquals(firstName, personInfoDTO.getFirstName());
        assertEquals(lastName, personInfoDTO.getLastName());
        assertEquals(address, personInfoDTO.getAddress());
        assertEquals(age, personInfoDTO.getAge());
        assertEquals(email, personInfoDTO.getEmail());
        assertTrue(personInfoDTO.getMedications().isEmpty());
        assertTrue(personInfoDTO.getAllergies().isEmpty());
    }

    @Test
    @DisplayName("Test de création avec des listes null")
    void testCreateWithNullLists() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String address = "123 Main St";
        int age = 30;
        String email = "john.doe@email.com";

        // Act
        personInfoDTO.setFirstName(firstName);
        personInfoDTO.setLastName(lastName);
        personInfoDTO.setAddress(address);
        personInfoDTO.setAge(age);
        personInfoDTO.setEmail(email);
        personInfoDTO.setMedications(null);
        personInfoDTO.setAllergies(null);

        // Assert
        assertEquals(firstName, personInfoDTO.getFirstName());
        assertEquals(lastName, personInfoDTO.getLastName());
        assertEquals(address, personInfoDTO.getAddress());
        assertEquals(age, personInfoDTO.getAge());
        assertEquals(email, personInfoDTO.getEmail());
        assertNull(personInfoDTO.getMedications());
        assertNull(personInfoDTO.getAllergies());
    }

    @Test
    @DisplayName("Test de sérialisation JSON")
    void testJsonSerialization() throws JsonProcessingException {
        // Arrange
        personInfoDTO.setFirstName("John");
        personInfoDTO.setLastName("Doe");
        personInfoDTO.setAddress("123 Main St");
        personInfoDTO.setAge(30);
        personInfoDTO.setEmail("john.doe@email.com");
        personInfoDTO.setMedications(List.of("med1", "med2"));
        personInfoDTO.setAllergies(List.of("allergy1", "allergy2"));

        // Act
        String json = objectMapper.writeValueAsString(personInfoDTO);

        // Assert
        assertTrue(json.contains("\"firstName\":\"John\""));
        assertTrue(json.contains("\"lastName\":\"Doe\""));
        assertTrue(json.contains("\"address\":\"123 Main St\""));
        assertTrue(json.contains("\"age\":30"));
        assertTrue(json.contains("\"email\":\"john.doe@email.com\""));
        assertTrue(json.contains("\"medications\":[\"med1\",\"med2\"]"));
        assertTrue(json.contains("\"allergies\":[\"allergy1\",\"allergy2\"]"));
    }

    @Test
    @DisplayName("Test de la méthode toString")
    void testToString() {
        // Arrange
        personInfoDTO.setFirstName("John");
        personInfoDTO.setLastName("Doe");
        personInfoDTO.setAddress("123 Main St");
        personInfoDTO.setAge(30);
        personInfoDTO.setEmail("john.doe@email.com");
        personInfoDTO.setMedications(List.of("med1", "med2"));
        personInfoDTO.setAllergies(List.of("allergy1", "allergy2"));

        // Act
        String toStringResult = personInfoDTO.toString();

        // Assert
        assertNotNull(toStringResult);
        assertFalse(toStringResult.isEmpty());
        assertTrue(toStringResult.contains("PersonInfoDTO(firstName=John, lastName=Doe, address=123 Main St, age=30, email=john.doe@email.com, medications=[med1, med2], allergies=[allergy1, allergy2])"));
    }
} 