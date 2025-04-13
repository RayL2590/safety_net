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

@DisplayName("Tests du DTO PersonInputDTO")
class PersonInputDTOTest {

    private PersonInputDTO personInputDTO;
    private Validator validator;

    @BeforeEach
    void setUp() {
        personInputDTO = new PersonInputDTO();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des valeurs valides")
    void testCreateWithValidValues() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String address = "123 Main St";
        String city = "Paris";
        String zip = "75000";
        String phone = "123-456-7890";
        String email = "john.doe@email.com";

        // Act
        personInputDTO.setFirstName(firstName);
        personInputDTO.setLastName(lastName);
        personInputDTO.setAddress(address);
        personInputDTO.setCity(city);
        personInputDTO.setZip(zip);
        personInputDTO.setPhone(phone);
        personInputDTO.setEmail(email);

        // Assert
        assertEquals(firstName, personInputDTO.getFirstName());
        assertEquals(lastName, personInputDTO.getLastName());
        assertEquals(address, personInputDTO.getAddress());
        assertEquals(city, personInputDTO.getCity());
        assertEquals(zip, personInputDTO.getZip());
        assertEquals(phone, personInputDTO.getPhone());
        assertEquals(email, personInputDTO.getEmail());
    }

    @Test
    @DisplayName("Test de validation avec des valeurs valides")
    void testValidationWithValidValues() {
        // Arrange
        personInputDTO.setFirstName("John");
        personInputDTO.setLastName("Doe");
        personInputDTO.setAddress("123 Main St");
        personInputDTO.setCity("Paris");
        personInputDTO.setZip("75000");
        personInputDTO.setPhone("123-456-7890");
        personInputDTO.setEmail("john.doe@email.com");

        // Act
        Set<ConstraintViolation<PersonInputDTO>> violations = validator.validate(personInputDTO);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec un prénom vide")
    void testValidationWithEmptyFirstName() {
        // Arrange
        personInputDTO.setFirstName("");
        personInputDTO.setLastName("Doe");
        personInputDTO.setAddress("123 Main St");
        personInputDTO.setCity("Paris");
        personInputDTO.setZip("75000");
        personInputDTO.setPhone("123-456-7890");
        personInputDTO.setEmail("john.doe@email.com");

        // Act
        Set<ConstraintViolation<PersonInputDTO>> violations = validator.validate(personInputDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le prénom est obligatoire")));
    }

    @Test
    @DisplayName("Test de validation avec un code postal invalide")
    void testValidationWithInvalidZip() {
        // Arrange
        personInputDTO.setFirstName("John");
        personInputDTO.setLastName("Doe");
        personInputDTO.setAddress("123 Main St");
        personInputDTO.setCity("Paris");
        personInputDTO.setZip("75A00"); // Code postal invalide
        personInputDTO.setPhone("123-456-7890");
        personInputDTO.setEmail("john.doe@email.com");

        // Act
        Set<ConstraintViolation<PersonInputDTO>> violations = validator.validate(personInputDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le code postal doit contenir 5 chiffres")));
    }

    @Test
    @DisplayName("Test de validation avec un numéro de téléphone invalide")
    void testValidationWithInvalidPhone() {
        // Arrange
        personInputDTO.setFirstName("John");
        personInputDTO.setLastName("Doe");
        personInputDTO.setAddress("123 Main St");
        personInputDTO.setCity("Paris");
        personInputDTO.setZip("75000");
        personInputDTO.setPhone("1234567890"); // Format invalide
        personInputDTO.setEmail("john.doe@email.com");

        // Act
        Set<ConstraintViolation<PersonInputDTO>> violations = validator.validate(personInputDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le numéro de téléphone doit être au format XXX-XXX-XXXX")));
    }

    @Test
    @DisplayName("Test de validation avec un email invalide")
    void testValidationWithInvalidEmail() {
        // Arrange
        personInputDTO.setFirstName("John");
        personInputDTO.setLastName("Doe");
        personInputDTO.setAddress("123 Main St");
        personInputDTO.setCity("Paris");
        personInputDTO.setZip("75000");
        personInputDTO.setPhone("123-456-7890");
        personInputDTO.setEmail("invalid-email"); // Email invalide

        // Act
        Set<ConstraintViolation<PersonInputDTO>> violations = validator.validate(personInputDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("L'email doit être valide")));
    }

    @Test
    @DisplayName("Test de la méthode toString")
    void testToString() {
        // Arrange
        personInputDTO.setFirstName("John");
        personInputDTO.setLastName("Doe");
        personInputDTO.setAddress("123 Main St");
        personInputDTO.setCity("Paris");
        personInputDTO.setZip("75000");
        personInputDTO.setPhone("123-456-7890");
        personInputDTO.setEmail("john.doe@email.com");

        // Act
        String toStringResult = personInputDTO.toString();

        // Assert
        assertNotNull(toStringResult);
        assertFalse(toStringResult.isEmpty());
        assertTrue(toStringResult.contains("PersonInputDTO(firstName=John, lastName=Doe, address=123 Main St, city=Paris, zip=75000, phone=123-456-7890, email=john.doe@email.com)"));
    }
} 