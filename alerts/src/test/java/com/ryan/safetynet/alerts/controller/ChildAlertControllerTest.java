package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.ChildAlertDTO;
import com.ryan.safetynet.alerts.dto.ChildDTO;
import com.ryan.safetynet.alerts.dto.HouseholdMemberDTO;
import com.ryan.safetynet.alerts.service.ChildAlertService;
import com.ryan.safetynet.alerts.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(ChildAlertController.class)
@Import(GlobalExceptionHandler.class)
class ChildAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChildAlertService childAlertService;

    @Test
    @DisplayName("GET /childAlert - Cas avec enfants trouvés")
    void getChildrenAtAddress_withChildren_shouldReturn200() throws Exception {
        // Given
        String address = "1509 Culver St";
        ChildAlertDTO response = new ChildAlertDTO();
        
        ChildDTO child1 = new ChildDTO();
        child1.setFirstName("John");
        child1.setLastName("Boyd");
        child1.setAge(10);
        
        ChildDTO child2 = new ChildDTO();
        child2.setFirstName("Jane");
        child2.setLastName("Boyd");
        child2.setAge(8);
        
        HouseholdMemberDTO adult = new HouseholdMemberDTO();
        adult.setFirstName("Bob");
        adult.setLastName("Boyd");

        response.setChildren(List.of(child1, child2));
        response.setHouseholdMembers(List.of(adult));

        when(childAlertService.getChildrenAtAddress(address)).thenReturn(response);

        // When/Then
        mockMvc.perform(get("/childAlert").param("address", address))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children.length()").value(2))
                .andExpect(jsonPath("$.children[0].firstName").value("John"))
                .andExpect(jsonPath("$.householdMembers.length()").value(1));
    }

    @Test
    @DisplayName("GET /childAlert - Cas sans enfants")
    void getChildrenAtAddress_noChildren_shouldReturn404() throws Exception {
        // Given
        String address = "123 Main St";
        ChildAlertDTO emptyResponse = new ChildAlertDTO();
        emptyResponse.setChildren(Collections.emptyList()); // Liste explicitement vide
        
        when(childAlertService.getChildrenAtAddress(address)).thenReturn(emptyResponse);

        // When/Then
        mockMvc.perform(get("/childAlert").param("address", address))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Aucun enfant trouvé à l'adresse " + address));
    }

    @Test
    @DisplayName("GET /childAlert - Adresse vide")
    void getChildrenAtAddress_emptyAddress_shouldReturn400() throws Exception {
        mockMvc.perform(get("/childAlert").param("address", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("L'adresse ne peut pas être vide"));
    }


    @Test
    @DisplayName("GET /childAlert - Erreur du service (doit retourner 500)")
    void getChildrenAtAddress_serviceError_shouldReturn500() throws Exception {
        // Given
        String address = "123 Error St";
        when(childAlertService.getChildrenAtAddress(address))
                .thenThrow(new RuntimeException("Database error"));

        // When/Then
        mockMvc.perform(get("/childAlert").param("address", address))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists());
    }
}
