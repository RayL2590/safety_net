package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.FireStationDTO;
import com.ryan.safetynet.alerts.dto.PersonDTO;
import com.ryan.safetynet.alerts.model.*;
import com.ryan.safetynet.alerts.repository.DataRepository;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service FireStationCoverageService")
class FireStationCoverageServiceTest {

    @Mock
    private DataRepository dataRepository;

    @Mock
    private FireStationService fireStationService;

    @InjectMocks
    private FireStationCoverageService fireStationCoverageService;

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
    }

    @Test
    @DisplayName("Test de récupération des personnes couvertes avec des adultes et des enfants")
    void testGetPersonsCoveredByStation_WithAdultsAndChildren() {
        // Arrange
        int stationNumber = 1;
        String address1 = "123 Main St";
        String address2 = "456 Oak St";

        when(dataRepository.getData()).thenReturn(mockData);
        when(fireStationService.existsByStationNumber(String.valueOf(stationNumber))).thenReturn(true);
        when(fireStationService.getAddressesCoveredByStation(stationNumber))
            .thenReturn(Arrays.asList(address1, address2));

        // Création des personnes
        Person adult1 = new Person("John", "Doe", address1, "Culver", "97451", "123-456-7890", "john@email.com");
        Person adult2 = new Person("Jane", "Doe", address1, "Culver", "97451", "987-654-3210", "jane@email.com");
        Person child = new Person("Bob", "Smith", address2, "Culver", "97451", "555-123-4567", "bob@email.com");
        mockPersons.addAll(Arrays.asList(adult1, adult2, child));

        // Création des dossiers médicaux
        MedicalRecord medicalRecord1 = new MedicalRecord();
        medicalRecord1.setFirstName("John");
        medicalRecord1.setLastName("Doe");
        medicalRecord1.setBirthdate(LocalDate.of(1990, 1, 1));

        MedicalRecord medicalRecord2 = new MedicalRecord();
        medicalRecord2.setFirstName("Jane");
        medicalRecord2.setLastName("Doe");
        medicalRecord2.setBirthdate(LocalDate.of(1995, 1, 1));

        MedicalRecord medicalRecord3 = new MedicalRecord();
        medicalRecord3.setFirstName("Bob");
        medicalRecord3.setLastName("Smith");
        medicalRecord3.setBirthdate(LocalDate.of(2010, 1, 1));

        mockMedicalRecords.addAll(Arrays.asList(medicalRecord1, medicalRecord2, medicalRecord3));

        // Act
        FireStationDTO result = fireStationCoverageService.getPersonsCoveredByStation(stationNumber);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getPersons().size());
        assertEquals(2, result.getAdultCount());
        assertEquals(1, result.getChildCount());

        // Vérification des informations des personnes
        List<PersonDTO> persons = result.getPersons();
        assertTrue(persons.stream().anyMatch(p -> 
            p.getFirstName().equals("John") && 
            p.getLastName().equals("Doe") &&
            p.getAddress().equals(address1) &&
            p.getPhone().equals("123-456-7890")
        ));
        assertTrue(persons.stream().anyMatch(p -> 
            p.getFirstName().equals("Jane") && 
            p.getLastName().equals("Doe") &&
            p.getAddress().equals(address1) &&
            p.getPhone().equals("987-654-3210")
        ));
        assertTrue(persons.stream().anyMatch(p -> 
            p.getFirstName().equals("Bob") && 
            p.getLastName().equals("Smith") &&
            p.getAddress().equals(address2) &&
            p.getPhone().equals("555-123-4567")
        ));
    }

    @Test
    @DisplayName("Test de récupération des personnes couvertes avec une station inexistante")
    void testGetPersonsCoveredByStation_StationNotFound() {
        // Arrange
        int stationNumber = 1;
        when(fireStationService.existsByStationNumber(String.valueOf(stationNumber))).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            fireStationCoverageService.getPersonsCoveredByStation(stationNumber)
        );
    }

    @Test
    @DisplayName("Test de récupération des personnes couvertes avec une station sans adresses")
    void testGetPersonsCoveredByStation_NoAddresses() {
        // Arrange
        int stationNumber = 1;
        when(dataRepository.getData()).thenReturn(mockData);
        when(fireStationService.existsByStationNumber(String.valueOf(stationNumber))).thenReturn(true);
        when(fireStationService.getAddressesCoveredByStation(stationNumber))
            .thenReturn(new ArrayList<>());

        // Act
        FireStationDTO result = fireStationCoverageService.getPersonsCoveredByStation(stationNumber);

        // Assert
        assertNotNull(result);
        assertTrue(result.getPersons().isEmpty());
        assertEquals(0, result.getAdultCount());
        assertEquals(0, result.getChildCount());
    }

    @Test
    @DisplayName("Test de récupération des personnes couvertes avec des adresses sans résidents")
    void testGetPersonsCoveredByStation_NoResidents() {
        // Arrange
        int stationNumber = 1;
        String address = "123 Main St";
        when(dataRepository.getData()).thenReturn(mockData);
        when(fireStationService.existsByStationNumber(String.valueOf(stationNumber))).thenReturn(true);
        when(fireStationService.getAddressesCoveredByStation(stationNumber))
            .thenReturn(Arrays.asList(address));

        // Act
        FireStationDTO result = fireStationCoverageService.getPersonsCoveredByStation(stationNumber);

        // Assert
        assertNotNull(result);
        assertTrue(result.getPersons().isEmpty());
        assertEquals(0, result.getAdultCount());
        assertEquals(0, result.getChildCount());
    }

    @Test
    @DisplayName("Test de récupération des personnes couvertes avec un dossier médical manquant")
    void testGetPersonsCoveredByStation_MissingMedicalRecord() {
        // Arrange
        int stationNumber = 1;
        String address = "123 Main St";
        when(dataRepository.getData()).thenReturn(mockData);
        when(fireStationService.existsByStationNumber(String.valueOf(stationNumber))).thenReturn(true);
        when(fireStationService.getAddressesCoveredByStation(stationNumber))
            .thenReturn(Arrays.asList(address));

        Person person = new Person("John", "Doe", address, "Culver", "97451", "123-456-7890", "john@email.com");
        mockPersons.add(person);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            fireStationCoverageService.getPersonsCoveredByStation(stationNumber)
        );
    }

    @Test
    @DisplayName("Test de récupération des personnes couvertes avec une erreur lors du traitement")
    void testGetPersonsCoveredByStation_WithError() {
        // Arrange
        int stationNumber = 1;
        when(fireStationService.existsByStationNumber(String.valueOf(stationNumber))).thenReturn(true);
        when(dataRepository.getData()).thenThrow(new RuntimeException("Erreur de base de données"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            fireStationCoverageService.getPersonsCoveredByStation(stationNumber)
        );
    }
} 