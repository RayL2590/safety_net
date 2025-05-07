package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.exception.DuplicatePersonException;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {
    private final DataRepository dataRepository;
    private final Validator validator;

    /**
     * Récupère toutes les personnes groupées par adresse.
     * Cette méthode filtre les personnes par une liste d'adresses
     * et retourne une Map avec l'adresse comme clé et la liste
     * des personnes qui y habitent comme valeur.
     *
     * @param addresses Liste des adresses à filtrer
     * @return Map des personnes regroupées par adresse
     */
    public Map<String, List<Person>> getPersonsByAddresses(List<String> addresses) {
        log.debug("Recherche des personnes pour les adresses: {}", addresses);
        Map<String, List<Person>> result = dataRepository.getData().getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .collect(Collectors.groupingBy(Person::getAddress));
        log.debug("Nombre d'adresses trouvées: {}", result.size());
        return result;
    }

    /**
     * Récupère toutes les personnes habitant à une adresse spécifique.
     *
     * @param address L'adresse à filtrer
     * @return Liste des personnes habitant à cette adresse
     */
    public List<Person> getPersonsByAddress(String address) {
        log.debug("Recherche des personnes pour l'adresse: {}", address);
        List<Person> persons = dataRepository.getData().getPersons().stream()
                .filter(p -> p.getAddress().equals(address))
                .collect(Collectors.toList());
        log.debug("Nombre de personnes trouvées: {}", persons.size());
        return persons;
    }

    /**
     * @param firstName Prénom
     * @param lastName Nom
     * @return Un optional contenant la personne trouvée ou vide si la personne n'existe pas
     */
    public Optional<Person> findPersonByName(String firstName, String lastName) {
        log.debug("Recherche de la personne: {} {}", firstName, lastName);
        Optional<Person> person = dataRepository.getData().getPersons().stream()
                .filter(p -> p.getFirstName().equals(firstName) && p.getLastName().equals(lastName))
                .findFirst();
        if (person.isEmpty()) {
            log.warn("Personne non trouvée: {} {}", firstName, lastName);
        }
        return person;
    }

    /**
     * @param person La personne à ajouter
     * @return la personne ajoutée
     */
    public Person addPerson(Person person) throws IOException {
        log.debug("Tentative d'ajout d'une nouvelle personne: {} {}", person.getFirstName(), person.getLastName());
        var violations = validator.validate(person);
        if (!violations.isEmpty()) {
            log.error("Erreur de validation pour la personne {} {}: {}", 
                person.getFirstName(), person.getLastName(), violations);
            throw new ConstraintViolationException("Erreur de validation dans PersonService", violations);
        }
        // Vérification du doublon (prénom, nom, adresse)
        boolean exists = dataRepository.getData().getPersons().stream()
            .anyMatch(p -> p.getFirstName().equals(person.getFirstName())
                && p.getLastName().equals(person.getLastName())
                && p.getAddress().equals(person.getAddress()));
        if (exists) {
            log.warn("Doublon détecté pour {} {} à l'adresse {}", person.getFirstName(), person.getLastName(), person.getAddress());
            throw new DuplicatePersonException(person.getFirstName(), person.getLastName(), person.getAddress());
        }
        dataRepository.getData().getPersons().add(person);
        dataRepository.saveData();
        log.info("Personne ajoutée avec succès: {} {}", person.getFirstName(), person.getLastName());
        return person;
    }

    /**
     * @param firstName Prénom
     * @param lastName Nom
     * @param person Les nouvelles informations de la personne
     * @return La personne mise à jour, ou null si la personne n'existe pas
     */
    public Person updatePerson(String firstName, String lastName, Person person) throws IOException {
        log.debug("Tentative de mise à jour de la personne: {} {}", firstName, lastName);
        Optional<Person> existingPerson = findPersonByName(firstName, lastName);
        if (existingPerson.isPresent()) {
            Person p = existingPerson.get();
            p.setAddress(person.getAddress());
            p.setCity(person.getCity());
            p.setZip(person.getZip());
            p.setPhone(person.getPhone());
            p.setEmail(person.getEmail());
            dataRepository.saveData();
            log.info("Personne mise à jour avec succès: {} {}", firstName, lastName);
            return p;
        }
        log.warn("Tentative de mise à jour d'une personne inexistante: {} {}", firstName, lastName);
        return null;
    }

    /**
     *
     * @param firstName prénom
     * @param lastName nom
     * @return true si la personne a bien été supprimé, sinon false
     */
    public boolean deletePerson(String firstName, String lastName) throws IOException {
        log.debug("Tentative de suppression de la personne: {} {}", firstName, lastName);
        boolean removed = dataRepository.getData().getPersons()
                .removeIf(p -> p.getFirstName().equals(firstName) && p.getLastName().equals(lastName));
        if (removed) {
            dataRepository.saveData();
            log.info("Personne supprimée avec succès: {} {}", firstName, lastName);
        } else {
            log.warn("Tentative de suppression d'une personne inexistante: {} {}", firstName, lastName);
        }
        return removed;
    }

    /**
     * Récupère la liste de toutes les personnes enregistrées dans le système.
     *
     * @return Liste de toutes les personnes
     */
    public List<Person> getAllPersons() {
        log.debug("Récupération de toutes les personnes");
        List<Person> persons = dataRepository.getData().getPersons();
        log.debug("Nombre total de personnes: {}", persons.size());
        return persons;
    }
}
