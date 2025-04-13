package com.ryan.safetynet.alerts.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests du DTO PersonDTO")
class PersonDTOTest {

    private PersonDTO personDTO;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        personDTO = new PersonDTO();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des valeurs valides")
    void testCreateWithValidValues() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String address = "123 Main St";
        String phone = "123-456-7890";
        Integer age = 30;

        // Act
        personDTO.setFirstName(firstName);
        personDTO.setLastName(lastName);
        personDTO.setAddress(address);
        personDTO.setPhone(phone);
        personDTO.setAge(age);

        // Assert
        assertEquals(firstName, personDTO.getFirstName());
        assertEquals(lastName, personDTO.getLastName());
        assertEquals(address, personDTO.getAddress());
        assertEquals(phone, personDTO.getPhone());
        assertEquals(age, personDTO.getAge());
    }

    @Test
    @DisplayName("Test de création avec un prénom vide")
    void testCreateWithEmptyFirstName() {
        // Arrange
        String emptyFirstName = "";
        String lastName = "Doe";
        String address = "123 Main St";
        String phone = "123-456-7890";
        Integer age = 30;

        // Act
        personDTO.setFirstName(emptyFirstName);
        personDTO.setLastName(lastName);
        personDTO.setAddress(address);
        personDTO.setPhone(phone);
        personDTO.setAge(age);

        // Assert
        assertEquals(emptyFirstName, personDTO.getFirstName());
        assertEquals(lastName, personDTO.getLastName());
        assertEquals(address, personDTO.getAddress());
        assertEquals(phone, personDTO.getPhone());
        assertEquals(age, personDTO.getAge());
    }

    @Test
    @DisplayName("Test de création avec un âge null")
    void testCreateWithNullAge() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String address = "123 Main St";
        String phone = "123-456-7890";

        // Act
        personDTO.setFirstName(firstName);
        personDTO.setLastName(lastName);
        personDTO.setAddress(address);
        personDTO.setPhone(phone);
        personDTO.setAge(null);

        // Assert
        assertEquals(firstName, personDTO.getFirstName());
        assertEquals(lastName, personDTO.getLastName());
        assertEquals(address, personDTO.getAddress());
        assertEquals(phone, personDTO.getPhone());
        assertNull(personDTO.getAge());
    }

    @Test
    @DisplayName("Test de sérialisation JSON")
    void testJsonSerialization() throws JsonProcessingException {
        // Arrange
        personDTO.setFirstName("John");
        personDTO.setLastName("Doe");
        personDTO.setAddress("123 Main St");
        personDTO.setPhone("123-456-7890");
        personDTO.setAge(30);

        // Act
        String json = objectMapper.writeValueAsString(personDTO);

        // Assert
        assertTrue(json.contains("\"firstName\":\"John\""));
        assertTrue(json.contains("\"lastName\":\"Doe\""));
        assertTrue(json.contains("\"address\":\"123 Main St\""));
        assertTrue(json.contains("\"phone\":\"123-456-7890\""));
        assertTrue(json.contains("\"age\":30"));
    }

    @Test
    @DisplayName("Test de sérialisation JSON avec un âge null")
    void testJsonSerializationWithNullAge() throws JsonProcessingException {
        // Arrange
        personDTO.setFirstName("John");
        personDTO.setLastName("Doe");
        personDTO.setAddress("123 Main St");
        personDTO.setPhone("123-456-7890");
        personDTO.setAge(null);

        // Act
        String json = objectMapper.writeValueAsString(personDTO);

        // Assert
        assertTrue(json.contains("\"firstName\":\"John\""));
        assertTrue(json.contains("\"lastName\":\"Doe\""));
        assertTrue(json.contains("\"address\":\"123 Main St\""));
        assertTrue(json.contains("\"phone\":\"123-456-7890\""));
        assertTrue(json.contains("\"age\":null"));
    }

    @Test
    @DisplayName("Test de la méthode toString")
    void testToString() {
        // Arrange
        personDTO.setFirstName("John");
        personDTO.setLastName("Doe");
        personDTO.setAddress("123 Main St");
        personDTO.setPhone("123-456-7890");
        personDTO.setAge(30);

        // Act
        String toStringResult = personDTO.toString();

        // Assert
        assertNotNull(toStringResult);
        assertFalse(toStringResult.isEmpty());
        assertTrue(toStringResult.contains("PersonDTO"));
        assertTrue(toStringResult.contains("firstName=John"));
        assertTrue(toStringResult.contains("lastName=Doe"));
        assertTrue(toStringResult.contains("address=123 Main St"));
        assertTrue(toStringResult.contains("phone=123-456-7890"));
        assertTrue(toStringResult.contains("age=30"));
    }
} 