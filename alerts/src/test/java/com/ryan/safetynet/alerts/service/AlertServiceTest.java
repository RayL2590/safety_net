package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.FireStation;
import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlertServiceTest {

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private AlertService alertService;

    private List<Person> personList;
    private List<FireStation> fireStationList;
    private List<MedicalRecord> medicalRecordList;
    private Data mockData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Création des données de test
        personList = new ArrayList<>();
        fireStationList = new ArrayList<>();
        medicalRecordList = new ArrayList<>();
        mockData = new Data();

        // Ajout de personnes
        Person person1 = new Person();
        person1.setFirstName("Harry");
        person1.setLastName("Potter");
        person1.setAddress("4 Privet Drive");
        person1.setCity("Little Whinging");
        person1.setZip("12345");
        person1.setPhone("123-456-7890");
        person1.setEmail("harry@hogwarts.edu");
        personList.add(person1);

        Person person2 = new Person();
        person2.setFirstName("Hermione");
        person2.setLastName("Granger");
        person2.setAddress("4 Privet Drive");
        person2.setCity("Little Whinging");
        person2.setZip("12345");
        person2.setPhone("123-456-7891");
        person2.setEmail("hermione@hogwarts.edu");
        personList.add(person2);

        Person person3 = new Person();
        person3.setFirstName("Ron");
        person3.setLastName("Weasley");
        person3.setAddress("The Burrow");
        person3.setCity("Ottery St Catchpole");
        person3.setZip("67890");
        person3.setPhone("987-654-3210");
        person3.setEmail("ron@hogwarts.edu");
        personList.add(person3);

        // Ajout de casernes
        FireStation station1 = new FireStation();
        station1.setAddress("4 Privet Drive");
        station1.setStation("1");
        fireStationList.add(station1);

        FireStation station2 = new FireStation();
        station2.setAddress("The Burrow");
        station2.setStation("2");
        fireStationList.add(station2);

        // Ajout de dossiers médicaux
        MedicalRecord medicalRecord1 = new MedicalRecord();
        medicalRecord1.setFirstName("Harry");
        medicalRecord1.setLastName("Potter");
        medicalRecord1.setBirthdate(LocalDate.now().minusYears(15)); // 15 ans (enfant)
        medicalRecord1.setMedications(List.of("Skele-Gro"));
        medicalRecord1.setAllergies(List.of("Dementors"));
        medicalRecordList.add(medicalRecord1);

        MedicalRecord medicalRecord2 = new MedicalRecord();
        medicalRecord2.setFirstName("Hermione");
        medicalRecord2.setLastName("Granger");
        medicalRecord2.setBirthdate(LocalDate.now().minusYears(16)); // 16 ans (enfant)
        medicalRecord2.setMedications(List.of("Calming Draught"));
        medicalRecord2.setAllergies(new ArrayList<>());
        medicalRecordList.add(medicalRecord2);

        MedicalRecord medicalRecord3 = new MedicalRecord();
        medicalRecord3.setFirstName("Ron");
        medicalRecord3.setLastName("Weasley");
        medicalRecord3.setBirthdate(LocalDate.now().minusYears(25)); // 25 ans (adulte)
        medicalRecord3.setMedications(List.of("Wit-Sharpening Potion"));
        medicalRecord3.setAllergies(List.of("Spiders"));
        medicalRecordList.add(medicalRecord3);

        // Configuration du mock
        mockData.setPersons(personList);
        mockData.setFireStations(fireStationList);
        mockData.setMedicalRecords(medicalRecordList);

        when(dataRepository.getData()).thenReturn(mockData);
    }

    @Test
    void testGetPersonsCoveredByStation() {
        // Exécution
        Map<String, Object> result = alertService.getPersonsCoveredByStation(1);

        // Vérification
        assertNotNull(result);
        
        @SuppressWarnings("unchecked")
        List<Person> persons = (List<Person>) result.get("persons");
        assertNotNull(persons);
        assertEquals(2, persons.size());
        
        long adultCount = (long) result.get("adultCount");
        long childCount = (long) result.get("childCount");
        assertEquals(0, adultCount);
        assertEquals(2, childCount);
        
        // Vérifier que getData a été appelé exactement une fois
        verify(dataRepository, times(1)).getData();
    }

    @Test
    void testGetChildrenAtAddress() {
        // Exécution
        Map<String, Object> result = alertService.getChildrenAtAddress("4 Privet Drive");

        // Vérification
        assertNotNull(result);
        
        @SuppressWarnings("unchecked")
        List<Person> children = (List<Person>) result.get("children");
        assertNotNull(children);
        assertEquals(2, children.size());
        
        @SuppressWarnings("unchecked")
        List<Person> householdMembers = (List<Person>) result.get("householdMembers");
        assertNotNull(householdMembers);
        assertEquals(0, householdMembers.size());
        
        // Vérifier que getData a été appelé exactement une fois
        verify(dataRepository, times(1)).getData();
    }

    @Test
    void testGetChildrenAtAddress_NoChildren() {
        // Exécution
        Map<String, Object> result = alertService.getChildrenAtAddress("The Burrow");

        // Vérification
        assertNotNull(result);
        
        @SuppressWarnings("unchecked")
        List<Person> children = (List<Person>) result.get("children");
        assertNotNull(children);
        assertEquals(0, children.size());
        
        @SuppressWarnings("unchecked")
        List<Person> householdMembers = (List<Person>) result.get("householdMembers");
        assertNotNull(householdMembers);
        assertEquals(1, householdMembers.size());
        
        // Vérifier que getData a été appelé exactement une fois
        verify(dataRepository, times(1)).getData();
    }

    @Test
    void testGetPhoneNumbersByStation() {
        // Exécution
        List<String> result = alertService.getPhoneNumbersByStation(1);

        // Vérification
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("123-456-7890"));
        assertTrue(result.contains("123-456-7891"));
        
        // Vérifier que getData a été appelé exactement une fois
        verify(dataRepository, times(1)).getData();
    }

    @Test
    void testGetPersonsAndFireStationByAddress() {
        // Exécution
        Map<String, Object> result = alertService.getPersonsAndFireStationByAddress("4 Privet Drive");

        // Vérification
        assertNotNull(result);
        
        @SuppressWarnings("unchecked")
        List<Person> residents = (List<Person>) result.get("residents");
        assertNotNull(residents);
        assertEquals(2, residents.size());
        
        String fireStationNumber = (String) result.get("fireStationNumber");
        assertEquals("1", fireStationNumber);
        
        // Vérifier que getData a été appelé exactement une fois
        verify(dataRepository, times(1)).getData();
    }

    @Test
    void testGetPersonsAndFireStationByAddress_NoFireStation() {
        // Exécution
        Map<String, Object> result = alertService.getPersonsAndFireStationByAddress("Hogwarts School");

        // Vérification
        assertNotNull(result);
        
        @SuppressWarnings("unchecked")
        List<Person> residents = (List<Person>) result.get("residents");
        assertNotNull(residents);
        assertEquals(0, residents.size());
        
        String fireStationNumber = (String) result.get("fireStationNumber");
        assertNull(fireStationNumber);
        
        // Vérifier que getData a été appelé exactement une fois
        verify(dataRepository, times(1)).getData();
    }
}
