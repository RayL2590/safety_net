package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.AddressInfoDTO;
import com.ryan.safetynet.alerts.dto.FloodStationDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.model.Person;
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
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service FloodAlertService")
class FloodAlertServiceTest {

    @Mock
    private DataRepository dataRepository;

    @Mock
    private FireStationService fireStationService;

    @Mock
    private PersonService personService;

    @InjectMocks
    private FloodAlertService floodAlertService;

    private Data mockData;
    private List<MedicalRecord> mockMedicalRecords;

    @BeforeEach
    void setUp() {
        mockData = new Data();
        mockMedicalRecords = new ArrayList<>();
        mockData.setMedicalRecords(mockMedicalRecords);
    }

    @Test
    @DisplayName("Test de récupération des foyers par stations avec des résidents")
    void testGetHouseholdsByStations_WithResidents() {
        // Arrange
        List<Integer> stationNumbers = Arrays.asList(1, 2);
        List<String> addresses = Arrays.asList("123 Main St", "456 Oak St");
        
        // Configuration des mocks
        when(fireStationService.existsByStationNumber(any())).thenReturn(true);
        when(fireStationService.getAddressesCoveredByStations(stationNumbers)).thenReturn(addresses);
        
        // Création des personnes par adresse
        Map<String, List<Person>> personsByAddress = new HashMap<>();
        List<Person> residents1 = Arrays.asList(
            new Person("John", "Doe", "123 Main St", "Culver", "97451", "123-456-7890", "john@email.com"),
            new Person("Jane", "Doe", "123 Main St", "Culver", "97451", "987-654-3210", "jane@email.com")
        );
        List<Person> residents2 = Arrays.asList(
            new Person("Bob", "Smith", "456 Oak St", "Culver", "97451", "555-123-4567", "bob@email.com")
        );
        personsByAddress.put("123 Main St", residents1);
        personsByAddress.put("456 Oak St", residents2);
        when(personService.getPersonsByAddresses(addresses)).thenReturn(personsByAddress);

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

        MedicalRecord medicalRecord3 = new MedicalRecord();
        medicalRecord3.setFirstName("Bob");
        medicalRecord3.setLastName("Smith");
        medicalRecord3.setBirthdate(LocalDate.of(1985, 1, 1));
        medicalRecord3.setMedications(Arrays.asList("med4"));
        medicalRecord3.setAllergies(Arrays.asList("allergy3"));

        mockMedicalRecords.addAll(Arrays.asList(medicalRecord1, medicalRecord2, medicalRecord3));
        when(dataRepository.getData()).thenReturn(mockData);

        // Act
        FloodStationDTO result = floodAlertService.getHouseholdsByStations(stationNumbers);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getAddresses().size());

        // Vérification des adresses et de leurs résidents
        Map<String, AddressInfoDTO> addressMap = result.getAddresses().stream()
            .collect(Collectors.toMap(AddressInfoDTO::getAddress, a -> a));

        // Vérification de la première adresse
        AddressInfoDTO address1 = addressMap.get("123 Main St");
        assertNotNull(address1);
        assertEquals(2, address1.getResidents().size());

        // Vérification des résidents de la première adresse
        List<PersonWithMedicalInfoDTO> residentsInfo1 = address1.getResidents();
        assertTrue(residentsInfo1.stream().anyMatch(r -> 
            r.getFirstName().equals("John") && 
            r.getLastName().equals("Doe") &&
            r.getMedications().containsAll(Arrays.asList("med1", "med2")) &&
            r.getAllergies().contains("allergy1")
        ));
        assertTrue(residentsInfo1.stream().anyMatch(r -> 
            r.getFirstName().equals("Jane") && 
            r.getLastName().equals("Doe") &&
            r.getMedications().contains("med3") &&
            r.getAllergies().contains("allergy2")
        ));

        // Vérification de la deuxième adresse
        AddressInfoDTO address2 = addressMap.get("456 Oak St");
        assertNotNull(address2);
        assertEquals(1, address2.getResidents().size());

        // Vérification du résident de la deuxième adresse
        PersonWithMedicalInfoDTO residentInfo2 = address2.getResidents().get(0);
        assertEquals("Bob", residentInfo2.getFirstName());
        assertEquals("Smith", residentInfo2.getLastName());
        assertTrue(residentInfo2.getMedications().contains("med4"));
        assertTrue(residentInfo2.getAllergies().contains("allergy3"));
    }

    @Test
    @DisplayName("Test de récupération des foyers par stations inexistantes")
    void testGetHouseholdsByStations_NonExistentStations() {
        // Arrange
        List<Integer> stationNumbers = Arrays.asList(1, 2);
        when(fireStationService.existsByStationNumber("1")).thenReturn(false);
        when(fireStationService.existsByStationNumber("2")).thenReturn(true);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> 
            floodAlertService.getHouseholdsByStations(stationNumbers)
        );
        assertTrue(exception.getMessage().contains("Les stations suivantes n'existent pas : 1"));
    }

    @Test
    @DisplayName("Test de récupération des foyers par stations sans adresses couvertes")
    void testGetHouseholdsByStations_NoCoveredAddresses() {
        // Arrange
        List<Integer> stationNumbers = Arrays.asList(1, 2);
        when(fireStationService.existsByStationNumber(any())).thenReturn(true);
        when(fireStationService.getAddressesCoveredByStations(stationNumbers)).thenReturn(new ArrayList<>());
        when(personService.getPersonsByAddresses(anyList())).thenReturn(new HashMap<>());
        when(dataRepository.getData()).thenReturn(mockData);

        // Act
        FloodStationDTO result = floodAlertService.getHouseholdsByStations(stationNumbers);

        // Assert
        assertNotNull(result);
        assertTrue(result.getAddresses().isEmpty());
    }

    @Test
    @DisplayName("Test de récupération des foyers par stations avec des adresses sans résidents")
    void testGetHouseholdsByStations_AddressesWithoutResidents() {
        // Arrange
        List<Integer> stationNumbers = Arrays.asList(1);
        List<String> addresses = Arrays.asList("123 Main St");
        
        when(fireStationService.existsByStationNumber(any())).thenReturn(true);
        when(fireStationService.getAddressesCoveredByStations(stationNumbers)).thenReturn(addresses);
        when(personService.getPersonsByAddresses(addresses)).thenReturn(new HashMap<>());
        when(dataRepository.getData()).thenReturn(mockData);

        // Act
        FloodStationDTO result = floodAlertService.getHouseholdsByStations(stationNumbers);

        // Assert
        assertNotNull(result);
        assertTrue(result.getAddresses().isEmpty());
    }

    @Test
    @DisplayName("Test de récupération des foyers par stations avec une erreur lors du traitement")
    void testGetHouseholdsByStations_WithError() {
        // Arrange
        List<Integer> stationNumbers = Arrays.asList(1);
        when(fireStationService.existsByStationNumber(any())).thenReturn(true);
        when(fireStationService.getAddressesCoveredByStations(stationNumbers))
            .thenThrow(new RuntimeException("Erreur de base de données"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            floodAlertService.getHouseholdsByStations(stationNumbers)
        );
    }
} 