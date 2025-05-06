package com.ryan.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

/**
 * Modèle représentant un dossier médical d'une personne.
 * Cette classe contient les informations médicales essentielles d'une personne,
 * notamment sa date de naissance, ses médicaments et ses allergies.
 * Ces informations sont cruciales pour les services d'urgence en cas de besoin.
 */
@Setter
@Getter
public class MedicalRecord {
    /**
     * Prénom de la personne.
     * Ce champ est obligatoire et ne peut pas être vide.
     */
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;
    
    /**
     * Nom de la personne.
     * Ce champ est obligatoire et ne peut pas être vide.
     */
    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    /**
     * Date de naissance de la personne.
     * Cette date doit être dans le passé et est formatée selon le pattern MM/dd/yyyy.
     * Elle est utilisée pour calculer l'âge de la personne et déterminer si elle est un adulte ou un enfant.
     */
    @Past(message = "La date de naissance doit être dans le passé")
    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate birthdate;

    /**
     * Liste des médicaments pris par la personne.
     * Chaque médicament doit respecter le format défini par l'expression régulière.
     * Cette information est cruciale pour les services d'urgence pour éviter les interactions médicamenteuses.
     */
    private List<@Pattern(regexp = "^[a-zA-Z0-9-: ]+$", message = "Médicament invalide") String> medications;
    
    /**
     * Liste des allergies de la personne.
     * Chaque allergie doit respecter le format défini par l'expression régulière.
     * Cette information est vitale pour les services d'urgence pour éviter les réactions allergiques.
     */
    private List<@Pattern(regexp = "^[a-zA-Z0-9- ]+$", message = "Allergie invalide") String> allergies;

    /**
     * Retourne une représentation textuelle de l'objet MedicalRecord.
     * Cette méthode est utile pour le débogage et l'affichage des informations.
     *
     * @return une chaîne de caractères représentant l'objet MedicalRecord
     */
    @Override
    public String toString() {
        return "MedicalRecord{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthdate=" + birthdate +
                ", medications=" + medications +
                ", allergies=" + allergies +
                '}';
    }
}
