package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service PhoneAlertService")
class PhoneAlertServiceTest {

    @Mock
    private FireStationService fireStationService;

    @Mock
    private PersonService personService;

    @InjectMocks
    private PhoneAlertService phoneAlertService;

    @Test
    @DisplayName("Test de récupération des numéros de téléphone avec plusieurs personnes")
    void testGetPhoneNumbersByStation_WithMultiplePersons() {
        // Arrange
        int stationNumber = 1;
        List<String> addresses = Arrays.asList("123 Main St", "456 Oak St");
        when(fireStationService.getAddressesCoveredByStation(stationNumber)).thenReturn(addresses);

        Person person1 = new Person();
        person1.setPhone("123-456-7890");
        person1.setAddress("123 Main St");

        Person person2 = new Person();
        person2.setPhone("987-654-3210");
        person2.setAddress("123 Main St");

        Person person3 = new Person();
        person3.setPhone("555-123-4567");
        person3.setAddress("456 Oak St");

        Map<String, List<Person>> personsByAddress = Map.of(
            "123 Main St", Arrays.asList(person1, person2),
            "456 Oak St", List.of(person3)
        );
        when(personService.getPersonsByAddresses(addresses)).thenReturn(personsByAddress);

        // Act
        List<String> result = phoneAlertService.getPhoneNumbersByStation(stationNumber);

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.contains("123-456-7890"));
        assertTrue(result.contains("987-654-3210"));
        assertTrue(result.contains("555-123-4567"));
    }

    @Test
    @DisplayName("Test de récupération des numéros de téléphone avec des doublons")
    void testGetPhoneNumbersByStation_WithDuplicates() {
        // Arrange
        int stationNumber = 1;
        List<String> addresses = Arrays.asList("123 Main St", "456 Oak St");
        when(fireStationService.getAddressesCoveredByStation(stationNumber)).thenReturn(addresses);

        Person person1 = new Person();
        person1.setPhone("123-456-7890");
        person1.setAddress("123 Main St");

        Person person2 = new Person();
        person2.setPhone("123-456-7890"); // Même numéro que person1
        person2.setAddress("456 Oak St");

        Map<String, List<Person>> personsByAddress = Map.of(
            "123 Main St", List.of(person1),
            "456 Oak St", List.of(person2)
        );
        when(personService.getPersonsByAddresses(addresses)).thenReturn(personsByAddress);

        // Act
        List<String> result = phoneAlertService.getPhoneNumbersByStation(stationNumber);

        // Assert
        assertEquals(1, result.size());
        assertEquals("123-456-7890", result.get(0));
    }

    @Test
    @DisplayName("Test de récupération des numéros de téléphone sans personnes")
    void testGetPhoneNumbersByStation_NoPersons() {
        // Arrange
        int stationNumber = 1;
        List<String> addresses = Arrays.asList("123 Main St", "456 Oak St");
        when(fireStationService.getAddressesCoveredByStation(stationNumber)).thenReturn(addresses);
        when(personService.getPersonsByAddresses(addresses)).thenReturn(Map.of());

        // Act
        List<String> result = phoneAlertService.getPhoneNumbersByStation(stationNumber);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test de récupération des numéros de téléphone sans adresses couvertes")
    void testGetPhoneNumbersByStation_NoAddresses() {
        // Arrange
        int stationNumber = 1;
        List<String> addresses = List.of();
        when(fireStationService.getAddressesCoveredByStation(stationNumber)).thenReturn(addresses);
        when(personService.getPersonsByAddresses(addresses)).thenReturn(Map.of());

        // Act
        List<String> result = phoneAlertService.getPhoneNumbersByStation(stationNumber);

        // Assert
        assertTrue(result.isEmpty());
        verify(personService).getPersonsByAddresses(addresses);
    }

    @Test
    @DisplayName("Test de récupération des numéros de téléphone avec des numéros vides")
    void testGetPhoneNumbersByStation_WithEmptyPhones() {
        // Arrange
        int stationNumber = 1;
        List<String> addresses = List.of("123 Main St");
        when(fireStationService.getAddressesCoveredByStation(stationNumber)).thenReturn(addresses);

        Person person1 = new Person();
        person1.setPhone("");
        person1.setAddress("123 Main St");

        Person person2 = new Person();
        person2.setPhone(null);
        person2.setAddress("123 Main St");

        Map<String, List<Person>> personsByAddress = Map.of(
            "123 Main St", Arrays.asList(person1, person2)
        );
        when(personService.getPersonsByAddresses(addresses)).thenReturn(personsByAddress);

        // Act
        List<String> result = phoneAlertService.getPhoneNumbersByStation(stationNumber);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(""));
        assertTrue(result.contains(null));
    }
} 