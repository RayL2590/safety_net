package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.PersonInfoDTO;
import com.ryan.safetynet.alerts.service.PersonInfoService;
import com.ryan.safetynet.alerts.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PersonInfoController.class)
@Import(GlobalExceptionHandler.class)
class PersonInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonInfoService personInfoService;

    @Test
    void testGetPersonInfo_WithValidPerson() throws Exception {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        PersonInfoDTO expectedResponse = new PersonInfoDTO();
        // Configurer expectedResponse avec les données attendues
        when(personInfoService.getPersonInfo(firstName, lastName)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/personInfo")
                .param("firstName", firstName)
                .param("lastName", lastName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    void testGetPersonInfo_WithPersonNotFound() throws Exception {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        when(personInfoService.getPersonInfo(firstName, lastName)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/personInfo")
                .param("firstName", firstName)
                .param("lastName", lastName))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("La personne " + firstName + " " + lastName + " n'existe pas dans le système"));
    }

    @Test
    void testGetPersonInfo_WithEmptyFirstName() throws Exception {
        // Arrange
        String firstName = "";
        String lastName = "Doe";
        when(personInfoService.getPersonInfo(firstName, lastName)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/personInfo")
                .param("firstName", firstName)
                .param("lastName", lastName))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("La personne " + firstName + " " + lastName + " n'existe pas dans le système"));
    }

    @Test
    void testGetPersonInfo_WithEmptyLastName() throws Exception {
        // Arrange
        String firstName = "John";
        String lastName = "";
        when(personInfoService.getPersonInfo(firstName, lastName)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/personInfo")
                .param("firstName", firstName)
                .param("lastName", lastName))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("La personne " + firstName + " " + lastName + " n'existe pas dans le système"));
    }

    @Test
    void testGetPersonInfo_WithNullFields() throws Exception {
        // Arrange
        String firstName = "";
        String lastName = "";
        when(personInfoService.getPersonInfo(firstName, lastName)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/personInfo")
                .param("firstName", firstName)
                .param("lastName", lastName))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("La personne " + firstName + " " + lastName + " n'existe pas dans le système"));
    }
} 