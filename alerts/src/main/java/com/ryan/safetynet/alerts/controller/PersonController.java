package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.service.PersonService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import com.ryan.safetynet.alerts.dto.PersonInputDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * Contrôleur gérant les opérations CRUD sur les personnes.
 * Ce contrôleur permet d'ajouter, mettre à jour et supprimer les informations
 * des personnes enregistrées dans le système. Les informations des personnes
 * sont essentielles pour les services d'urgence car elles permettent de localiser
 * et contacter les habitants en cas de besoin.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;

    /**
     * Récupère la liste de toutes les personnes enregistrées dans le système.
     *
     * @return ResponseEntity contenant la liste des personnes
     */
    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons() {
        log.info("Récupération de toutes les personnes");
        List<Person> persons = personService.getAllPersons();
        return ResponseEntity.ok(persons);
    }

    /**
     * Crée un objet Person à partir d'un DTO.
     *
     * @param personDTO le DTO contenant les informations de la personne
     * @return un objet Person initialisé avec les données du DTO
     */
    private Person createPersonFromDTO(PersonInputDTO personDTO) {
        Person person = new Person();
        person.setFirstName(personDTO.getFirstName());
        person.setLastName(personDTO.getLastName());
        person.setAddress(personDTO.getAddress());
        person.setCity(personDTO.getCity());
        person.setZip(personDTO.getZip());
        person.setPhone(personDTO.getPhone());
        person.setEmail(personDTO.getEmail());
        return person;
    }

    /**
     * Ajoute une nouvelle personne dans le système.
     * Cette méthode crée un nouveau profil de personne à partir des informations
     * fournies dans le DTO et le persiste dans le système.
     *
     * @param personDTO DTO contenant les informations de la personne à créer
     * @return ResponseEntity contenant la personne créée avec le statut HTTP 201 (Created)
     * @throws IOException en cas d'erreur lors de la persistance des données
     */
    @PostMapping
    public ResponseEntity<Person> addPerson(@Valid @RequestBody PersonInputDTO personDTO) throws IOException {
        log.info("Ajout d'une nouvelle personne : {} {}", 
                personDTO.getFirstName(), personDTO.getLastName());

        Person person = createPersonFromDTO(personDTO);
        Person createdPerson = personService.addPerson(person);
        return new ResponseEntity<>(createdPerson, HttpStatus.CREATED);
    }

    /**
     * Met à jour une personne existante.
     * Cette méthode recherche une personne par prénom et nom, puis met à jour
     * ses informations avec les nouvelles données fournies dans le DTO.
     *
     * @param personDTO DTO contenant les nouvelles informations de la personne
     * @return ResponseEntity contenant la personne mise à jour
     * @throws IOException en cas d'erreur lors de la persistance des données
     * @throws ResourceNotFoundException si la personne n'est pas trouvée
     */
    @PutMapping
    public ResponseEntity<Person> updatePerson(@Valid @RequestBody PersonInputDTO personDTO) throws IOException {
        log.info("Mise à jour de la personne : {} {}", 
                personDTO.getFirstName(), personDTO.getLastName());

        Person person = createPersonFromDTO(personDTO);
        Person updatedPerson = personService.updatePerson(
                personDTO.getFirstName(),
                personDTO.getLastName(),
                person
        );

        if (updatedPerson == null) {
            throw new ResourceNotFoundException(
                String.format("Personne non trouvée pour mise à jour : %s %s",
                    personDTO.getFirstName(), personDTO.getLastName())
            );
        }

        return ResponseEntity.ok(updatedPerson);
    }

    /**
     * Supprime une personne existante.
     * Cette méthode recherche et supprime une personne identifiée par prénom et nom.
     *
     * @param firstName prénom de la personne à supprimer
     * @param lastName nom de la personne à supprimer
     * @return ResponseEntity sans contenu avec le statut HTTP 204 (No Content) en cas de succès
     * @throws IOException en cas d'erreur lors de la persistance des données
     * @throws ResourceNotFoundException si la personne n'est pas trouvée
     */
    @DeleteMapping
    public ResponseEntity<Void> deletePerson(
            @RequestParam String firstName,
            @RequestParam String lastName
    ) throws IOException {
        log.info("Suppression de la personne : {} {}", firstName, lastName);
        boolean deleted = personService.deletePerson(firstName, lastName);

        if (!deleted) {
            throw new ResourceNotFoundException(
                String.format("Personne non trouvée : %s %s", firstName, lastName)
            );
        }

        return ResponseEntity.noContent().build();
    }
}