package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private DataRepository dataRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private PersonService personService;

    private Data testData;
    private Person testPerson;
    private List<Person> personList;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setFirstName("John");
        testPerson.setLastName("Doe");
        testPerson.setAddress("123 Main St");
        testPerson.setCity("City");
        testPerson.setZip("12345");
        testPerson.setPhone("123-456-7890");
        testPerson.setEmail("john.doe@email.com");

        personList = new ArrayList<>();
        personList.add(testPerson);

        testData = new Data();
        testData.setPersons(personList);
    }

    @Test
    void getPersonsByAddresses_ShouldReturnPersonsGroupedByAddress() {
        // Given
        List<String> addresses = Arrays.asList("123 Main St", "456 Oak St");
        when(dataRepository.getData()).thenReturn(testData);

        // When
        Map<String, List<Person>> result = personService.getPersonsByAddresses(addresses);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("123 Main St"));
        assertEquals(1, result.get("123 Main St").size());
        assertEquals(testPerson, result.get("123 Main St").get(0));
    }

    @Test
    void getPersonsByAddress_ShouldReturnPersonsAtAddress() {
        // Given
        String address = "123 Main St";
        when(dataRepository.getData()).thenReturn(testData);

        // When
        List<Person> result = personService.getPersonsByAddress(address);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPerson, result.get(0));
    }

    @Test
    void findPersonByName_ShouldReturnPersonWhenExists() {
        // Given
        when(dataRepository.getData()).thenReturn(testData);

        // When
        Optional<Person> result = personService.findPersonByName("John", "Doe");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testPerson, result.get());
    }

    @Test
    void findPersonByName_ShouldReturnEmptyWhenNotExists() {
        // Given
        when(dataRepository.getData()).thenReturn(testData);

        // When
        Optional<Person> result = personService.findPersonByName("Jane", "Doe");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void addPerson_ShouldAddPersonSuccessfully() throws IOException {
        // Given
        Person newPerson = new Person();
        newPerson.setFirstName("Jane");
        newPerson.setLastName("Smith");
        when(dataRepository.getData()).thenReturn(testData);
        when(validator.validate(any())).thenReturn(Collections.emptySet());

        // When
        Person result = personService.addPerson(newPerson);

        // Then
        assertNotNull(result);
        assertEquals(newPerson, result);
        verify(dataRepository).saveData();
        assertEquals(2, testData.getPersons().size());
    }

    @Test
    void addPerson_ShouldThrowConstraintViolationException() throws IOException {
        // Given
        Person invalidPerson = new Person();
        Set<ConstraintViolation<Person>> violations = new HashSet<>();
        ConstraintViolation<Person> violation = mock(ConstraintViolation.class);
        violations.add(violation);
        when(validator.validate(invalidPerson)).thenReturn(violations);

        // When & Then
        assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> personService.addPerson(invalidPerson));
        verify(dataRepository, never()).saveData();
    }

    @Test
    void updatePerson_ShouldUpdateExistingPerson() throws IOException {
        // Given
        Person updatedPerson = new Person();
        updatedPerson.setAddress("456 New St");
        updatedPerson.setCity("NewCity");
        updatedPerson.setZip("54321");
        updatedPerson.setPhone("987-654-3210");
        updatedPerson.setEmail("new.email@email.com");
        when(dataRepository.getData()).thenReturn(testData);

        // When
        Person result = personService.updatePerson("John", "Doe", updatedPerson);

        // Then
        assertNotNull(result);
        assertEquals("456 New St", result.getAddress());
        assertEquals("NewCity", result.getCity());
        assertEquals("54321", result.getZip());
        assertEquals("987-654-3210", result.getPhone());
        assertEquals("new.email@email.com", result.getEmail());
        verify(dataRepository).saveData();
    }

    @Test
    void updatePerson_ShouldReturnNullWhenPersonNotFound() throws IOException {
        // Given
        Person updatedPerson = new Person();
        when(dataRepository.getData()).thenReturn(testData);

        // When
        Person result = personService.updatePerson("NonExistent", "Person", updatedPerson);

        // Then
        assertNull(result);
        verify(dataRepository, never()).saveData();
    }

    @Test
    void deletePerson_ShouldDeleteExistingPerson() throws IOException {
        // Given
        when(dataRepository.getData()).thenReturn(testData);

        // When
        boolean result = personService.deletePerson("John", "Doe");

        // Then
        assertTrue(result);
        assertTrue(testData.getPersons().isEmpty());
        verify(dataRepository).saveData();
    }

    @Test
    void deletePerson_ShouldReturnFalseWhenPersonNotFound() throws IOException {
        // Given
        when(dataRepository.getData()).thenReturn(testData);

        // When
        boolean result = personService.deletePerson("NonExistent", "Person");

        // Then
        assertFalse(result);
        assertEquals(1, testData.getPersons().size());
        verify(dataRepository, never()).saveData();
    }

    @Test
    void getAllPersons_ShouldReturnAllPersons() {
        // Given
        when(dataRepository.getData()).thenReturn(testData);

        // When
        List<Person> result = personService.getAllPersons();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPerson, result.get(0));
    }
} 