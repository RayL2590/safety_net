package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.service.PersonService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import com.ryan.safetynet.alerts.dto.PersonInputDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du controller PersonController")
class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    @Test
    @DisplayName("Test d'ajout d'une nouvelle personne")
    void testAddPerson() throws IOException {
        // Arrange
        PersonInputDTO inputDTO = new PersonInputDTO();
        inputDTO.setFirstName("John");
        inputDTO.setLastName("Doe");
        inputDTO.setAddress("123 Main St");
        inputDTO.setCity("Culver");
        inputDTO.setZip("97451");
        inputDTO.setPhone("555-1234");
        inputDTO.setEmail("john.doe@email.com");

        Person expectedPerson = new Person();
        expectedPerson.setFirstName("John");
        expectedPerson.setLastName("Doe");
        expectedPerson.setAddress("123 Main St");
        expectedPerson.setCity("Culver");
        expectedPerson.setZip("97451");
        expectedPerson.setPhone("555-1234");
        expectedPerson.setEmail("john.doe@email.com");

        when(personService.addPerson(any(Person.class))).thenReturn(expectedPerson);

        // Act
        ResponseEntity<Person> response = personController.addPerson(inputDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals("123 Main St", response.getBody().getAddress());
        assertEquals("Culver", response.getBody().getCity());
        assertEquals("97451", response.getBody().getZip());
        assertEquals("555-1234", response.getBody().getPhone());
        assertEquals("john.doe@email.com", response.getBody().getEmail());
    }

    @Test
    @DisplayName("Test de mise à jour d'une personne existante")
    void testUpdatePerson() throws IOException {
        // Arrange
        PersonInputDTO inputDTO = new PersonInputDTO();
        inputDTO.setFirstName("John");
        inputDTO.setLastName("Doe");
        inputDTO.setAddress("456 New St");
        inputDTO.setCity("Culver");
        inputDTO.setZip("97451");
        inputDTO.setPhone("555-5678");
        inputDTO.setEmail("john.doe.new@email.com");

        Person updatedPerson = new Person();
        updatedPerson.setFirstName("John");
        updatedPerson.setLastName("Doe");
        updatedPerson.setAddress("456 New St");
        updatedPerson.setCity("Culver");
        updatedPerson.setZip("97451");
        updatedPerson.setPhone("555-5678");
        updatedPerson.setEmail("john.doe.new@email.com");

        when(personService.updatePerson(eq("John"), eq("Doe"), any(Person.class)))
            .thenReturn(updatedPerson);

        // Act
        ResponseEntity<Person> response = personController.updatePerson(inputDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals("456 New St", response.getBody().getAddress());
        assertEquals("Culver", response.getBody().getCity());
        assertEquals("97451", response.getBody().getZip());
        assertEquals("555-5678", response.getBody().getPhone());
        assertEquals("john.doe.new@email.com", response.getBody().getEmail());
    }

    @Test
    @DisplayName("Test de mise à jour d'une personne inexistante")
    void testUpdatePerson_NotFound() throws IOException {
        // Arrange
        PersonInputDTO inputDTO = new PersonInputDTO();
        inputDTO.setFirstName("John");
        inputDTO.setLastName("Doe");
        inputDTO.setAddress("456 New St");

        when(personService.updatePerson(eq("John"), eq("Doe"), any(Person.class)))
            .thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
            personController.updatePerson(inputDTO)
        );
    }

    @Test
    @DisplayName("Test de suppression d'une personne")
    void testDeletePerson() throws IOException {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        when(personService.deletePerson(firstName, lastName)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = personController.deletePerson(firstName, lastName);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Test de suppression d'une personne inexistante")
    void testDeletePerson_NotFound() throws IOException {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        when(personService.deletePerson(firstName, lastName)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
            personController.deletePerson(firstName, lastName)
        );
    }

    @Test
    @DisplayName("Test d'ajout d'une personne avec des champs vides")
    void testAddPerson_WithEmptyFields() throws IOException {
        // Arrange
        PersonInputDTO inputDTO = new PersonInputDTO();
        inputDTO.setFirstName("John");
        inputDTO.setLastName("Doe");
        // Les autres champs sont null

        Person expectedPerson = new Person();
        expectedPerson.setFirstName("John");
        expectedPerson.setLastName("Doe");
        // Les autres champs sont null

        when(personService.addPerson(any(Person.class))).thenReturn(expectedPerson);

        // Act
        ResponseEntity<Person> response = personController.addPerson(inputDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertNull(response.getBody().getAddress());
        assertNull(response.getBody().getCity());
        assertNull(response.getBody().getZip());
        assertNull(response.getBody().getPhone());
        assertNull(response.getBody().getEmail());
    }
}
