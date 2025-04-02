package com.ryan.safetynet.alerts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

/**
 * DTO pour la saisie des informations médicales d'une personne.
 * Ce DTO est utilisé pour valider et transférer les données lors de la création
 * ou de la mise à jour d'un dossier médical dans le système.
 * Il inclut les informations essentielles comme la date de naissance et les antécédents médicaux.
 */
public class MedicalRecordInputDTO {
    
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @NotBlank(message = "La date de naissance est obligatoire")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "La date de naissance doit être au format MM/dd/yyyy")
    private String birthdate;

    private List<String> medications;

    private List<String> allergies;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public List<String> getMedications() {
        return medications;
    }

    public void setMedications(List<String> medications) {
        this.medications = medications;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }
} 