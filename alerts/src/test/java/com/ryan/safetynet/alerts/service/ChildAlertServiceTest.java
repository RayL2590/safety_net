package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.ChildAlertDTO;
import com.ryan.safetynet.alerts.dto.ChildDTO;
import com.ryan.safetynet.alerts.dto.HouseholdMemberDTO;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service ChildAlertService")
class ChildAlertServiceTest {

    @Mock
    private DataRepository dataRepository;

    @Mock
    private PersonService personService;

    @InjectMocks
    private ChildAlertService childAlertService;

    private Data mockData;
    private List<MedicalRecord> mockMedicalRecords;
    private List<Person> mockPersons;

    @BeforeEach
    void setUp() {
        // Initialisation des données de test
        mockData = new Data();
        mockMedicalRecords = new ArrayList<>();
        mockPersons = new ArrayList<>();

        // Configuration du mock DataRepository
        when(dataRepository.getData()).thenReturn(mockData);
        mockData.setMedicalRecords(mockMedicalRecords);
    }

    @Test
    @DisplayName("Test de recherche d'enfants avec des enfants et des adultes")
    void testGetChildrenAtAddress_WithChildrenAndAdults() {
        // Arrange
        String address = "123 Main St";
        
        // Création des personnes
        Person child = new Person("John", "Doe", address, "City", "12345", "123-456-7890", "john@email.com");
        Person adult = new Person("Jane", "Doe", address, "City", "12345", "987-654-3210", "jane@email.com");
        mockPersons.add(child);
        mockPersons.add(adult);
        
        when(personService.getPersonsByAddress(address)).thenReturn(mockPersons);

        // Configuration des dossiers médicaux
        MedicalRecord childRecord = new MedicalRecord();
        childRecord.setFirstName("John");
        childRecord.setLastName("Doe");
        childRecord.setBirthdate(LocalDate.of(2010, 1, 1));
        childRecord.setMedications(new ArrayList<>());
        childRecord.setAllergies(new ArrayList<>());

        MedicalRecord adultRecord = new MedicalRecord();
        adultRecord.setFirstName("Jane");
        adultRecord.setLastName("Doe");
        adultRecord.setBirthdate(LocalDate.of(1990, 1, 1));
        adultRecord.setMedications(new ArrayList<>());
        adultRecord.setAllergies(new ArrayList<>());

        mockMedicalRecords.add(childRecord);
        mockMedicalRecords.add(adultRecord);

        try (MockedStatic<MedicalRecordUtils> utils = mockStatic(MedicalRecordUtils.class)) {
            // Configuration des retours de MedicalRecordUtils
            PersonWithMedicalInfoDTO childInfo = new PersonWithMedicalInfoDTO();
            childInfo.setAge(14);
            PersonWithMedicalInfoDTO adultInfo = new PersonWithMedicalInfoDTO();
            adultInfo.setAge(34);
            
            utils.when(() -> MedicalRecordUtils.extractMedicalInfo(eq(child), any()))
                .thenReturn(childInfo);
            utils.when(() -> MedicalRecordUtils.extractMedicalInfo(eq(adult), any()))
                .thenReturn(adultInfo);

            // Act
            ChildAlertDTO result = childAlertService.getChildrenAtAddress(address);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getChildren().size());
            assertEquals(1, result.getHouseholdMembers().size());

            ChildDTO childDTO = result.getChildren().get(0);
            assertEquals("John", childDTO.getFirstName());
            assertEquals("Doe", childDTO.getLastName());
            assertEquals(14, childDTO.getAge());

            HouseholdMemberDTO adultDTO = result.getHouseholdMembers().get(0);
            assertEquals("Jane", adultDTO.getFirstName());
            assertEquals("Doe", adultDTO.getLastName());
        }
    }

    @Test
    @DisplayName("Test de recherche d'enfants sans personne à l'adresse")
    void testGetChildrenAtAddress_NoPersons() {
        // Arrange
        String address = "123 Main St";
        when(personService.getPersonsByAddress(address)).thenReturn(Collections.emptyList());

        // Act
        ChildAlertDTO result = childAlertService.getChildrenAtAddress(address);

        // Assert
        assertNotNull(result);
        assertTrue(result.getChildren().isEmpty());
        assertTrue(result.getHouseholdMembers().isEmpty());
    }

    @Test
    @DisplayName("Test de recherche d'enfants avec uniquement des adultes")
    void testGetChildrenAtAddress_OnlyAdults() {
        // Arrange
        String address = "123 Main St";
        Person adult1 = new Person("John", "Doe", address, "City", "12345", "123-456-7890", "john@email.com");
        Person adult2 = new Person("Jane", "Doe", address, "City", "12345", "987-654-3210", "jane@email.com");
        mockPersons.add(adult1);
        mockPersons.add(adult2);
        
        when(personService.getPersonsByAddress(address)).thenReturn(mockPersons);

        // Configuration des dossiers médicaux
        MedicalRecord record1 = new MedicalRecord();
        record1.setFirstName("John");
        record1.setLastName("Doe");
        record1.setBirthdate(LocalDate.of(1990, 1, 1));
        record1.setMedications(new ArrayList<>());
        record1.setAllergies(new ArrayList<>());

        MedicalRecord record2 = new MedicalRecord();
        record2.setFirstName("Jane");
        record2.setLastName("Doe");
        record2.setBirthdate(LocalDate.of(1995, 1, 1));
        record2.setMedications(new ArrayList<>());
        record2.setAllergies(new ArrayList<>());

        mockMedicalRecords.add(record1);
        mockMedicalRecords.add(record2);

        try (MockedStatic<MedicalRecordUtils> utils = mockStatic(MedicalRecordUtils.class)) {
            // Configuration des retours de MedicalRecordUtils
            PersonWithMedicalInfoDTO info1 = new PersonWithMedicalInfoDTO();
            info1.setAge(34);
            PersonWithMedicalInfoDTO info2 = new PersonWithMedicalInfoDTO();
            info2.setAge(29);
            
            utils.when(() -> MedicalRecordUtils.extractMedicalInfo(eq(adult1), any()))
                .thenReturn(info1);
            utils.when(() -> MedicalRecordUtils.extractMedicalInfo(eq(adult2), any()))
                .thenReturn(info2);

            // Act
            ChildAlertDTO result = childAlertService.getChildrenAtAddress(address);

            // Assert
            assertNotNull(result);
            assertTrue(result.getChildren().isEmpty());
            assertEquals(2, result.getHouseholdMembers().size());
        }
    }

    @Test
    @DisplayName("Test de recherche d'enfants avec une erreur lors du traitement")
    void testGetChildrenAtAddress_WithError() {
        // Arrange
        String address = "123 Main St";
        when(dataRepository.getData()).thenThrow(new RuntimeException("Erreur de base de données"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> childAlertService.getChildrenAtAddress(address));
    }
} 