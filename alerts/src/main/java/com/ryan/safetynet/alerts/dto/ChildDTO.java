package com.ryan.safetynet.alerts.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO représentant les informations d'un enfant (personne de 18 ans ou moins).
 * Inclut l'âge calculé à partir de la date de naissance.
 */
@Getter
@Setter
public class ChildDTO {
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @Min(value = 0, message = "L'âge ne peut pas être négatif")
    @Max(value = 18, message = "L'âge doit être inférieur ou égal à 18 ans")
    private int age;
}