package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.CommunityEmailDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service CommunityEmailService")
class CommunityEmailServiceTest {

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private CommunityEmailService communityEmailService;

    private Data mockData;
    private List<Person> mockPersons;

    @BeforeEach
    void setUp() {
        mockData = new Data();
        mockPersons = new ArrayList<>();
        mockData.setPersons(mockPersons);
        when(dataRepository.getData()).thenReturn(mockData);
    }

    @Test
    @DisplayName("Test de récupération des emails avec plusieurs personnes dans la même ville")
    void testGetEmailsByCity_WithMultiplePersons() {
        // Arrange
        String city = "Paris";
        Person person1 = new Person("John", "Doe", "123 Main St", city, "12345", "123-456-7890", "john@email.com");
        Person person2 = new Person("Jane", "Doe", "123 Main St", city, "12345", "987-654-3210", "jane@email.com");
        Person person3 = new Person("Bob", "Smith", "456 Oak St", city, "12345", "555-123-4567", "bob@email.com");
        
        mockPersons.addAll(Arrays.asList(person1, person2, person3));

        // Act
        CommunityEmailDTO result = communityEmailService.getEmailsByCity(city);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getEmails().size());
        assertTrue(result.getEmails().contains("john@email.com"));
        assertTrue(result.getEmails().contains("jane@email.com"));
        assertTrue(result.getEmails().contains("bob@email.com"));
    }

    @Test
    @DisplayName("Test de récupération des emails avec des doublons")
    void testGetEmailsByCity_WithDuplicates() {
        // Arrange
        String city = "Paris";
        Person person1 = new Person("John", "Doe", "123 Main St", city, "12345", "123-456-7890", "john@email.com");
        Person person2 = new Person("Jane", "Doe", "123 Main St", city, "12345", "987-654-3210", "john@email.com"); // Même email que person1
        
        mockPersons.addAll(Arrays.asList(person1, person2));

        // Act
        CommunityEmailDTO result = communityEmailService.getEmailsByCity(city);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getEmails().size());
        assertTrue(result.getEmails().contains("john@email.com"));
    }

    @Test
    @DisplayName("Test de récupération des emails avec une ville vide")
    void testGetEmailsByCity_EmptyCity() {
        // Arrange
        String city = "";
        
        // Act
        CommunityEmailDTO result = communityEmailService.getEmailsByCity(city);

        // Assert
        assertNotNull(result);
        assertTrue(result.getEmails().isEmpty());
    }

    @Test
    @DisplayName("Test de récupération des emails avec une ville qui n'existe pas")
    void testGetEmailsByCity_NonExistentCity() {
        // Arrange
        String city = "NonExistentCity";
        Person person = new Person("John", "Doe", "123 Main St", "Paris", "12345", "123-456-7890", "john@email.com");
        mockPersons.add(person);

        // Act
        CommunityEmailDTO result = communityEmailService.getEmailsByCity(city);

        // Assert
        assertNotNull(result);
        assertTrue(result.getEmails().isEmpty());
    }

    @Test
    @DisplayName("Test de récupération des emails avec une ville en majuscules/minuscules")
    void testGetEmailsByCity_CaseInsensitive() {
        // Arrange
        String city = "PARIS";
        Person person = new Person("John", "Doe", "123 Main St", "Paris", "12345", "123-456-7890", "john@email.com");
        mockPersons.add(person);

        // Act
        CommunityEmailDTO result = communityEmailService.getEmailsByCity(city);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getEmails().size());
        assertTrue(result.getEmails().contains("john@email.com"));
    }

    @Test
    @DisplayName("Test de récupération des emails avec une erreur lors du traitement")
    void testGetEmailsByCity_WithError() {
        // Arrange
        String city = "Paris";
        when(dataRepository.getData()).thenThrow(new RuntimeException("Erreur de base de données"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> communityEmailService.getEmailsByCity(city));
    }
} 