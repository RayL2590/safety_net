package com.ryan.safetynet.alerts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AlertsApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
		// Vérifie que le contexte Spring se charge correctement
	}

	@Test
	void testMainEndpointsAvailability() {
		// Test des endpoints
		String[] endpoints = {
			"/firestation",
			"/medicalRecord",
			"/person",
			"/communityEmail",
			"/childAlert",
			"/phoneAlert",
			"/fire",
			"/flood/stations",
			"/personInfo"
		};

		for (String endpoint : endpoints) {
			ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);
			assertNotEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
				"Endpoint " + endpoint + " should be available");
		}
	}

	@Test
	void testErrorHandling() {
		// Test de la gestion des erreurs pour un endpoint inexistant
		ResponseEntity<String> response = restTemplate.getForEntity("/nonexistent", String.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

}
