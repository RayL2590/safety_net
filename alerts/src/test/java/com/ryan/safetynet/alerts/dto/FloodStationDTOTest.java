package com.ryan.safetynet.alerts.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests du DTO FloodStationDTO")
class FloodStationDTOTest {

    private FloodStationDTO floodStationDTO;

    @BeforeEach
    void setUp() {
        floodStationDTO = new FloodStationDTO();
    }

    @Test
    @DisplayName("Test de création d'une instance avec des valeurs valides")
    void testCreateWithValidValues() {
        // Arrange
        List<AddressInfoDTO> addresses = List.of(
            createSampleAddressInfo("123 Main St"),
            createSampleAddressInfo("456 Oak Ave")
        );

        // Act
        floodStationDTO.setAddresses(addresses);

        // Assert
        assertEquals(addresses, floodStationDTO.getAddresses());
        assertEquals(2, floodStationDTO.getAddresses().size());
    }

    @Test
    @DisplayName("Test de création avec une liste d'adresses vide")
    void testCreateWithEmptyAddresses() {
        // Arrange
        List<AddressInfoDTO> emptyList = List.of();

        // Act
        floodStationDTO.setAddresses(emptyList);

        // Assert
        assertTrue(floodStationDTO.getAddresses().isEmpty());
    }

    @Test
    @DisplayName("Test de création avec une liste d'adresses null")
    void testCreateWithNullAddresses() {
        // Act
        floodStationDTO.setAddresses(null);

        // Assert
        assertNull(floodStationDTO.getAddresses());
    }

    @Test
    @DisplayName("Test de la méthode toString")
    void testToString() {
        // Arrange
        List<AddressInfoDTO> addresses = List.of(createSampleAddressInfo("123 Main St"));
        floodStationDTO.setAddresses(addresses);

        // Act
        String toStringResult = floodStationDTO.toString();

        // Assert
        assertNotNull(toStringResult);
        assertFalse(toStringResult.isEmpty());
        assertTrue(toStringResult.contains("FloodStationDTO(addresses="));
    }

    private AddressInfoDTO createSampleAddressInfo(String address) {
        AddressInfoDTO addressInfo = new AddressInfoDTO();
        addressInfo.setAddress(address);
        addressInfo.setResidents(List.of(
            createSamplePersonWithMedicalInfo("John", "Doe"),
            createSamplePersonWithMedicalInfo("Jane", "Doe")
        ));
        return addressInfo;
    }

    private PersonWithMedicalInfoDTO createSamplePersonWithMedicalInfo(String firstName, String lastName) {
        PersonWithMedicalInfoDTO person = new PersonWithMedicalInfoDTO();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setPhone("123-456-7890");
        person.setAge(30);
        person.setMedications(List.of("med1", "med2"));
        person.setAllergies(List.of("allergy1", "allergy2"));
        return person;
    }
} 