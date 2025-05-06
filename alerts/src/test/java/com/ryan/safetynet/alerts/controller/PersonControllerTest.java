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
        expectedPerson.setFirstName(inputDTO.getFirstName());
        expectedPerson.setLastName(inputDTO.getLastName());
        expectedPerson.setAddress(inputDTO.getAddress());
        expectedPerson.setCity(inputDTO.getCity());
        expectedPerson.setZip(inputDTO.getZip());
        expectedPerson.setPhone(inputDTO.getPhone());
        expectedPerson.setEmail(inputDTO.getEmail());

        when(personService.addPerson(any(Person.class))).thenReturn(expectedPerson);

        // Act
        ResponseEntity<Person> response = personController.addPerson(inputDTO);

        // Assert
        // Vérification de la réponse globale
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), 
            "Le statut doit être CREATED (201)");
        
        // Vérification du corps de réponse
        Person createdPerson = response.getBody();
        assertNotNull(createdPerson, "Le corps de la réponse ne doit pas être null");
        
        // Vérification des propriétés
        assertEquals(inputDTO.getFirstName(), createdPerson.getFirstName(), 
            "Le prénom doit correspondre");
        assertEquals(inputDTO.getLastName(), createdPerson.getLastName(),
            "Le nom doit correspondre");
        assertEquals(inputDTO.getAddress(), createdPerson.getAddress(),
            "L'adresse doit correspondre");
        assertEquals(inputDTO.getCity(), createdPerson.getCity(),
            "La ville doit correspondre");
        assertEquals(inputDTO.getZip(), createdPerson.getZip(),
            "Le code postal doit correspondre");
        assertEquals(inputDTO.getPhone(), createdPerson.getPhone(),
            "Le téléphone doit correspondre");
        assertEquals(inputDTO.getEmail(), createdPerson.getEmail(),
            "L'email doit correspondre");
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

        Person expectedPerson = new Person();
        expectedPerson.setFirstName(inputDTO.getFirstName());
        expectedPerson.setLastName(inputDTO.getLastName());
        expectedPerson.setAddress(inputDTO.getAddress());
        expectedPerson.setCity(inputDTO.getCity());
        expectedPerson.setZip(inputDTO.getZip());
        expectedPerson.setPhone(inputDTO.getPhone());
        expectedPerson.setEmail(inputDTO.getEmail());

        when(personService.updatePerson(
            eq(inputDTO.getFirstName()), 
            eq(inputDTO.getLastName()), 
            any(Person.class)))
            .thenReturn(expectedPerson);

        // Act
        ResponseEntity<Person> response = personController.updatePerson(inputDTO);

        // Assert
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(), 
            "Le code statut doit être 200 (OK)");
        
        Person updatedPerson = response.getBody();
        assertNotNull(updatedPerson, "Le corps de la réponse ne doit pas être null");
        
        assertAll("Vérification des propriétés mises à jour",
            () -> assertEquals(inputDTO.getFirstName(), updatedPerson.getFirstName(), "Prénom incorrect"),
            () -> assertEquals(inputDTO.getLastName(), updatedPerson.getLastName(), "Nom incorrect"),
            () -> assertEquals(inputDTO.getAddress(), updatedPerson.getAddress(), "Adresse incorrecte"),
            () -> assertEquals(inputDTO.getCity(), updatedPerson.getCity(), "Ville incorrecte"),
            () -> assertEquals(inputDTO.getZip(), updatedPerson.getZip(), "Code postal incorrect"),
            () -> assertEquals(inputDTO.getPhone(), updatedPerson.getPhone(), "Téléphone incorrect"),
            () -> assertEquals(inputDTO.getEmail(), updatedPerson.getEmail(), "Email incorrect")
        );
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
        // Tous les autres champs non-requis sont intentionnellement null

        Person expectedPerson = new Person();
        expectedPerson.setFirstName(inputDTO.getFirstName());
        expectedPerson.setLastName(inputDTO.getLastName());  
        // Les autres champs devraient rester null comme dans l'input

        when(personService.addPerson(argThat(person -> 
            person.getFirstName().equals("John") && 
            person.getLastName().equals("Doe") &&
            person.getAddress() == null &&
            person.getCity() == null)))
            .thenReturn(expectedPerson);

        // Act
        ResponseEntity<Person> response = personController.addPerson(inputDTO);

        // Assert
        // Vérification de la réponse HTTP
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.CREATED, response.getStatusCode(),
            "Le statut doit être CREATED (201) pour une création valide avec champs optionnels manquants");

        // Vérification du corps de la réponse
        Person createdPerson = response.getBody();
        assertNotNull(createdPerson, "Le corps de la réponse ne doit pas être null");
        
        // Vérification des champs requis
        assertEquals("John", createdPerson.getFirstName(), 
            "Le prénom doit être conservé");
        assertEquals("Doe", createdPerson.getLastName(),
            "Le nom doit être conservé");
        
        // Vérification des champs optionnels null
        assertAll("Vérification des champs optionnels vides",
            () -> assertNull(createdPerson.getAddress(), 
                "L'adresse devrait être null quand non fournie"),
            () -> assertNull(createdPerson.getCity(), 
                "La ville devrait être null quand non fournie"),
            () -> assertNull(createdPerson.getZip(), 
                "Le code postal devrait être null quand non fournie"),
            () -> assertNull(createdPerson.getPhone(), 
                "Le téléphone devrait être null quand non fournie"),
            () -> assertNull(createdPerson.getEmail(), 
                "L'email devrait être null quand non fournie")
        );
    }

}
