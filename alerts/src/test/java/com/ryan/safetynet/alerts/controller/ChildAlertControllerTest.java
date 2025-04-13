package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.ChildAlertDTO;
import com.ryan.safetynet.alerts.dto.ChildDTO;
import com.ryan.safetynet.alerts.dto.HouseholdMemberDTO;
import com.ryan.safetynet.alerts.service.ChildAlertService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du controller ChildAlertController")
class ChildAlertControllerTest {

    @Mock
    private ChildAlertService childAlertService;

    @InjectMocks
    private ChildAlertController childAlertController;

    @Test
    @DisplayName("Test de récupération des enfants avec des enfants présents")
    void testGetChildrenAtAddress_WithChildren() {
        // Arrange
        String address = "123 Main St";
        ChildAlertDTO mockResponse = new ChildAlertDTO();
        
        ChildDTO child1 = new ChildDTO();
        child1.setFirstName("John");
        child1.setLastName("Doe");
        child1.setAge(10);
        
        ChildDTO child2 = new ChildDTO();
        child2.setFirstName("Jane");
        child2.setLastName("Doe");
        child2.setAge(8);
        
        HouseholdMemberDTO adult1 = new HouseholdMemberDTO();
        adult1.setFirstName("Bob");
        adult1.setLastName("Doe");
        
        mockResponse.setChildren(Arrays.asList(child1, child2));
        mockResponse.setHouseholdMembers(List.of(adult1));
        
        when(childAlertService.getChildrenAtAddress(address)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ChildAlertDTO> response = childAlertController.getChildrenAtAddress(address);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getChildren().size());
        assertEquals(1, response.getBody().getHouseholdMembers().size());
        assertTrue(response.getBody().getChildren().contains(child1));
        assertTrue(response.getBody().getChildren().contains(child2));
        assertTrue(response.getBody().getHouseholdMembers().contains(adult1));
    }

    @Test
    @DisplayName("Test de récupération des enfants sans enfants présents")
    void testGetChildrenAtAddress_NoChildren() {
        // Arrange
        String address = "123 Main St";
        ChildAlertDTO mockResponse = new ChildAlertDTO();
        mockResponse.setChildren(List.of());
        mockResponse.setHouseholdMembers(List.of());
        
        when(childAlertService.getChildrenAtAddress(address)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ChildAlertDTO> response = childAlertController.getChildrenAtAddress(address);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Test de récupération des enfants avec une adresse vide")
    void testGetChildrenAtAddress_EmptyAddress() {
        // Arrange
        String address = "";
        ChildAlertDTO mockResponse = new ChildAlertDTO();
        mockResponse.setChildren(List.of());
        mockResponse.setHouseholdMembers(List.of());
        
        when(childAlertService.getChildrenAtAddress(address)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ChildAlertDTO> response = childAlertController.getChildrenAtAddress(address);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Test de récupération des enfants avec une erreur du service")
    void testGetChildrenAtAddress_WithServiceError() {
        // Arrange
        String address = "123 Main St";
        when(childAlertService.getChildrenAtAddress(address))
            .thenThrow(new RuntimeException("Erreur du service"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
            childAlertController.getChildrenAtAddress(address)
        );
    }
} 