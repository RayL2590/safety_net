package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.PersonInfoDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import com.ryan.safetynet.alerts.utils.MedicalRecordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonInfoService {

    private final DataRepository dataRepository;

    /**
     * Récupère les informations d'une personne spécifique par son prénom et son nom de famille.
     *
     * @param firstName prénom de la personne
     * @param lastName nom de famille de la personne
     * @return les informations de la personne ou null si non trouvée
     */
    public PersonInfoDTO getPersonInfo(String firstName, String lastName) {
        log.debug("Recherche des informations pour {} {}", firstName, lastName);
        Data data = dataRepository.getData();

        Optional<Person> personOpt = data.getPersons().stream()
                .filter(person -> person.getFirstName().equalsIgnoreCase(firstName) &&
                        person.getLastName().equalsIgnoreCase(lastName))
                .findFirst();

        if (personOpt.isEmpty()) {
            log.warn("Aucune personne trouvée pour {} {}", firstName, lastName);
            return null;
        }

        Person person = personOpt.get();
        log.debug("Personne trouvée, extraction des informations médicales");
        PersonWithMedicalInfoDTO medicalInfo = MedicalRecordUtils.extractMedicalInfo(person, data.getMedicalRecords());

        PersonInfoDTO dto = new PersonInfoDTO();
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setAddress(person.getAddress());
        dto.setEmail(person.getEmail());
        dto.setAge(medicalInfo.getAge());
        dto.setMedications(medicalInfo.getMedications());
        dto.setAllergies(medicalInfo.getAllergies());

        log.debug("Informations complètes extraites pour {} {}", firstName, lastName);
        return dto;
    }

    /**
     * Récupère les informations de toutes les personnes portant un nom de famille spécifique.
     *
     * @param lastName nom de famille des personnes à rechercher
     * @return liste des informations des personnes trouvées
     */
    public List<PersonInfoDTO> getPersonsByLastName(String lastName) {
        log.debug("Recherche des personnes avec le nom de famille: {}", lastName);
        Data data = dataRepository.getData();

        List<PersonInfoDTO> persons = data.getPersons().stream()
                .filter(person -> person.getLastName().equalsIgnoreCase(lastName))
                .map(person -> {
                    log.debug("Traitement des informations pour {} {}", person.getFirstName(), person.getLastName());
                    PersonWithMedicalInfoDTO medicalInfo = MedicalRecordUtils.extractMedicalInfo(person, data.getMedicalRecords());
                    PersonInfoDTO dto = new PersonInfoDTO();
                    dto.setFirstName(person.getFirstName());
                    dto.setLastName(person.getLastName());
                    dto.setAddress(person.getAddress());
                    dto.setEmail(person.getEmail());
                    dto.setAge(medicalInfo.getAge());
                    dto.setMedications(medicalInfo.getMedications());
                    dto.setAllergies(medicalInfo.getAllergies());
                    return dto;
                })
                .collect(Collectors.toList());

        log.debug("Nombre de personnes trouvées pour le nom {}: {}", lastName, persons.size());
        return persons;
    }
}
