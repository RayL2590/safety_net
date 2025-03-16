package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PersonServiceTest {

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private PersonService personService;
    
    private List<Person> personList;
    private Data mockData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Création d'une liste de personnes pour les tests
        personList = new ArrayList<>();
        
        // Ajout d'une personne existante pour les tests
        Person existingPerson = new Person();
        existingPerson.setFirstName("Harry");
        existingPerson.setLastName("Potter");
        existingPerson.setAddress("4 Privet Drive");
        existingPerson.setCity("Little Whinging");
        existingPerson.setZip("12345");
        existingPerson.setPhone("123-456-7890");
        existingPerson.setEmail("harry@hogwarts.edu");
        personList.add(existingPerson);
        
        // Configuration du mock dataRepository
        mockData = new Data();
        mockData.setPersons(personList);
        
        when(dataRepository.getData()).thenReturn(mockData);
    }

    @Test
    void testAddPerson() {
        Person person = new Person("Hermione", "Granger", "1 street Harry Potter", "Poudlard", "97451", "841-874-6512", "hermione@email.com");
        Person addedPerson = personService.addPerson(person);
        
        assertNotNull(addedPerson);
        assertEquals("Hermione", addedPerson.getFirstName());
        
        // Vérifier que la personne a bien été ajoutée à la liste
        verify(dataRepository, times(1)).getData();
        assertTrue(personList.contains(person));
    }
    
    @Test
    void testGetAllPersons() {
        // Exécution
        List<Person> result = personService.getAllPersons();
        
        // Vérification
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Harry", result.get(0).getFirstName());
        assertEquals("Potter", result.get(0).getLastName());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testFindPersonByName_PersonExists() {
        // Exécution
        Optional<Person> result = personService.findPersonByName("Harry", "Potter");
        
        // Vérification
        assertTrue(result.isPresent());
        assertEquals("Harry", result.get().getFirstName());
        assertEquals("Potter", result.get().getLastName());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testFindPersonByName_PersonDoesNotExist() {
        // Exécution
        Optional<Person> result = personService.findPersonByName("Ron", "Weasley");
        
        // Vérification
        assertFalse(result.isPresent());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testUpdatePerson_PersonExists() {
        // Préparation
        Person updatedInfo = new Person();
        updatedInfo.setAddress("Hogwarts School");
        updatedInfo.setCity("Scotland");
        updatedInfo.setZip("54321");
        updatedInfo.setPhone("987-654-3210");
        updatedInfo.setEmail("harry.potter@hogwarts.edu");
        
        // Exécution
        Person result = personService.updatePerson("Harry", "Potter", updatedInfo);
        
        // Vérification
        assertNotNull(result);
        assertEquals("Harry", result.getFirstName()); // Le prénom ne change pas
        assertEquals("Potter", result.getLastName()); // Le nom ne change pas
        assertEquals("Hogwarts School", result.getAddress());
        assertEquals("Scotland", result.getCity());
        assertEquals("54321", result.getZip());
        assertEquals("987-654-3210", result.getPhone());
        assertEquals("harry.potter@hogwarts.edu", result.getEmail());
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testUpdatePerson_PersonDoesNotExist() {
        // Préparation
        Person updatedInfo = new Person();
        updatedInfo.setAddress("The Burrow");
        updatedInfo.setCity("Ottery St Catchpole");
        updatedInfo.setZip("67890");
        updatedInfo.setPhone("555-123-4567");
        updatedInfo.setEmail("ron@hogwarts.edu");
        
        // Exécution
        Person result = personService.updatePerson("Ron", "Weasley", updatedInfo);
        
        // Vérification
        assertNull(result);
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testDeletePerson_PersonExists() {
        // Exécution
        boolean result = personService.deletePerson("Harry", "Potter");
        
        // Vérification
        assertTrue(result);
        assertEquals(0, personList.size()); // La liste devrait être vide après suppression
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
    
    @Test
    void testDeletePerson_PersonDoesNotExist() {
        // Exécution
        boolean result = personService.deletePerson("Ron", "Weasley");
        
        // Vérification
        assertFalse(result);
        assertEquals(1, personList.size()); // La liste ne devrait pas changer
        
        // Vérifier que getData a été appelé
        verify(dataRepository, times(1)).getData();
    }
}