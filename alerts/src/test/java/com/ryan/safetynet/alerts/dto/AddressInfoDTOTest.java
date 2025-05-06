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
import java.time.Period;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests du DTO AddressInfoDTO")
class AddressInfoDTOTest {

    private AddressInfoDTO addressInfoDTO;
    private Validator validator;

    @BeforeEach
    void setUp() {
        addressInfoDTO = new AddressInfoDTO();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des valeurs valides")
    void testCreateWithValidValues() {
        // Arrange
        String address = "123 Main St";
        List<PersonWithMedicalInfoDTO> residents = List.of(
            createSamplePersonWithMedicalInfoDTO()
        );

        // Act
        addressInfoDTO.setAddress(address);
        addressInfoDTO.setResidents(residents);

        // Assert
        assertEquals(address, addressInfoDTO.getAddress());
        assertEquals(residents, addressInfoDTO.getResidents());
        assertEquals(1, addressInfoDTO.getResidents().size());
    }

    @Test
    @DisplayName("Test de validation avec des valeurs valides")
    void testValidationWithValidValues() {
        // Arrange
        addressInfoDTO.setAddress("123 Main St");
        addressInfoDTO.setResidents(List.of(createSamplePersonWithMedicalInfoDTO()));

        // Act
        Set<ConstraintViolation<AddressInfoDTO>> violations = validator.validate(addressInfoDTO);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec une adresse vide")
    void testValidationWithEmptyAddress() {
        // Arrange
        addressInfoDTO.setAddress("");
        addressInfoDTO.setResidents(List.of(createSamplePersonWithMedicalInfoDTO()));

        // Act
        Set<ConstraintViolation<AddressInfoDTO>> violations = validator.validate(addressInfoDTO);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test de validation avec des résidents null")
    void testValidationWithNullResidents() {
        // Arrange
        addressInfoDTO.setAddress("123 Main St");
        addressInfoDTO.setResidents(null);

        // Act
        Set<ConstraintViolation<AddressInfoDTO>> violations = validator.validate(addressInfoDTO);

        // Assert
        assertTrue(violations.isEmpty()); // Les résidents peuvent être null
    }

    @Test
    @DisplayName("Test de conversion depuis une entité Person")
    void testConversionFromPerson() {
        // Arrange
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("123 Main St");
        person.setCity("Culver");
        person.setZip("97451");
        person.setPhone("123-456-7890");
        person.setEmail("john.doe@email.com");

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        LocalDate birthdate = LocalDate.of(1990, 1, 1);
        medicalRecord.setBirthdate(birthdate);
        medicalRecord.setMedications(List.of("aznol:350mg"));
        medicalRecord.setAllergies(List.of("peanuts"));

        // Act
        AddressInfoDTO dto = new AddressInfoDTO();
        dto.setAddress(person.getAddress());
        dto.setResidents(List.of(createPersonWithMedicalInfoDTO(person, medicalRecord)));

        // Assert
        assertEquals(person.getAddress(), dto.getAddress());
        assertEquals(1, dto.getResidents().size());
        assertEquals(person.getFirstName(), dto.getResidents().get(0).getFirstName());
        assertEquals(person.getLastName(), dto.getResidents().get(0).getLastName());
        assertEquals(person.getPhone(), dto.getResidents().get(0).getPhone());
        assertEquals(calculateAge(birthdate), dto.getResidents().get(0).getAge());
        assertEquals(medicalRecord.getMedications(), dto.getResidents().get(0).getMedications());
        assertEquals(medicalRecord.getAllergies(), dto.getResidents().get(0).getAllergies());
    }

    private PersonWithMedicalInfoDTO createSamplePersonWithMedicalInfoDTO() {
        PersonWithMedicalInfoDTO dto = new PersonWithMedicalInfoDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPhone("123-456-7890");
        dto.setAge(33);
        dto.setMedications(List.of("aznol:350mg"));
        dto.setAllergies(List.of("peanuts"));
        return dto;
    }

    private PersonWithMedicalInfoDTO createPersonWithMedicalInfoDTO(Person person, MedicalRecord medicalRecord) {
        PersonWithMedicalInfoDTO dto = new PersonWithMedicalInfoDTO();
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setPhone(person.getPhone());
        dto.setAge(calculateAge(medicalRecord.getBirthdate()));
        dto.setMedications(medicalRecord.getMedications());
        dto.setAllergies(medicalRecord.getAllergies());
        return dto;
    }

    private int calculateAge(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears();
    }
} 