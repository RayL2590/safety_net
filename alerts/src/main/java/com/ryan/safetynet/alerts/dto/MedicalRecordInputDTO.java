package com.ryan.safetynet.alerts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.List;

/**
 * DTO pour la saisie des informations médicales d'une personne.
 * Ce DTO est utilisé pour valider et transférer les données lors de la création
 * ou de la mise à jour d'un dossier médical dans le système.
 * Il inclut les informations essentielles comme la date de naissance et les antécédents médicaux.
 */
@Data
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
} 