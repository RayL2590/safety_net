package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.FireAlertDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
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
@DisplayName("Tests du service FireAlertService")
class FireAlertServiceTest {

    @Mock
    private DataRepository dataRepository;

    @Mock
    private FireStationService fireStationService;

    @InjectMocks
    private FireAlertService fireAlertService;

    private Data mockData;
    private List<Person> mockPersons;
    private List<FireStation> mockFireStations;
    private List<MedicalRecord> mockMedicalRecords;

    @BeforeEach
    void setUp() {
        mockData = new Data();
        mockPersons = new ArrayList<>();
        mockFireStations = new ArrayList<>();
        mockMedicalRecords = new ArrayList<>();
        
        mockData.setPersons(mockPersons);
        mockData.setFireStations(mockFireStations);
        mockData.setMedicalRecords(mockMedicalRecords);
        
        when(dataRepository.getData()).thenReturn(mockData);
    }

    @Test
    @DisplayName("Test de récupération des informations avec plusieurs résidents et une caserne")
    void testGetPersonsAndFireStationByAddress_WithMultipleResidents() {
        // Arrange
        String address = "123 Main St";
        String stationNumber = "1";
        
        // Création des personnes
        Person person1 = new Person("John", "Doe", address, "Culver", "97451", "123-456-7890", "john@email.com");
        Person person2 = new Person("Jane", "Doe", address, "Culver", "97451", "987-654-3210", "jane@email.com");
        mockPersons.addAll(Arrays.asList(person1, person2));

        // Création des dossiers médicaux
        MedicalRecord medicalRecord1 = new MedicalRecord();
        medicalRecord1.setFirstName("John");
        medicalRecord1.setLastName("Doe");
        medicalRecord1.setBirthdate(LocalDate.of(1990, 1, 1));
        medicalRecord1.setMedications(Arrays.asList("med1", "med2"));
        medicalRecord1.setAllergies(Arrays.asList("allergy1"));

        MedicalRecord medicalRecord2 = new MedicalRecord();
        medicalRecord2.setFirstName("Jane");
        medicalRecord2.setLastName("Doe");
        medicalRecord2.setBirthdate(LocalDate.of(1995, 1, 1));
        medicalRecord2.setMedications(Arrays.asList("med3"));
        medicalRecord2.setAllergies(Arrays.asList("allergy2"));

        mockMedicalRecords.addAll(Arrays.asList(medicalRecord1, medicalRecord2));

        // Création de la caserne
        FireStation fireStation = new FireStation();
        fireStation.setAddress(address);
        fireStation.setStation(stationNumber);
        mockFireStations.add(fireStation);

        // Configuration du mock pour la vérification de l'existence de la station
        when(fireStationService.existsByStationNumber(stationNumber)).thenReturn(true);

        // Act
        FireAlertDTO result = fireAlertService.getPersonsAndFireStationByAddress(address);

        // Assert
        assertNotNull(result);
        assertEquals(stationNumber, result.getFireStationNumber());
        assertEquals(2, result.getResidents().size());
        
        // Vérification des informations des résidents
        List<PersonWithMedicalInfoDTO> residents = result.getResidents();
        assertTrue(residents.stream().anyMatch(r -> 
            r.getFirstName().equals("John") && 
            r.getLastName().equals("Doe") &&
            r.getMedications().containsAll(Arrays.asList("med1", "med2")) &&
            r.getAllergies().contains("allergy1")
        ));
        assertTrue(residents.stream().anyMatch(r -> 
            r.getFirstName().equals("Jane") && 
            r.getLastName().equals("Doe") &&
            r.getMedications().contains("med3") &&
            r.getAllergies().contains("allergy2")
        ));
    }

    @Test
    @DisplayName("Test de récupération des informations avec une adresse sans caserne")
    void testGetPersonsAndFireStationByAddress_NoFireStation() {
        // Arrange
        String address = "123 Main St";
        Person person = new Person("John", "Doe", address, "Culver", "97451", "123-456-7890", "john@email.com");
        mockPersons.add(person);

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate(LocalDate.of(1990, 1, 1));
        medicalRecord.setMedications(Arrays.asList("med1"));
        medicalRecord.setAllergies(Arrays.asList("allergy1"));
        mockMedicalRecords.add(medicalRecord);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            fireAlertService.getPersonsAndFireStationByAddress(address)
        );
    }

    @Test
    @DisplayName("Test de récupération des informations avec une adresse sans résidents")
    void testGetPersonsAndFireStationByAddress_NoResidents() {
        // Arrange
        String address = "123 Main St";
        String stationNumber = "1";
        FireStation fireStation = new FireStation();
        fireStation.setAddress(address);
        fireStation.setStation(stationNumber);
        mockFireStations.add(fireStation);

        // Configuration du mock pour la vérification de l'existence de la station
        when(fireStationService.existsByStationNumber(stationNumber)).thenReturn(true);

        // Act
        FireAlertDTO result = fireAlertService.getPersonsAndFireStationByAddress(address);

        // Assert
        assertNotNull(result);
        assertEquals(stationNumber, result.getFireStationNumber());
        assertTrue(result.getResidents().isEmpty());
    }

    @Test
    @DisplayName("Test de récupération des informations avec une adresse vide")
    void testGetPersonsAndFireStationByAddress_EmptyAddress() {
        // Arrange
        String address = "";

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            fireAlertService.getPersonsAndFireStationByAddress(address)
        );
    }

    @Test
    @DisplayName("Test de récupération des informations avec une erreur lors du traitement")
    void testGetPersonsAndFireStationByAddress_WithError() {
        // Arrange
        String address = "123 Main St";
        when(dataRepository.getData()).thenThrow(new RuntimeException("Erreur de base de données"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            fireAlertService.getPersonsAndFireStationByAddress(address)
        );
    }

    @Test
    @DisplayName("Test de récupération des informations avec un dossier médical manquant")
    void testGetPersonsAndFireStationByAddress_MissingMedicalRecord() {
        // Arrange
        String address = "123 Main St";
        String stationNumber = "1";
        Person person = new Person("John", "Doe", address, "Culver", "97451", "123-456-7890", "john@email.com");
        mockPersons.add(person);

        FireStation fireStation = new FireStation();
        fireStation.setAddress(address);
        fireStation.setStation(stationNumber);
        mockFireStations.add(fireStation);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            fireAlertService.getPersonsAndFireStationByAddress(address)
        );
    }
} 