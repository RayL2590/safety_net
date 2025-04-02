package com.ryan.safetynet.alerts.utils;

import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.model.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Classe utilitaire pour la manipulation des dossiers médicaux.
 * Cette classe fournit des méthodes statiques pour accéder et manipuler
 * les informations des dossiers médicaux de manière centralisée.
 * Elle permet d'éviter la duplication de code dans les différents services
 * qui ont besoin d'accéder aux informations médicales.
 */
public class MedicalRecordUtils {

    /**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     */
    private MedicalRecordUtils() {}

    /**
     * Récupère la date de naissance d'une personne à partir de son prénom et nom.
     * Cette méthode recherche le dossier médical correspondant dans la liste fournie
     * et extrait la date de naissance. Si aucun dossier n'est trouvé, une exception
     * IllegalStateException est levée.
     *
     * @param firstName prénom de la personne
     * @param lastName nom de la personne
     * @param medicalRecords liste des dossiers médicaux à parcourir
     * @return la date de naissance de la personne
     * @throws IllegalStateException si aucun dossier médical n'est trouvé pour la personne
     */
    public static LocalDate getBirthdate(String firstName, String lastName, List<com.ryan.safetynet.alerts.model.MedicalRecord> medicalRecords) {
        return medicalRecords.stream()
                .filter(mr -> mr.getFirstName().equals(firstName) && mr.getLastName().equals(lastName))
                .map(com.ryan.safetynet.alerts.model.MedicalRecord::getBirthdate)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Dossier médical non trouvé pour " + firstName + " " + lastName));
    }

    /**
     * Extrait les informations médicales d'une personne et crée un DTO avec ces informations.
     *
     * @param person la personne dont on veut extraire les informations
     * @param medicalRecords la liste des dossiers médicaux
     * @return un DTO contenant les informations de la personne avec ses données médicales
     * @throws IllegalStateException si le dossier médical n'est pas trouvé
     */
    public static PersonWithMedicalInfoDTO extractMedicalInfo(Person person, List<MedicalRecord> medicalRecords) {
        Optional<MedicalRecord> medicalRecordOpt = medicalRecords.stream()
                .filter(mr -> mr.getFirstName().equals(person.getFirstName()) &&
                        mr.getLastName().equals(person.getLastName()))
                .findFirst();

        if (medicalRecordOpt.isEmpty()) {
            throw new IllegalStateException("Dossier médical non trouvé pour " +
                    person.getFirstName() + " " + person.getLastName());
        }

        MedicalRecord medicalRecord = medicalRecordOpt.get();
        int age = AgeCalculator.calculateAge(medicalRecord.getBirthdate());

        PersonWithMedicalInfoDTO dto = new PersonWithMedicalInfoDTO();
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setPhone(person.getPhone());
        dto.setAge(age);
        dto.setMedications(medicalRecord.getMedications());
        dto.setAllergies(medicalRecord.getAllergies());

        return dto;
    }
}
