package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.PersonInfoDTO;
import com.ryan.safetynet.alerts.service.PersonInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du controller PersonInfoController")
class PersonInfoControllerTest {

    @Mock
    private PersonInfoService personInfoService;

    @InjectMocks
    private PersonInfoController personInfoController;

    @Test
    @DisplayName("Test de récupération des informations d'une personne existante")
    void testGetPersonInfo_WithPersonFound() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        
        PersonInfoDTO expectedInfo = new PersonInfoDTO();
        expectedInfo.setFirstName(firstName);
        expectedInfo.setLastName(lastName);
        expectedInfo.setAddress("123 Main St");
        expectedInfo.setAge(30);
        expectedInfo.setEmail("john.doe@email.com");
        expectedInfo.setMedications(Arrays.asList("med1", "med2"));
        expectedInfo.setAllergies(Arrays.asList("allergy1", "allergy2"));

        when(personInfoService.getPersonInfo(firstName, lastName)).thenReturn(expectedInfo);

        // Act
        ResponseEntity<PersonInfoDTO> response = personInfoController.getPersonInfo(firstName, lastName);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals("123 Main St", response.getBody().getAddress());
        assertEquals(30, response.getBody().getAge());
        assertEquals("john.doe@email.com", response.getBody().getEmail());
        assertEquals(2, response.getBody().getMedications().size());
        assertEquals(2, response.getBody().getAllergies().size());
    }

    @Test
    @DisplayName("Test de récupération des informations d'une personne inexistante")
    void testGetPersonInfo_WithPersonNotFound() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        
        when(personInfoService.getPersonInfo(firstName, lastName)).thenReturn(null);

        // Act
        ResponseEntity<PersonInfoDTO> response = personInfoController.getPersonInfo(firstName, lastName);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Test de récupération des informations avec un prénom vide")
    void testGetPersonInfo_WithEmptyFirstName() {
        // Arrange
        String firstName = "";
        String lastName = "Doe";

        // Act
        ResponseEntity<PersonInfoDTO> response = personInfoController.getPersonInfo(firstName, lastName);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(personInfoService).getPersonInfo(firstName, lastName);
    }

    @Test
    @DisplayName("Test de récupération des informations avec un nom vide")
    void testGetPersonInfo_WithEmptyLastName() {
        // Arrange
        String firstName = "John";
        String lastName = "";

        // Act
        ResponseEntity<PersonInfoDTO> response = personInfoController.getPersonInfo(firstName, lastName);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(personInfoService).getPersonInfo(firstName, lastName);
    }

    @Test
    @DisplayName("Test de récupération des informations avec des champs null")
    void testGetPersonInfo_WithNullFields() {
        // Arrange
        String firstName = null;
        String lastName = null;

        // Act
        ResponseEntity<PersonInfoDTO> response = personInfoController.getPersonInfo(firstName, lastName);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(personInfoService).getPersonInfo(firstName, lastName);
    }
} 