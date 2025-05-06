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

    @Test
    @DisplayName("Test de récupération des personnes par nom de famille avec plusieurs personnes")
    void testGetPersonsByLastName_MultiplePersons() {
        try (MockedStatic<MedicalRecordUtils> utils = mockStatic(MedicalRecordUtils.class)) {
            // Arrange
            String lastName = "Doe";
            
            // Première personne
            Person person1 = new Person();
            person1.setFirstName("John");
            person1.setLastName(lastName);
            person1.setAddress("123 Main St");
            person1.setEmail("john.doe@email.com");
            
            // Deuxième personne
            Person person2 = new Person();
            person2.setFirstName("Jane");
            person2.setLastName(lastName);
            person2.setAddress("456 Oak St");
            person2.setEmail("jane.doe@email.com");
            
            mockPersons.add(person1);
            mockPersons.add(person2);

            // Configuration des informations médicales
            PersonWithMedicalInfoDTO medicalInfo1 = new PersonWithMedicalInfoDTO();
            medicalInfo1.setAge(30);
            medicalInfo1.setMedications(List.of("med1"));
            medicalInfo1.setAllergies(List.of("allergy1"));

            PersonWithMedicalInfoDTO medicalInfo2 = new PersonWithMedicalInfoDTO();
            medicalInfo2.setAge(25);
            medicalInfo2.setMedications(List.of("med2"));
            medicalInfo2.setAllergies(List.of("allergy2"));

            utils.when(() -> MedicalRecordUtils.extractMedicalInfo(eq(person1), any()))
                .thenReturn(medicalInfo1);
            utils.when(() -> MedicalRecordUtils.extractMedicalInfo(eq(person2), any()))
                .thenReturn(medicalInfo2);

            // Act
            List<PersonInfoDTO> results = personInfoService.getPersonsByLastName(lastName);

            // Assert
            assertNotNull(results);
            assertEquals(2, results.size());
            
            // Vérification de la première personne
            PersonInfoDTO result1 = results.get(0);
            assertEquals("John", result1.getFirstName());
            assertEquals(lastName, result1.getLastName());
            assertEquals("123 Main St", result1.getAddress());
            assertEquals("john.doe@email.com", result1.getEmail());
            assertEquals(30, result1.getAge());
            assertEquals(List.of("med1"), result1.getMedications());
            assertEquals(List.of("allergy1"), result1.getAllergies());

            // Vérification de la deuxième personne
            PersonInfoDTO result2 = results.get(1);
            assertEquals("Jane", result2.getFirstName());
            assertEquals(lastName, result2.getLastName());
            assertEquals("456 Oak St", result2.getAddress());
            assertEquals("jane.doe@email.com", result2.getEmail());
            assertEquals(25, result2.getAge());
            assertEquals(List.of("med2"), result2.getMedications());
            assertEquals(List.of("allergy2"), result2.getAllergies());
        }
    }

    @Test
    @DisplayName("Test de récupération des personnes par nom de famille avec casse différente")
    void testGetPersonsByLastName_CaseInsensitive() {
        try (MockedStatic<MedicalRecordUtils> utils = mockStatic(MedicalRecordUtils.class)) {
            // Arrange
            String lastName = "Doe";
            Person person = new Person();
            person.setFirstName("John");
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
            List<PersonInfoDTO> results = personInfoService.getPersonsByLastName("DOE");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("John", results.get(0).getFirstName());
            assertEquals(lastName, results.get(0).getLastName());
        }
    }

    @Test
    @DisplayName("Test de récupération des personnes par nom de famille inexistant")
    void testGetPersonsByLastName_NonExisting() {
        // Act
        List<PersonInfoDTO> results = personInfoService.getPersonsByLastName("NonExisting");

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Test de récupération des personnes par nom de famille avec erreur médicale")
    void testGetPersonsByLastName_MedicalError() {
        try (MockedStatic<MedicalRecordUtils> utils = mockStatic(MedicalRecordUtils.class)) {
            // Arrange
            String lastName = "Doe";
            Person person = new Person();
            person.setFirstName("John");
            person.setLastName(lastName);
            mockPersons.add(person);

            utils.when(() -> MedicalRecordUtils.extractMedicalInfo(eq(person), any()))
                .thenThrow(new IllegalStateException("Dossier médical non trouvé"));

            // Act & Assert
            assertThrows(IllegalStateException.class, () ->
                personInfoService.getPersonsByLastName(lastName)
            );
        }
    }
} 