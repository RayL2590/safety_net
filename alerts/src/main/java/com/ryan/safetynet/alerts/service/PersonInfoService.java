package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.PersonInfoDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import com.ryan.safetynet.alerts.utils.AgeCalculator;
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
        Optional<MedicalRecord> medicalRecordOpt = data.getMedicalRecords().stream()
                .filter(mr -> mr.getFirstName().equalsIgnoreCase(firstName) &&
                        mr.getLastName().equalsIgnoreCase(lastName))
                .findFirst();

        if (medicalRecordOpt.isEmpty()) {
            throw new IllegalStateException("Dossier médical non trouvé pour " +
                    firstName + " " + lastName);
        }

        MedicalRecord medicalRecord = medicalRecordOpt.get();
        int age = AgeCalculator.calculateAge(medicalRecord.getBirthdate());

        PersonInfoDTO dto = new PersonInfoDTO();
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setAddress(person.getAddress());
        dto.setEmail(person.getEmail());
        dto.setAge(age);
        dto.setMedications(medicalRecord.getMedications());
        dto.setAllergies(medicalRecord.getAllergies());

        return dto;
    }
}
