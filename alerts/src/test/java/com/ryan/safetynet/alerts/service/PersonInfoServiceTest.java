package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.PersonInfoDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import com.ryan.safetynet.alerts.utils.MedicalRecordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service PersonInfoService")
class PersonInfoServiceTest {

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private PersonInfoService personInfoService;

    private Data mockData;
    private List<Person> mockPersons;
    private List<MedicalRecord> mockMedicalRecords;

    @BeforeEach
    void setUp() {
        mockData = new Data();
        mockPersons = new ArrayList<>();
        mockMedicalRecords = new ArrayList<>();
        mockData.setPersons(mockPersons);
        mockData.setMedicalRecords(mockMedicalRecords);
        when(dataRepository.getData()).thenReturn(mockData);
    }

    @Test
    @DisplayName("Test de récupération des informations d'une personne existante")
    void testGetPersonInfo_ExistingPerson() {
        try (MockedStatic<MedicalRecordUtils> utils = mockStatic(MedicalRecordUtils.class)) {
            // Arrange
            String firstName = "John";
            String lastName = "Doe";
            Person person = new Person();
            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setAddress("123 Main St");
            person.setEmail("john.doe@email.com");
            mockPersons.add(person);

            PersonWithMedicalInfoDTO medicalInfo = new PersonWithMedicalInfoDTO();
            medicalInfo.setAge(30);
            medicalInfo.setMedications(List.of("med1", "med2"));
            medicalInfo.setAllergies(List.of("allergy1", "allergy2"));
            utils.when(() -> MedicalRecordUtils.extractMedicalInfo(eq(person), any()))
                .thenReturn(medicalInfo);

            // Act
            PersonInfoDTO result = personInfoService.getPersonInfo(firstName, lastName);

            // Assert
            assertNotNull(result);
            assertEquals(firstName, result.getFirstName());
            assertEquals(lastName, result.getLastName());
            assertEquals("123 Main St", result.getAddress());
            assertEquals("john.doe@email.com", result.getEmail());
            assertEquals(30, result.getAge());
            assertEquals(List.of("med1", "med2"), result.getMedications());
            assertEquals(List.of("allergy1", "allergy2"), result.getAllergies());
        }
    }

    @Test
    @DisplayName("Test de récupération des informations d'une personne inexistante")
    void testGetPersonInfo_NonExistingPerson() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";

        // Act
        PersonInfoDTO result = personInfoService.getPersonInfo(firstName, lastName);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Test de récupération des informations d'une personne avec casse différente")
    void testGetPersonInfo_CaseInsensitive() {
        try (MockedStatic<MedicalRecordUtils> utils = mockStatic(MedicalRecordUtils.class)) {
            // Arrange
            String firstName = "John";
            String lastName = "Doe";
            Person person = new Person();
            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setAddress("123 Main St");
            person.setEmail("john.doe@email.com");
            mockPersons.add(person);

            PersonWithMedicalInfoDTO medicalInfo = new PersonWithMedicalInfoDTO();
            medicalInfo.setAge(30);
            medicalInfo.setMedications(List.of("med1"));
            medicalInfo.setAllergies(List.of("allergy1"));
            utils.when(() -> MedicalRecordUtils.extractMedicalInfo(eq(person), any()))
                .thenReturn(medicalInfo);

            // Act
            PersonInfoDTO result = personInfoService.getPersonInfo("JOHN", "DOE");

            // Assert
            assertNotNull(result);
            assertEquals(firstName, result.getFirstName());
            assertEquals(lastName, result.getLastName());
        }
    }

    @Test
    @DisplayName("Test de récupération des informations d'une personne sans dossier médical")
    void testGetPersonInfo_NoMedicalRecord() {
        try (MockedStatic<MedicalRecordUtils> utils = mockStatic(MedicalRecordUtils.class)) {
            // Arrange
            String firstName = "John";
            String lastName = "Doe";
            Person person = new Person();
            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setAddress("123 Main St");
            person.setEmail("john.doe@email.com");
            mockPersons.add(person);

            utils.when(() -> MedicalRecordUtils.extractMedicalInfo(eq(person), any()))
                .thenThrow(new IllegalStateException("Dossier médical non trouvé"));

            // Act & Assert
            assertThrows(IllegalStateException.class, () ->
                personInfoService.getPersonInfo(firstName, lastName)
            );
        }
    }
} 