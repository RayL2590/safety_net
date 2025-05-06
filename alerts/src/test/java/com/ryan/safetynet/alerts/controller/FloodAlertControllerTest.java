package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.FloodStationDTO;
import com.ryan.safetynet.alerts.dto.ErrorResponse;
import com.ryan.safetynet.alerts.dto.AddressInfoDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.service.FloodAlertService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du controller FloodAlertController")
class FloodAlertControllerTest {

    @Mock
    private FloodAlertService floodAlertService;

    @InjectMocks
    private FloodAlertController floodAlertController;

    @Test
    @DisplayName("Test de récupération des foyers avec des stations valides")
    void testGetHouseholdsByStations_WithValidStations() {
        // Arrange
        String stations = "1,2";
        FloodStationDTO mockResponse = new FloodStationDTO();
        
        List<AddressInfoDTO> addresses = Arrays.asList(
            createSampleAddressInfo("123 Main St", Arrays.asList(
                createSamplePersonWithMedicalInfo("John", "Doe", "123-456-7890", 30, List.of("med1"), List.of("allergy1")),
                createSamplePersonWithMedicalInfo("Jane", "Doe", "123-456-7891", 28, List.of("med2"), List.of("allergy2"))
            )),
            createSampleAddressInfo("456 Oak St", Arrays.asList(
                createSamplePersonWithMedicalInfo("Bob", "Smith", "123-456-7892", 25, List.of("med3"), List.of("allergy3"))
            ))
        );
        mockResponse.setAddresses(addresses);
        
        when(floodAlertService.getHouseholdsByStations(Arrays.asList(1, 2))).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = floodAlertController.getHouseholdsByStations(stations);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof FloodStationDTO);
        
        FloodStationDTO responseBody = (FloodStationDTO) response.getBody();
        assertNotNull(responseBody, "Le corps de la réponse ne doit pas être null");
        assertEquals(2, responseBody.getAddresses().size());
        assertEquals("123 Main St", responseBody.getAddresses().get(0).getAddress());
        assertEquals(2, responseBody.getAddresses().get(0).getResidents().size());
        assertEquals("456 Oak St", responseBody.getAddresses().get(1).getAddress());
        assertEquals(1, responseBody.getAddresses().get(1).getResidents().size());
    }

    @Test
    @DisplayName("Test de récupération des foyers sans adresses trouvées")
    void testGetHouseholdsByStations_NoAddressesFound() {
        // Arrange
        String stations = "1,2";
        FloodStationDTO mockResponse = new FloodStationDTO();
        mockResponse.setAddresses(List.of());
        
        when(floodAlertService.getHouseholdsByStations(Arrays.asList(1, 2))).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = floodAlertController.getHouseholdsByStations(stations);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Test de récupération des foyers avec une station inexistante")
    void testGetHouseholdsByStations_StationNotFound() {
        // Arrange
        String stations = "1,2";
        when(floodAlertService.getHouseholdsByStations(Arrays.asList(1, 2)))
            .thenThrow(new ResourceNotFoundException("Une ou plusieurs stations n'existent pas"));

        // Act
        ResponseEntity<?> response = floodAlertController.getHouseholdsByStations(stations);

        // Assert
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), 
            "Le statut doit être NOT_FOUND (404)");
        
        Object responseBody = response.getBody();
        assertNotNull(responseBody, "Le corps de la réponse ne doit pas être null");
        assertTrue(responseBody instanceof ErrorResponse, 
            "Le corps devrait être de type ErrorResponse");
        
        ErrorResponse errorResponse = (ErrorResponse) responseBody;
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus(),
            "Le code d'erreur doit correspondre à NOT_FOUND");
        assertEquals("Une ou plusieurs stations n'existent pas", errorResponse.getMessage(),
            "Le message d'erreur doit correspondre");
    }

    @Test
    @DisplayName("Test de récupération des foyers avec un format de station invalide")
    void testGetHouseholdsByStations_InvalidStationFormat() {
        // Arrange
        String stations = "1,abc";

        // Act
        ResponseEntity<?> response = floodAlertController.getHouseholdsByStations(stations);

        // Assert
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), 
            "Le statut doit être BAD_REQUEST (400)");
        
        Object responseBody = response.getBody();
        assertNotNull(responseBody, "Le corps de la réponse ne doit pas être null");
        assertTrue(responseBody instanceof ErrorResponse, 
            "Le corps devrait être de type ErrorResponse");
        
        
        ErrorResponse errorResponse = (ErrorResponse) responseBody;
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Format de station invalide. Les stations doivent être des nombres.", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Test de récupération des foyers avec une seule station")
    void testGetHouseholdsByStations_SingleStation() {
        // Arrange
        String stations = "1";
        FloodStationDTO mockResponse = new FloodStationDTO();
        
        List<AddressInfoDTO> addresses = Arrays.asList(
            createSampleAddressInfo("123 Main St", Arrays.asList(
                createSamplePersonWithMedicalInfo("John", "Doe", "123-456-7890", 30, List.of("med1"), List.of("allergy1"))
            ))
        );
        mockResponse.setAddresses(addresses);
        
        when(floodAlertService.getHouseholdsByStations(List.of(1))).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = floodAlertController.getHouseholdsByStations(stations);

        // Assert
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(200, response.getStatusCode().value(), "Le status code doit être 200");
        
        Object responseBody = response.getBody();
        assertNotNull(responseBody, "Le corps de la réponse ne doit pas être null");
        assertTrue(responseBody instanceof FloodStationDTO, 
            "Le corps devrait être de type FloodStationDTO");
            
        FloodStationDTO floodStationDTO = (FloodStationDTO) responseBody;
        assertEquals(1, floodStationDTO.getAddresses().size(), 
            "Doit contenir 1 adresse");
        assertEquals("123 Main St", floodStationDTO.getAddresses().get(0).getAddress(), 
            "L'adresse doit correspondre");
        assertEquals(1, floodStationDTO.getAddresses().get(0).getResidents().size(),
            "Doit contenir 1 résident");
    }

    private AddressInfoDTO createSampleAddressInfo(String address, List<PersonWithMedicalInfoDTO> residents) {
        AddressInfoDTO addressInfo = new AddressInfoDTO();
        addressInfo.setAddress(address);
        addressInfo.setResidents(residents);
        return addressInfo;
    }

    private PersonWithMedicalInfoDTO createSamplePersonWithMedicalInfo(String firstName, String lastName, String phone, int age, List<String> medications, List<String> allergies) {
        PersonWithMedicalInfoDTO person = new PersonWithMedicalInfoDTO();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setPhone(phone);
        person.setAge(age);
        person.setMedications(medications);
        person.setAllergies(allergies);
        return person;
    }
} 