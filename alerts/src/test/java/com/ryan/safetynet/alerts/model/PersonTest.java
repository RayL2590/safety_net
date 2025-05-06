package com.ryan.safetynet.alerts.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de la classe Person")
class PersonTest {

    private Person person;
    private Validator validator;

    @BeforeEach
    void setUp() {
        person = new Person();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test de création d'une instance de Person avec des valeurs valides")
    void testCreatePersonWithValidValues() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String address = "123 Main St";
        String city = "Culver";
        String zip = "97451";
        String phone = "123-456-7890";
        String email = "john.doe@email.com";

        // Act
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setAddress(address);
        person.setCity(city);
        person.setZip(zip);
        person.setPhone(phone);
        person.setEmail(email);

        // Assert
        assertEquals(firstName, person.getFirstName());
        assertEquals(lastName, person.getLastName());
        assertEquals(address, person.getAddress());
        assertEquals(city, person.getCity());
        assertEquals(zip, person.getZip());
        assertEquals(phone, person.getPhone());
        assertEquals(email, person.getEmail());
    }

    @Test
    @DisplayName("Test du constructeur avec tous les paramètres")
    void testConstructorWithAllParameters() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String address = "123 Main St";
        String city = "Culver";
        String zip = "97451";
        String phone = "123-456-7890";
        String email = "john.doe@email.com";

        // Act
        Person newPerson = new Person(firstName, lastName, address, city, zip, phone, email);

        // Assert
        assertEquals(firstName, newPerson.getFirstName());
        assertEquals(lastName, newPerson.getLastName());
        assertEquals(address, newPerson.getAddress());
        assertEquals(city, newPerson.getCity());
        assertEquals(zip, newPerson.getZip());
        assertEquals(phone, newPerson.getPhone());
        assertEquals(email, newPerson.getEmail());
    }

    @Test
    @DisplayName("Test de validation avec tous les champs vides")
    void testValidationWithAllEmptyFields() {
        // Act
        Set<ConstraintViolation<Person>> violations = validator.validate(person);

        // Assert
        assertEquals(7, violations.size()); // Tous les champs sont obligatoires
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Test de validation avec un prénom vide")
    void testValidationWithEmptyFirstName(String firstName) {
        // Arrange
        person.setFirstName(firstName);
        person.setLastName("Doe");
        person.setAddress("123 Main St");
        person.setCity("Culver");
        person.setZip("97451");
        person.setPhone("123-456-7890");
        person.setEmail("john.doe@email.com");

        // Act
        Set<ConstraintViolation<Person>> violations = validator.validate(person);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le prénom est obligatoire")));
    }

    @Test
    @DisplayName("Test de validation avec un code postal invalide")
    void testValidationWithInvalidZip() {
        // Arrange
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("123 Main St");
        person.setCity("Culver");
        person.setZip("1234"); // Code postal invalide
        person.setPhone("123-456-7890");
        person.setEmail("john.doe@email.com");

        // Act
        Set<ConstraintViolation<Person>> violations = validator.validate(person);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le code postal doit contenir 5 chiffres")));
    }

    @Test
    @DisplayName("Test de validation avec un numéro de téléphone invalide")
    void testValidationWithInvalidPhone() {
        // Arrange
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("123 Main St");
        person.setCity("Culver");
        person.setZip("97451");
        person.setPhone("1234567890"); // Format invalide
        person.setEmail("john.doe@email.com");

        // Act
        Set<ConstraintViolation<Person>> violations = validator.validate(person);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Format de téléphone invalide (ex: 123-456-7890)")));
    }

    @Test
    @DisplayName("Test de validation avec un email invalide")
    void testValidationWithInvalidEmail() {
        // Arrange
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("123 Main St");
        person.setCity("Culver");
        person.setZip("97451");
        person.setPhone("123-456-7890");
        person.setEmail("invalid-email"); // Email invalide

        // Act
        Set<ConstraintViolation<Person>> violations = validator.validate(person);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Email invalide")));
    }

    @Test
    @DisplayName("Test de la méthode toString")
    void testToString() {
        // Arrange
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("123 Main St");
        person.setCity("Culver");
        person.setZip("97451");
        person.setPhone("123-456-7890");
        person.setEmail("john.doe@email.com");

        // Act
        String toStringResult = person.toString();

        // Assert
        assertTrue(toStringResult.contains("firstName='John'"));
        assertTrue(toStringResult.contains("lastName='Doe'"));
        assertTrue(toStringResult.contains("address='123 Main St'"));
        assertTrue(toStringResult.contains("city='Culver'"));
        assertTrue(toStringResult.contains("zip='97451'"));
        assertTrue(toStringResult.contains("phone='123-456-7890'"));
        assertTrue(toStringResult.contains("email='john.doe@email.com'"));
    }

    @Test
    @DisplayName("Test de validation avec des valeurs valides")
    void testValidationWithValidValues() {
        // Arrange
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("123 Main St");
        person.setCity("Culver");
        person.setZip("97451");
        person.setPhone("123-456-7890");
        person.setEmail("john.doe@email.com");

        // Act
        Set<ConstraintViolation<Person>> violations = validator.validate(person);

        // Assert
        assertTrue(violations.isEmpty());
    }

} 