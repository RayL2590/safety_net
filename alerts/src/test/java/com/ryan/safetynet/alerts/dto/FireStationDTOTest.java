package com.ryan.safetynet.alerts.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests du DTO FireStationDTO")
class FireStationDTOTest {

    private FireStationDTO fireStationDTO;

    @BeforeEach
    void setUp() {
        fireStationDTO = new FireStationDTO();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des valeurs valides")
    void testCreateWithValidValues() {
        // Arrange
        List<PersonDTO> persons = List.of(
            createSamplePerson("John", "Doe", 30),
            createSamplePerson("Jane", "Doe", 10)
        );
        long adultCount = 1;
        long childCount = 1;

        // Act
        fireStationDTO.setPersons(persons);
        fireStationDTO.setAdultCount(adultCount);
        fireStationDTO.setChildCount(childCount);

        // Assert
        assertEquals(persons, fireStationDTO.getPersons());
        assertEquals(adultCount, fireStationDTO.getAdultCount());
        assertEquals(childCount, fireStationDTO.getChildCount());
    }

    @Test
    @DisplayName("Test de création avec une liste de personnes vide")
    void testCreateWithEmptyPersons() {
        // Arrange
        List<PersonDTO> emptyList = List.of();
        long adultCount = 0;
        long childCount = 0;

        // Act
        fireStationDTO.setPersons(emptyList);
        fireStationDTO.setAdultCount(adultCount);
        fireStationDTO.setChildCount(childCount);

        // Assert
        assertTrue(fireStationDTO.getPersons().isEmpty());
        assertEquals(0, fireStationDTO.getAdultCount());
        assertEquals(0, fireStationDTO.getChildCount());
    }

    @Test
    @DisplayName("Test de création avec une liste de personnes null")
    void testCreateWithNullPersons() {
        // Arrange
        long adultCount = 0;
        long childCount = 0;

        // Act
        fireStationDTO.setPersons(null);
        fireStationDTO.setAdultCount(adultCount);
        fireStationDTO.setChildCount(childCount);

        // Assert
        assertNull(fireStationDTO.getPersons());
        assertEquals(0, fireStationDTO.getAdultCount());
        assertEquals(0, fireStationDTO.getChildCount());
    }

    @Test
    @DisplayName("Test de création avec des compteurs négatifs")
    void testCreateWithNegativeCounts() {
        // Arrange
        List<PersonDTO> persons = List.of(createSamplePerson("John", "Doe", 30));
        long negativeAdultCount = -1;
        long negativeChildCount = -1;

        // Act
        fireStationDTO.setPersons(persons);
        fireStationDTO.setAdultCount(negativeAdultCount);
        fireStationDTO.setChildCount(negativeChildCount);

        // Assert
        assertEquals(persons, fireStationDTO.getPersons());
        assertEquals(negativeAdultCount, fireStationDTO.getAdultCount());
        assertEquals(negativeChildCount, fireStationDTO.getChildCount());
    }

    @Test
    @DisplayName("Test de la méthode toString")
    void testToString() {
        // Arrange
        List<PersonDTO> persons = List.of(createSamplePerson("John", "Doe", 30));
        fireStationDTO.setPersons(persons);
        fireStationDTO.setAdultCount(1);
        fireStationDTO.setChildCount(0);

        // Act
        String toStringResult = fireStationDTO.toString();

        // Assert
        assertTrue(toStringResult.contains("persons="));
        assertTrue(toStringResult.contains("adultCount=1"));
        assertTrue(toStringResult.contains("childCount=0"));
    }

    private PersonDTO createSamplePerson(String firstName, String lastName, int age) {
        PersonDTO person = new PersonDTO();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setAge(age);
        person.setPhone("123-456-7890");
        return person;
    }
} 