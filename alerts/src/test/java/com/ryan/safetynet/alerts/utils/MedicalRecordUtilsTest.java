package com.ryan.safetynet.alerts.utils;

import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MedicalRecordUtilsTest {

    private List<MedicalRecord> medicalRecords;
    private Person testPerson;
    private MedicalRecord testMedicalRecord;
    private LocalDate birthdate;

    @BeforeEach
    void setUp() {
        // Création d'une personne de test
        testPerson = new Person();
        testPerson.setFirstName("John");
        testPerson.setLastName("Doe");
        testPerson.setPhone("123-456-7890");

        // Création d'un dossier médical de test
        birthdate = LocalDate.of(1990, 1, 1);
        testMedicalRecord = new MedicalRecord();
        testMedicalRecord.setFirstName("John");
        testMedicalRecord.setLastName("Doe");
        testMedicalRecord.setBirthdate(birthdate);
        testMedicalRecord.setMedications(Arrays.asList("Med1", "Med2"));
        testMedicalRecord.setAllergies(Arrays.asList("Allergy1", "Allergy2"));

        // Création d'une liste de dossiers médicaux
        medicalRecords = Collections.singletonList(testMedicalRecord);
    }

    @Test
    void getBirthdate_ShouldReturnCorrectBirthdate() {
        // Given
        String firstName = "John";
        String lastName = "Doe";

        // When
        LocalDate birthdate = MedicalRecordUtils.getBirthdate(firstName, lastName, medicalRecords);

        // Then
        assertEquals(LocalDate.of(1990, 1, 1), birthdate);
    }

    @Test
    void getBirthdate_ShouldThrowException_WhenMedicalRecordNotFound() {
        // Given
        String firstName = "Jane";
        String lastName = "Smith";

        // When & Then
        assertThrows(IllegalStateException.class, () ->
            MedicalRecordUtils.getBirthdate(firstName, lastName, medicalRecords)
        );
    }

    @Test
    void extractMedicalInfo_ShouldReturnCorrectDTO() {
        // When
        PersonWithMedicalInfoDTO result = MedicalRecordUtils.extractMedicalInfo(testPerson, medicalRecords);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("123-456-7890", result.getPhone());
        
        // Calcul dynamique de l'âge attendu
        int expectedAge = Period.between(birthdate, LocalDate.now()).getYears();
        assertEquals(expectedAge, result.getAge(), 
            "L'âge calculé devrait être basé sur la période entre la date de naissance et aujourd'hui");
        
        assertEquals(2, result.getMedications().size());
        assertEquals(2, result.getAllergies().size());
        assertTrue(result.getMedications().contains("Med1"));
        assertTrue(result.getMedications().contains("Med2"));
        assertTrue(result.getAllergies().contains("Allergy1"));
        assertTrue(result.getAllergies().contains("Allergy2"));
    }

    @Test
    void extractMedicalInfo_ShouldThrowException_WhenMedicalRecordNotFound() {
        // Given
        Person person = new Person();
        person.setFirstName("Jane");
        person.setLastName("Smith");

        // When & Then
        assertThrows(IllegalStateException.class, () ->
            MedicalRecordUtils.extractMedicalInfo(person, medicalRecords)
        );
    }

    @Test
    void extractMedicalInfo_ShouldHandleEmptyMedicationsAndAllergies() {
        // Given
        MedicalRecord emptyRecord = new MedicalRecord();
        emptyRecord.setFirstName("John");
        emptyRecord.setLastName("Doe");
        emptyRecord.setBirthdate(birthdate);
        emptyRecord.setMedications(Collections.emptyList());
        emptyRecord.setAllergies(Collections.emptyList());

        List<MedicalRecord> records = Collections.singletonList(emptyRecord);

        // When
        PersonWithMedicalInfoDTO result = MedicalRecordUtils.extractMedicalInfo(testPerson, records);

        // Then
        assertNotNull(result);
        assertTrue(result.getMedications().isEmpty());
        assertTrue(result.getAllergies().isEmpty());
    }
} 