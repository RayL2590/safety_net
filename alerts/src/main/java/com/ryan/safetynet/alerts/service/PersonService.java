package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final DataRepository dataRepository;

    private final Validator validator;

    @Autowired
    public PersonService(DataRepository dataRepository, Validator validator) {

        this.dataRepository = dataRepository;
        this.validator = validator;
    }

    /**
     * @return la liste des personnes
     */
    public List<Person> getAllPersons() {
        return dataRepository.getData().getPersons();
    }

    /**
     * @param firstName Prénom
     * @param lastName Nom
     * @return Un optional contenant la personne trouvée ou vide si la personne n'existe pas
     */
    public Optional<Person> findPersonByName(String firstName, String lastName) {
        return dataRepository.getData().getPersons().stream().filter(p -> p.getFirstName().equals(firstName) && p.getLastName().equals(lastName)).findFirst();
    }

    /**
     * @param person La personne à ajouter
     * @return la personne ajoutée
     */
    public Person addPerson(Person person) throws IOException {
        var violations = validator.validate(person);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Erreur de validation dans PersonService", violations);
        }
        dataRepository.getData().getPersons().add(person);
        dataRepository.saveData();
        return person;
    }

    /**
     * @param firstName Prénom
     * @param lastName Nom
     * @param person Les nouvelles informations de la personne
     * @return La personne mise à jour, ou null si la personne n'existe pas
     */
    public Person updatePerson(String firstName, String lastName, Person person) throws IOException {
        Optional<Person> existingPerson = findPersonByName(firstName, lastName);
        if (existingPerson.isPresent()) {
            Person p = existingPerson.get();
            p.setAddress(person.getAddress());
            p.setCity(person.getCity());
            p.setZip(person.getZip());
            p.setPhone(person.getPhone());
            p.setEmail(person.getEmail());
            dataRepository.saveData();
            return p;
        }
        return null;
    }

    /**
     *
     * @param firstName prénom
     * @param lastName nom
     * @return true si la personne a bien été supprimé, sinon false
     */
    public boolean deletePerson(String firstName, String lastName) throws IOException {
        boolean removed = dataRepository.getData().getPersons()
                .removeIf(p -> p.getFirstName().equals(firstName) && p.getLastName().equals(lastName));
        if (removed) {
            dataRepository.saveData(); // Persistance
        }
        return removed;
    }
}
