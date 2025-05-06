package com.ryan.safetynet.alerts.dto;

import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.model.MedicalRecord;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests du DTO ChildAlertDTO")
class ChildAlertDTOTest {

    private ChildAlertDTO childAlertDTO;
    private Validator validator;

    @BeforeEach
    void setUp() {
        childAlertDTO = new ChildAlertDTO();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des valeurs valides")
    void testCreateWithValidValues() {
        // Arrange
        List<ChildDTO> children = List.of(createSampleChildDTO());
        List<HouseholdMemberDTO> householdMembers = List.of(createSampleHouseholdMemberDTO());

        // Act
        childAlertDTO.setChildren(children);
        childAlertDTO.setHouseholdMembers(householdMembers);

        // Assert
        assertEquals(children, childAlertDTO.getChildren());
        assertEquals(householdMembers, childAlertDTO.getHouseholdMembers());
        assertEquals(1, childAlertDTO.getChildren().size());
        assertEquals(1, childAlertDTO.getHouseholdMembers().size());
    }

    @Test
    @DisplayName("Test de validation avec des valeurs valides")
    void testValidationWithValidValues() {
        // Arrange
        childAlertDTO.setChildren(List.of(createSampleChildDTO()));
        childAlertDTO.setHouseholdMembers(List.of(createSampleHouseholdMemberDTO()));

        // Act
        Set<ConstraintViolation<ChildAlertDTO>> violations = validator.validate(childAlertDTO);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test avec des listes vides")
    void testWithEmptyLists() {
        // Arrange
        childAlertDTO.setChildren(List.of());
        childAlertDTO.setHouseholdMembers(List.of());

        // Act
        Set<ConstraintViolation<ChildAlertDTO>> violations = validator.validate(childAlertDTO);

        // Assert
        assertTrue(violations.isEmpty());
        assertTrue(childAlertDTO.getChildren().isEmpty());
        assertTrue(childAlertDTO.getHouseholdMembers().isEmpty());
    }

    @Test
    @DisplayName("Test avec des listes null")
    void testWithNullLists() {
        // Arrange
        childAlertDTO.setChildren(null);
        childAlertDTO.setHouseholdMembers(null);

        // Act
        Set<ConstraintViolation<ChildAlertDTO>> violations = validator.validate(childAlertDTO);

        // Assert
        assertTrue(violations.isEmpty());
        assertNull(childAlertDTO.getChildren());
        assertNull(childAlertDTO.getHouseholdMembers());
    }

    @Test
    @DisplayName("Test avec des noms contenant des accents")
    void testWithAccentedNames() {
        // Arrange
        ChildDTO child = createSampleChildDTO();
        child.setFirstName("François");
        child.setLastName("Dupont-Évêque");

        HouseholdMemberDTO member = createSampleHouseholdMemberDTO();
        member.setFirstName("José");
        member.setLastName("García-Muñoz");

        childAlertDTO.setChildren(List.of(child));
        childAlertDTO.setHouseholdMembers(List.of(member));

        // Act
        Set<ConstraintViolation<ChildAlertDTO>> violations = validator.validate(childAlertDTO);

        // Assert
        assertTrue(violations.isEmpty());
        assertEquals("François", childAlertDTO.getChildren().get(0).getFirstName());
        assertEquals("Dupont-Évêque", childAlertDTO.getChildren().get(0).getLastName());
        assertEquals("José", childAlertDTO.getHouseholdMembers().get(0).getFirstName());
        assertEquals("García-Muñoz", childAlertDTO.getHouseholdMembers().get(0).getLastName());
    }

    @Test
    @DisplayName("Test avec des listes dépassant la limite de taille")
    void testWithExceedingSizeLists() {
        // Arrange
        List<ChildDTO> children = new ArrayList<>();
        List<HouseholdMemberDTO> members = new ArrayList<>();
        
        for (int i = 0; i < 51; i++) {
            children.add(createSampleChildDTO());
            members.add(createSampleHouseholdMemberDTO());
        }

        childAlertDTO.setChildren(children);
        childAlertDTO.setHouseholdMembers(members);

        // Act
        Set<ConstraintViolation<ChildAlertDTO>> violations = validator.validate(childAlertDTO);

        // Assert
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("enfants")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("membres du foyer")));
    }

    @Test
    @DisplayName("Test de conversion depuis des entités Person")
    void testConversionFromPersons() {
        // Arrange
        Person child = new Person();
        child.setFirstName("John");
        child.setLastName("Doe");
        child.setAddress("123 Main St");

        Person adult = new Person();
        adult.setFirstName("Jane");
        adult.setLastName("Doe");
        adult.setAddress("123 Main St");

        MedicalRecord childMedicalRecord = new MedicalRecord();
        childMedicalRecord.setFirstName("John");
        childMedicalRecord.setLastName("Doe");
        childMedicalRecord.setBirthdate(LocalDate.now().minusYears(10)); // 10 ans

        MedicalRecord adultMedicalRecord = new MedicalRecord();
        adultMedicalRecord.setFirstName("Jane");
        adultMedicalRecord.setLastName("Doe");
        adultMedicalRecord.setBirthdate(LocalDate.now().minusYears(30)); // 30 ans

        // Act
        ChildAlertDTO dto = new ChildAlertDTO();
        dto.setChildren(List.of(createChildDTO(child, childMedicalRecord)));
        dto.setHouseholdMembers(List.of(createHouseholdMemberDTO(adult)));

        // Assert
        assertEquals(1, dto.getChildren().size());
        assertEquals(1, dto.getHouseholdMembers().size());
        
        ChildDTO childDTO = dto.getChildren().get(0);
        assertEquals(child.getFirstName(), childDTO.getFirstName());
        assertEquals(child.getLastName(), childDTO.getLastName());
        assertEquals(10, childDTO.getAge());

        HouseholdMemberDTO memberDTO = dto.getHouseholdMembers().get(0);
        assertEquals(adult.getFirstName(), memberDTO.getFirstName());
        assertEquals(adult.getLastName(), memberDTO.getLastName());
    }

    private ChildDTO createSampleChildDTO() {
        ChildDTO dto = new ChildDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setAge(10);
        return dto;
    }

    private HouseholdMemberDTO createSampleHouseholdMemberDTO() {
        HouseholdMemberDTO dto = new HouseholdMemberDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        return dto;
    }

    private ChildDTO createChildDTO(Person person, MedicalRecord medicalRecord) {
        ChildDTO dto = new ChildDTO();
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setAge(calculateAge(medicalRecord.getBirthdate()));
        return dto;
    }

    private HouseholdMemberDTO createHouseholdMemberDTO(Person person) {
        HouseholdMemberDTO dto = new HouseholdMemberDTO();
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        return dto;
    }

    private int calculateAge(LocalDate birthdate) {
        return java.time.Period.between(birthdate, LocalDate.now()).getYears();
    }
} 