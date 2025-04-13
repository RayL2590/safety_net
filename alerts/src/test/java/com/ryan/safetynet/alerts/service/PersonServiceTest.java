package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service PersonService")
class PersonServiceTest {

    @Mock
    private DataRepository dataRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private PersonService personService;

    private Data mockData;
    private List<Person> mockPersons;

    @BeforeEach
    void setUp() {
        mockData = new Data();
        mockPersons = new ArrayList<>();
        mockData.setPersons(mockPersons);
    }

    @Test
    @DisplayName("Test de récupération des personnes par adresses")
    void testGetPersonsByAddresses() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String address1 = "123 Main St";
        String address2 = "456 Oak St";
        List<String> addresses = List.of(address1, address2);

        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Doe");
        person1.setAddress(address1);

        Person person2 = new Person();
        person2.setFirstName("Jane");
        person2.setLastName("Doe");
        person2.setAddress(address1);

        Person person3 = new Person();
        person3.setFirstName("Bob");
        person3.setLastName("Smith");
        person3.setAddress(address2);

        mockPersons.addAll(List.of(person1, person2, person3));

        // Act
        Map<String, List<Person>> result = personService.getPersonsByAddresses(addresses);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey(address1));
        assertTrue(result.containsKey(address2));
        assertEquals(2, result.get(address1).size());
        assertEquals(1, result.get(address2).size());
    }

    @Test
    @DisplayName("Test de récupération des personnes par adresse")
    void testGetPersonsByAddress() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String address = "123 Main St";
        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Doe");
        person1.setAddress(address);

        Person person2 = new Person();
        person2.setFirstName("Jane");
        person2.setLastName("Doe");
        person2.setAddress(address);

        Person person3 = new Person();
        person3.setFirstName("Bob");
        person3.setLastName("Smith");
        person3.setAddress("456 Oak St");

        mockPersons.addAll(List.of(person1, person2, person3));

        // Act
        List<Person> result = personService.getPersonsByAddress(address);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(person1));
        assertTrue(result.contains(person2));
        assertFalse(result.contains(person3));
    }

    @Test
    @DisplayName("Test de recherche d'une personne existante")
    void testFindPersonByName_Existing() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        mockPersons.add(person);

        // Act
        Optional<Person> result = personService.findPersonByName(firstName, lastName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(person, result.get());
    }

    @Test
    @DisplayName("Test de recherche d'une personne inexistante")
    void testFindPersonByName_NonExisting() {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";

        // Act
        Optional<Person> result = personService.findPersonByName(firstName, lastName);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test d'ajout d'une personne valide")
    void testAddPerson_Valid() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("123 Main St");
        person.setCity("City");
        person.setZip("12345");
        person.setPhone("123-456-7890");
        person.setEmail("john.doe@email.com");

        when(validator.validate(person)).thenReturn(Collections.emptySet());

        // Act
        Person result = personService.addPerson(person);

        // Assert
        assertEquals(person, result);
        assertTrue(mockPersons.contains(person));
        verify(dataRepository).saveData();
    }

    @Test
    @DisplayName("Test d'ajout d'une personne invalide")
    @SuppressWarnings("unchecked")
    void testAddPerson_Invalid() {
        // Arrange
        Person person = new Person();
        Set<ConstraintViolation<Person>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));
        when(validator.validate(person)).thenReturn(violations);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () ->
            personService.addPerson(person)
        );
    }

    @Test
    @DisplayName("Test de mise à jour d'une personne existante")
    void testUpdatePerson_Existing() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";
        Person existingPerson = new Person();
        existingPerson.setFirstName(firstName);
        existingPerson.setLastName(lastName);
        existingPerson.setAddress("123 Main St");
        mockPersons.add(existingPerson);

        Person updatedPerson = new Person();
        updatedPerson.setFirstName(firstName);
        updatedPerson.setLastName(lastName);
        updatedPerson.setAddress("456 Oak St");
        updatedPerson.setCity("New City");
        updatedPerson.setZip("54321");
        updatedPerson.setPhone("987-654-3210");
        updatedPerson.setEmail("john.doe@newemail.com");

        // Act
        Person result = personService.updatePerson(firstName, lastName, updatedPerson);

        // Assert
        assertNotNull(result);
        assertEquals("456 Oak St", result.getAddress());
        assertEquals("New City", result.getCity());
        assertEquals("54321", result.getZip());
        assertEquals("987-654-3210", result.getPhone());
        assertEquals("john.doe@newemail.com", result.getEmail());
        verify(dataRepository).saveData();
    }

    @Test
    @DisplayName("Test de mise à jour d'une personne inexistante")
    void testUpdatePerson_NonExisting() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";
        Person updatedPerson = new Person();
        updatedPerson.setFirstName(firstName);
        updatedPerson.setLastName(lastName);
        updatedPerson.setAddress("456 Oak St");

        // Act
        Person result = personService.updatePerson(firstName, lastName, updatedPerson);

        // Assert
        assertNull(result);
        verify(dataRepository, never()).saveData();
    }

    @Test
    @DisplayName("Test de suppression d'une personne existante")
    void testDeletePerson_Existing() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        mockPersons.add(person);

        // Act
        boolean result = personService.deletePerson(firstName, lastName);

        // Assert
        assertTrue(result);
        assertTrue(mockPersons.isEmpty());
        verify(dataRepository).saveData();
    }

    @Test
    @DisplayName("Test de suppression d'une personne inexistante")
    void testDeletePerson_NonExisting() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";

        // Act
        boolean result = personService.deletePerson(firstName, lastName);

        // Assert
        assertFalse(result);
        verify(dataRepository, never()).saveData();
    }

    @Test
    @DisplayName("Test de suppression d'une personne avec erreur de sauvegarde")
    void testDeletePerson_WithSaveError() throws IOException {
        // Arrange
        when(dataRepository.getData()).thenReturn(mockData);
        String firstName = "John";
        String lastName = "Doe";
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        mockPersons.add(person);
        doThrow(new RuntimeException("Erreur de sauvegarde")).when(dataRepository).saveData();

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
            personService.deletePerson(firstName, lastName)
        );
    }
}