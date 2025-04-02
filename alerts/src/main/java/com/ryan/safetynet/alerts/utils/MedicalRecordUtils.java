package com.ryan.safetynet.alerts.utils;

import java.time.LocalDate;
import java.util.List;

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
}
