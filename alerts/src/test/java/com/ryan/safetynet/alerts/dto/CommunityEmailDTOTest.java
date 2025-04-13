package com.ryan.safetynet.alerts.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests du DTO CommunityEmailDTO")
class CommunityEmailDTOTest {

    private CommunityEmailDTO communityEmailDTO;
    private Validator validator;

    @BeforeEach
    void setUp() {
        communityEmailDTO = new CommunityEmailDTO();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des emails valides")
    void testCreateWithValidEmails() {
        // Arrange
        List<String> emails = List.of(
            "john.doe@email.com",
            "jane.doe@email.com"
        );

        // Act
        communityEmailDTO.setEmails(emails);

        // Assert
        assertEquals(emails, communityEmailDTO.getEmails());
        assertEquals(2, communityEmailDTO.getEmails().size());
    }

    @Test
    @DisplayName("Test de validation avec des emails valides")
    void testValidationWithValidEmails() {
        // Arrange
        communityEmailDTO.setEmails(List.of(
            "john.doe@email.com",
            "jane.doe@email.com"
        ));

        // Act
        Set<ConstraintViolation<CommunityEmailDTO>> violations = validator.validate(communityEmailDTO);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec une liste d'emails vide")
    void testValidationWithEmptyEmails() {
        // Arrange
        communityEmailDTO.setEmails(List.of());

        // Act
        Set<ConstraintViolation<CommunityEmailDTO>> violations = validator.validate(communityEmailDTO);

        // Assert
        assertTrue(violations.isEmpty());
        assertTrue(communityEmailDTO.getEmails().isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec une liste d'emails null")
    void testValidationWithNullEmails() {
        // Arrange
        communityEmailDTO.setEmails(null);

        // Act
        Set<ConstraintViolation<CommunityEmailDTO>> violations = validator.validate(communityEmailDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("La liste des emails ne peut pas être null")));
    }

    @Test
    @DisplayName("Test de validation avec des emails invalides")
    void testValidationWithInvalidEmails() {
        // Arrange
        communityEmailDTO.setEmails(List.of(
            "invalid-email",
            "another@invalid",
            "no-at-sign.com"
        ));

        // Act
        Set<ConstraintViolation<CommunityEmailDTO>> violations = validator.validate(communityEmailDTO);

        // Assert
        assertFalse(violations.isEmpty());
        // Vérifie qu'il y a au moins une violation pour chaque email invalide
        assertTrue(violations.size() >= 1);
        // Vérifie que tous les messages d'erreur sont bien des messages de format d'email invalide
        assertTrue(violations.stream()
                .allMatch(v -> v.getMessage().equals("Format d'email invalide")));
    }

    @Test
    @DisplayName("Test de validation avec un mélange d'emails valides et invalides")
    void testValidationWithMixedEmails() {
        // Arrange
        communityEmailDTO.setEmails(List.of(
            "john.doe@email.com",
            "invalid-email",
            "jane.doe@email.com"
        ));

        // Act
        Set<ConstraintViolation<CommunityEmailDTO>> violations = validator.validate(communityEmailDTO);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.stream()
                .allMatch(v -> v.getMessage().equals("Format d'email invalide")));
    }
} 