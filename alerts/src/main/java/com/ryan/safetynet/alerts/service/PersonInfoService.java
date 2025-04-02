package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.PersonInfoDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import com.ryan.safetynet.alerts.utils.MedicalRecordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonInfoService {

    private final DataRepository dataRepository;

    @Autowired
    public PersonInfoService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * Récupère les informations d'une personne spécifique par son prénom et son nom de famille.
     *
     * @param firstName prénom de la personne
     * @param lastName nom de famille de la personne
     * @return les informations de la personne ou null si non trouvée
     */
    public PersonInfoDTO getPersonInfo(String firstName, String lastName) {
        Data data = dataRepository.getData();

        Optional<Person> personOpt = data.getPersons().stream()
                .filter(person -> person.getFirstName().equalsIgnoreCase(firstName) &&
                        person.getLastName().equalsIgnoreCase(lastName))
                .findFirst();

        if (personOpt.isEmpty()) {
            return null;
        }

        Person person = personOpt.get();
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
    }
}
